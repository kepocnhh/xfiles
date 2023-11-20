package org.kepocnhh.xfiles.module.unlocked

import android.app.Activity
import android.content.ClipboardManager
import android.content.Context
import android.content.res.Configuration
import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.defaultMinSize
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
import androidx.compose.runtime.toMutableStateMap
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
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
import org.kepocnhh.xfiles.util.android.ClipDataUtil
import org.kepocnhh.xfiles.util.android.findActivity
import org.kepocnhh.xfiles.util.android.showToast
import org.kepocnhh.xfiles.util.compose.AnimatedFadeVisibility
import org.kepocnhh.xfiles.util.compose.ClickableText
import org.kepocnhh.xfiles.util.compose.ColorIndication
import org.kepocnhh.xfiles.util.compose.FloatingActionButton
import org.kepocnhh.xfiles.util.compose.Squares
import org.kepocnhh.xfiles.util.compose.append
import org.kepocnhh.xfiles.util.compose.requireLayoutDirection
import org.kepocnhh.xfiles.util.compose.toPaddings
import sp.ax.jc.animations.tween.fade.FadeVisibility
import sp.ax.jc.animations.tween.slide.SlideHVisibility
import sp.ax.jc.clicks.onClick
import sp.ax.jc.dialogs.Dialog
import javax.crypto.SecretKey
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
    onConfirm: (EncryptedValue) -> Unit,
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
        message = String.format(App.Theme.strings.unlocked.deleteItem, value.title),
    )
}

@Composable
internal fun ShowDialog(
    state: MutableState<String?>,
) {
    val value = state.value ?: return
    androidx.compose.ui.window.Dialog(
        onDismissRequest = {
            state.value = null
        },
    ) {
        Box(
            modifier = Modifier
                .defaultMinSize(minWidth = 280.dp, minHeight = Dp.Unspecified)
                .background(color = App.Theme.colors.background, shape = RoundedCornerShape(28.dp))
                .padding(PaddingValues(24.dp)),
        ) {
            val textStyle = if (value.length > 16) {
                TextStyle(
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 2.sp,
                )
            } else {
                TextStyle(
                    fontSize = 16.sp,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 4.sp,
                )
            }
            BasicText(
                modifier = Modifier.align(Alignment.Center),
                text = buildAnnotatedString {
                    value.toCharArray().forEach {
                        val color = if (Character.isDigit(it)) {
                            Colors.digits
                        } else if (Character.isUpperCase(it)) {
                            App.Theme.colors.capitals
                        } else if (Character.isLetter(it)) {
                            App.Theme.colors.text
                        } else {
                            Colors.signs
                        }
                        append(color, it)
                    }
                },
                minLines = 1,
                maxLines = 1,
                style = textStyle,
            )
        }
    }
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
    val logger = App.newLogger(tag = "[Unlocked]")
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
    val strings = App.Theme.strings
    LaunchedEffect(strings) {
        viewModel.broadcast.collect { broadcast ->
            logger.debug("broadcast: $broadcast")
            when (broadcast) {
                is UnlockedViewModel.Broadcast.OnCopy -> {
                    markStartState.value = TimeSource.Monotonic.markNow()
                    val clipboardManager = context.getSystemService(ClipboardManager::class.java)
                    if (clipboardManager != null) {
                        val clip = ClipDataUtil.newSecretText(
                            label = "secret",
                            text = broadcast.secret,
                        )
                        clipboardManager.setPrimaryClip(clip)
                        context.showToast(strings.unlocked.copied)
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
    height: Dp,
    onShow: () -> Unit,
    onCopy: () -> Unit,
    onDelete: () -> Unit,
) {
    Box(
        modifier = Modifier
            .padding(horizontal = App.Theme.sizes.small)
            .background(App.Theme.colors.secondary, RoundedCornerShape(App.Theme.sizes.large))
            .fillMaxWidth()
            .height(height),
    ) {
        val buttonSize = App.Theme.sizes.large
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = height / 2, end = (height - buttonSize) / 2),
            horizontalArrangement = Arrangement.spacedBy(App.Theme.sizes.xs),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BasicText(
                modifier = Modifier.weight(1f),
                text = value.title,
                style = App.Theme.textStyle,
                overflow = TextOverflow.Ellipsis,
                minLines = 1,
                maxLines = 1,
            )
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
    itemsPadding: Dp,
    itemsAlign: Alignment.Vertical,
    items: Map<String, String>,
    itemContent: @Composable (EncryptedValue) -> Unit,
) {
    val visibleMap = remember {
        items.map { (key, _) -> key to true }.toMutableStateMap()
    }
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(itemsPadding, itemsAlign),
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
            SlideHVisibility(visible = visibleMap[id] ?: false) {
                itemContent(EncryptedValue(id = id, title = title))
            }
            LaunchedEffect(id) {
                visibleMap[id] = true
            }
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
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(App.Theme.colors.background),
    ) {
        val constraintsScope: BoxWithConstraintsScope = this
        val layoutDirection = LocalConfiguration.current.requireLayoutDirection()
        val insets = LocalView.current.rootWindowInsets.toPaddings()
        FadeVisibility(
            modifier = Modifier
                .align(Alignment.Center),
            visible = !loading && encrypteds != null && encrypteds.isEmpty(),
        ) {
            val tag = "addItem"
            ClickableText(
                modifier = Modifier
                    .padding(horizontal = App.Theme.sizes.small),
                text = String.format(App.Theme.strings.unlocked.noItems, tag),
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
                val itemHeight = App.Theme.sizes.xxxl
                val itemsPadding = App.Theme.sizes.small
                val encryptedsHeight = itemHeight * encrypteds.size + itemsPadding * (encrypteds.size - 1)
                val verticalPadding = App.Theme.sizes.small
                val buttonsPadding = App.Theme.sizes.xxxl + App.Theme.sizes.small
                val bottomPadding = insets.calculateBottomPadding() + buttonsPadding + verticalPadding
                val contentPadding = when {
                    encryptedsHeight / 2 < constraintsScope.maxHeight / 2 - bottomPadding -> PaddingValues(
                        start = insets.calculateStartPadding(layoutDirection),
                        end = insets.calculateEndPadding(layoutDirection),
                    )
                    else -> PaddingValues(
                        start = insets.calculateStartPadding(layoutDirection),
                        top = insets.calculateTopPadding() + verticalPadding,
                        end = insets.calculateEndPadding(layoutDirection),
                        bottom = insets.calculateBottomPadding() + verticalPadding + buttonsPadding,
                    )
                }
                Encrypteds(
                    modifier = Modifier
                        .fillMaxSize(),
                    enabled = !loading,
                    contentPadding = contentPadding,
                    itemsAlign = Alignment.CenterVertically,
                    itemsPadding = itemsPadding,
                    items = encrypteds,
                    itemContent = { item ->
                        EncryptedValueItem(
                            enabled = !loading,
                            value = item,
                            height = itemHeight,
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
