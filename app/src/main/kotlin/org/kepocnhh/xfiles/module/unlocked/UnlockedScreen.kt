package org.kepocnhh.xfiles.module.unlocked

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.res.Configuration
import android.os.PersistableBundle
import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.kepocnhh.xfiles.App
import org.kepocnhh.xfiles.R
import org.kepocnhh.xfiles.entity.EncryptedValue
import org.kepocnhh.xfiles.module.app.Colors
import org.kepocnhh.xfiles.module.unlocked.items.AddItemScreen
import org.kepocnhh.xfiles.util.android.findActivity
import org.kepocnhh.xfiles.util.android.showToast
import org.kepocnhh.xfiles.util.compose.AnimatedFadeVisibility
import org.kepocnhh.xfiles.util.compose.ClickableText
import org.kepocnhh.xfiles.util.compose.ColorIndication
import org.kepocnhh.xfiles.util.compose.FloatingActionButton
import org.kepocnhh.xfiles.util.compose.Squares
import org.kepocnhh.xfiles.util.compose.requireLayoutDirection
import org.kepocnhh.xfiles.util.compose.screenHeight
import org.kepocnhh.xfiles.util.compose.screenWidth
import org.kepocnhh.xfiles.util.compose.toPaddings
import sp.ax.jc.animations.tween.fade.FadeVisibility
import sp.ax.jc.animations.tween.slide.SlideHVisibility
import sp.ax.jc.clicks.onClick
import sp.ax.jc.dialogs.Dialog
import java.util.concurrent.atomic.AtomicBoolean
import javax.crypto.SecretKey
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource

internal object UnlockedScreen {
    sealed interface Broadcast {
        object Lock : Broadcast
    }
}

@Composable
internal fun DeletedDialog(
    state: MutableState<EncryptedValue?>,
    onConfirm: (EncryptedValue) -> Unit
) {
    val value = state.value ?: return
    Dialog(
        App.Theme.strings.yes to {
            onConfirm(value)
            state.value = null
        },
        onDismissRequest = {
            state.value = null
        },
        message = "delete ${value.title}?", // todo
    )
}

@Composable
internal fun ShowDialog(
    state: MutableState<String?>,
) {
    val value = state.value ?: return
    Dialog(
        App.Theme.strings.yes to {
            state.value = null
        }, // todo
        onDismissRequest = {
            state.value = null
        },
        message = value, // todo
    )
}

private fun ClipboardManager.getPrimaryClipTextOrNull(): CharSequence? {
    if (!hasPrimaryClip()) return null
    val primaryClip = primaryClip ?: return null
    if (primaryClip.itemCount != 1) return null
    return primaryClip.getItemAt(0).text
}

private fun clearClipboardIfNeeded(context: Context, expected: Int?) {
    if (expected == null) return
    val clipboardManager = context.getSystemService(ClipboardManager::class.java) ?: return
    val actual = clipboardManager
        .getPrimaryClipTextOrNull()
        ?.hashCode()
    if (actual != null && actual != expected) return
    clipboardManager.text = ""
}

@Composable
internal fun UnlockedScreen(
    key: SecretKey,
    broadcast: (UnlockedScreen.Broadcast) -> Unit,
) {
    val logger = App.newLogger(tag = "[Unlocked|Screen]")
//    KeepScreenOn() // todo
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val lockedState = remember { mutableStateOf(false) }
    val markStartState = remember { mutableStateOf(TimeSource.Monotonic.markNow()) }
    val clipboardHashState = remember { mutableStateOf<Int?>(null) }
    val lockedTimeout = 60.seconds
    val clipboardTimeout = 30.seconds
    DisposableEffect(Unit) {
        scope.launch {
            withContext(Dispatchers.Default) {
                while (markStartState.value.elapsedNow() < lockedTimeout && !lockedState.value) {
                    delay(0.25.seconds)
                }
                if (!lockedState.value) {
                    logger.debug("locked by timeout")
                    broadcast(UnlockedScreen.Broadcast.Lock)
                }
            }
        }
        onDispose {
            lockedState.value = true
            clearClipboardIfNeeded(
                context = context,
                expected = clipboardHashState.value,
            )
        }
    }
    DisposableEffect(Unit) {
        context
            .findActivity<Activity>()
            ?.window
            ?.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        onDispose {
            context
                .findActivity<Activity>()
                ?.window
                ?.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }
    /*
    DisposableEffect(Unit) {
        context.notifyAndStartForeground<ObserverService>(
            id = ObserverService.TIMER_NOTIFICATION_ID,
            title = "unlocked",
        )
        onDispose {
            context.stopForeground<ObserverService>()
        }
    }
    */
    val viewModel = App.viewModel<UnlockedViewModel>()
    val deleteState = remember { mutableStateOf<EncryptedValue?>(null) }
    LaunchedEffect(deleteState.value) {
        markStartState.value = TimeSource.Monotonic.markNow()
    }
    DeletedDialog(
        state = deleteState,
        onConfirm = {
            viewModel.deleteValue(key, id = it.id)
        },
    )
    val showState = remember { mutableStateOf<String?>(null) }
    LaunchedEffect(showState.value) {
        if (showState.value != null) {
            markStartState.value = TimeSource.Monotonic.markNow()
        }
    }
    ShowDialog(
        state = showState,
    )
    val addItemState = remember { mutableStateOf(false) }
    LaunchedEffect(addItemState.value) {
        markStartState.value = TimeSource.Monotonic.markNow()
    }
    val loading by viewModel.loading.collectAsState(true)
    val encrypteds by viewModel.encrypteds.collectAsState(null)
    LaunchedEffect(encrypteds) {
        if (encrypteds == null) {
            viewModel.requestValues(key)
        }
    }
    BackHandler {
        if (!loading) {
            broadcast(UnlockedScreen.Broadcast.Lock)
        }
    }
    LaunchedEffect(Unit) {
        viewModel.broadcast.collect { broadcast ->
            logger.debug("broadcast: $broadcast")
            when (broadcast) {
                is UnlockedViewModel.Broadcast.OnCopy -> {
                    markStartState.value = TimeSource.Monotonic.markNow()
                    val clip = ClipData.newPlainText("secret", broadcast.secret)
                    clip.description.extras = PersistableBundle().also {
                        it.putBoolean("android.content.extra.IS_SENSITIVE", true)
                    }
                    val clipboardManager = context.getSystemService(ClipboardManager::class.java)
                    if (clipboardManager != null) {
                        clipboardManager.setPrimaryClip(clip)
                        context.showToast("Copied.") // todo
                        val markStart = TimeSource.Monotonic.markNow()
                        val expected = broadcast.secret.hashCode()
                        clipboardHashState.value = expected
                        scope.launch {
                            withContext(Dispatchers.Default) {
                                while (!lockedState.value) {
                                    // https://stackoverflow.com/a/59864381
                                    val actual = clipboardManager.getPrimaryClipTextOrNull()?.hashCode()
                                    when {
                                        actual != null && actual != expected -> break
                                        markStart.elapsedNow() < clipboardTimeout -> delay(0.25.seconds)
                                        else -> {
                                            clipboardManager.text = ""
                                            clipboardHashState.value = null
                                            logger.debug("clipboard cleared")
                                            break
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                is UnlockedViewModel.Broadcast.OnShow -> {
                    showState.value = broadcast.secret
                }
            }
        }
    }
    when (val orientation = LocalConfiguration.current.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            UnlockedScreenLandscape(
                loading = loading,
                encrypteds = encrypteds,
                onAdd = {
                    addItemState.value = true
                },
                onLock = {
                    broadcast(UnlockedScreen.Broadcast.Lock)
                },
                onShow = {
                    viewModel.requestToShow(key, id = it.id)
                },
                onCopy = {
                    viewModel.requestToCopy(key, id = it.id)
                },
                onDelete = {
                    deleteState.value = it
                },
            )
        }
        Configuration.ORIENTATION_PORTRAIT -> {
            UnlockedScreenPortrait(
                loading = loading,
                encrypteds = encrypteds,
                onShow = {
                    viewModel.requestToShow(key, id = it.id)
                },
                onCopy = {
                    viewModel.requestToCopy(key, id = it.id)
                },
                onAdd = {
                    addItemState.value = true
                },
                onDelete = {
                    deleteState.value = it
                },
                onLock = {
                    broadcast(UnlockedScreen.Broadcast.Lock)
                },
            )
        }
        else -> error("Orientation $orientation is not supported!")
    }
    // todo shadow
    FadeVisibility(
        visible = addItemState.value,
    ) {
        Spacer(modifier = Modifier
            .fillMaxSize()
            .background(Colors.black))
    }
    SlideHVisibility(
        visible = addItemState.value,
    ) {
        AddItemScreen(
            onAdd = { title, secret ->
                viewModel.addValue(key, title = title, secret = secret)
                addItemState.value = false
            },
            onCancel = {
                addItemState.value = false
            },
        )
    }
}

@Composable
private fun ButtonsRow(
    modifier: Modifier,
    enabled: Boolean,
    onAdd: () -> Unit,
    onLock: () -> Unit,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(App.Theme.sizes.small),
    ) {
        FloatingActionButton(
            color = App.Theme.colors.primary,
            enabled = enabled,
            indication = ColorIndication.create(Colors.white),
            icon = R.drawable.plus,
            iconColor = Colors.white,
            contentDescription = "unlocked:add",
            onClick = onAdd,
        )
        FloatingActionButton(
//            color = App.Theme.colors.foreground,
            enabled = enabled,
//            indication = ColorIndication(color = App.Theme.colors.background),
            icon = R.drawable.key,
//            iconColor = App.Theme.colors.background,
            contentDescription = "unlocked:lock",
            onClick = onLock,
        )
    }
}

@Composable
private fun EncryptedValueButton(
    enabled: Boolean,
    size: Dp,
    iconSize: Dp = size / 2,
    @DrawableRes icon: Int,
    contentDescription: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(size / 2))
            .onClick(enabled = enabled, onClick),
    ) {
        Image(
            modifier = Modifier
                .size(iconSize)
                .align(Alignment.Center),
            painter = painterResource(id = icon),
            contentDescription = contentDescription,
            colorFilter = ColorFilter.tint(App.Theme.colors.icon),
        )
    }
}

@Composable
private fun EncryptedValueItem(
    enabled: Boolean,
    value: EncryptedValue,
    onShow: () -> Unit,
    onCopy: () -> Unit,
    onDelete: () -> Unit,
) {
    val height = App.Theme.sizes.xxxl
    Box(
        modifier = Modifier
            .padding(
                start = App.Theme.sizes.small,
                end = App.Theme.sizes.small,
            )
            .background(App.Theme.colors.secondary, RoundedCornerShape(App.Theme.sizes.large))
            .fillMaxWidth()
            .height(height),
    ) {
        BasicText(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = height / 2),
            text = value.title,
            style = TextStyle(
                fontSize = 14.sp,
                color = App.Theme.colors.foreground,
            ),
        )
        val buttonSize = App.Theme.sizes.large
        Row(
            modifier = Modifier
                .padding(end = (height - buttonSize) / 2)
                .align(Alignment.CenterEnd),
            horizontalArrangement = Arrangement.spacedBy(App.Theme.sizes.xs),
        ) {
            EncryptedValueButton(
                enabled = enabled,
                size = buttonSize,
                icon = R.drawable.eye,
                contentDescription = "unlocked:item:${value.id}:show",
                onClick = onShow,
            )
            EncryptedValueButton(
                enabled = enabled,
                size = buttonSize,
                icon = R.drawable.copy,
                contentDescription = "unlocked:item:${value.id}:copy",
                onClick = onCopy,
            )
            EncryptedValueButton(
                enabled = enabled,
                size = buttonSize,
                icon = R.drawable.cross,
                contentDescription = "unlocked:item:${value.id}:delete",
                onClick = onDelete,
            )
        }
    }
}

@Composable
private fun Encrypteds(
    modifier: Modifier,
    enabled: Boolean,
    contentPadding: PaddingValues,
    items: Map<String, String>,
    itemContent: @Composable (EncryptedValue) -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(App.Theme.sizes.small),
        contentPadding = contentPadding,
        userScrollEnabled = enabled,
    ) {
        val keys = items.keys.toList()
        items(
            count = keys.size,
            key = { keys[it] },
        ) { index ->
            val id = keys[index]
            val title = items[id] ?: TODO()
            itemContent(EncryptedValue(id = id, title = title))
        }
    }
}

@Composable
private fun UnlockedScreenPortrait(
    loading: Boolean,
    encrypteds: Map<String, String>?,
    onShow: (EncryptedValue) -> Unit,
    onCopy: (EncryptedValue) -> Unit,
    onAdd: () -> Unit,
    onDelete: (EncryptedValue) -> Unit,
    onLock: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(App.Theme.colors.background),
    ) {
        val layoutDirection = LocalConfiguration.current.requireLayoutDirection()
        val insets = LocalView.current.rootWindowInsets.toPaddings()
        FadeVisibility(
            modifier = Modifier
                .align(Alignment.Center),
            visible = !loading && encrypteds != null && encrypteds.isEmpty(),
        ) {
            val text = "There are no entries yet.\nClick on [%s](+) to add a new one." // todo lang
            val tag = "addItem"
            ClickableText(
                modifier = Modifier
                    .padding(horizontal = App.Theme.sizes.small),
                text = String.format(text, tag),
                style = App.Theme.textStyle.copy(fontSize = 16.sp, textAlign = TextAlign.Center),
                styles = mapOf(tag to TextStyle(App.Theme.colors.primary)),
                onClick = {
                    when (it) {
                        tag -> onAdd()
                    }
                },
            )
        }
        when {
            encrypteds == null -> {
                // todo
            }
            encrypteds.isEmpty() -> {
                // noop
            }
            else -> {
                Encrypteds(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center),
                    enabled = !loading,
                    contentPadding = PaddingValues(
                        start = insets.calculateStartPadding(layoutDirection),
                        top = insets.calculateTopPadding() + App.Theme.sizes.small,
                        end = insets.calculateEndPadding(layoutDirection),
                        bottom = insets.calculateBottomPadding() + App.Theme.sizes.small + App.Theme.sizes.small + App.Theme.sizes.xxxl,
                    ),
                    items = encrypteds,
                    itemContent = { item ->
                        EncryptedValueItem(
                            enabled = !loading,
                            value = item,
                            onShow = {
                                onShow(item)
                            },
                            onCopy = {
                                onCopy(item)
                            },
                            onDelete = {
                                onDelete(item)
                            },
                        )
                    },
                )
            }
        }
        ButtonsRow(
            modifier = Modifier
                .padding(
                    bottom = insets.calculateBottomPadding() + App.Theme.sizes.small,
                    end = insets.calculateEndPadding(layoutDirection) + App.Theme.sizes.small,
                )
                .align(Alignment.BottomEnd),
            enabled = encrypteds != null && !loading,
            onAdd = onAdd,
            onLock = onLock,
        )
        AnimatedFadeVisibility(
            modifier = Modifier.align(Alignment.Center),
            visible = loading,
            duration = App.Theme.durations.animation,
        ) {
            Squares(
                color = App.Theme.colors.foreground,
                width = App.Theme.sizes.large,
                padding = App.Theme.sizes.small,
                radius = App.Theme.sizes.xs,
            )
        }
    }
}

@Composable
private fun UnlockedScreenLandscape(
    loading: Boolean,
    encrypteds: Map<String, String>?,
    onAdd: () -> Unit,
    onLock: () -> Unit,
    onShow: (EncryptedValue) -> Unit,
    onCopy: (EncryptedValue) -> Unit,
    onDelete: (EncryptedValue) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(App.Theme.colors.background),
    ) {
        val layoutDirection = LocalConfiguration.current.requireLayoutDirection()
        val insets = LocalView.current.rootWindowInsets.toPaddings()
        val width = LocalConfiguration.current.screenWidth(insets)
        val height = LocalConfiguration.current.screenHeight(insets)
        when {
            encrypteds == null -> {
                // todo
            }
            encrypteds.isEmpty() -> {
                BasicText(
                    modifier = Modifier.align(Alignment.Center),
                    text = "no items", // todo
                )
            }
            else -> {
                Encrypteds(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center),
                    enabled = !loading,
                    contentPadding = PaddingValues(
                        top = insets.calculateTopPadding() + App.Theme.sizes.small,
                        bottom = insets.calculateBottomPadding() + App.Theme.sizes.small,
                        end = width - height,
//                        end = width - (width / 3) * 2,
                    ),
                    items = encrypteds,
                    itemContent = { item ->
                        EncryptedValueItem(
                            enabled = !loading,
                            value = item,
                            onShow = {
                                onShow(item)
                            },
                            onCopy = {
                                onCopy(item)
                            },
                            onDelete = {
                                onDelete(item)
                            },
                        )
                    },
                )
            }
        }
        ButtonsRow(
            modifier = Modifier
                .padding(
                    bottom = insets.calculateBottomPadding() + App.Theme.sizes.small,
                    end = insets.calculateEndPadding(layoutDirection) + App.Theme.sizes.small,
                )
                .align(Alignment.BottomEnd),
            enabled = encrypteds != null && !loading,
            onAdd = onAdd,
            onLock = onLock,
        )
        AnimatedFadeVisibility(
            modifier = Modifier.align(Alignment.Center),
            visible = loading,
            duration = App.Theme.durations.animation,
        ) {
            Squares(
                color = App.Theme.colors.foreground,
                width = App.Theme.sizes.large,
                padding = App.Theme.sizes.small,
                radius = App.Theme.sizes.xs,
            )
        }
    }
}
