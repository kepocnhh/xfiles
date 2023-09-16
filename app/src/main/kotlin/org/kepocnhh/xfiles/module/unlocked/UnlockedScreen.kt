package org.kepocnhh.xfiles.module.unlocked

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.res.Configuration
import android.os.PersistableBundle
import android.view.View
import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.modifier.modifierLocalMapOf
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import org.kepocnhh.xfiles.App
import org.kepocnhh.xfiles.R
import org.kepocnhh.xfiles.entity.EncryptedValue
import org.kepocnhh.xfiles.module.app.Colors
import org.kepocnhh.xfiles.util.android.showToast
import org.kepocnhh.xfiles.util.compose.AnimatedFadeVisibility
import org.kepocnhh.xfiles.util.compose.AnimatedHVisibility
import org.kepocnhh.xfiles.util.compose.AnimatedHVisibilityShadow
import org.kepocnhh.xfiles.util.compose.ColorIndication
import org.kepocnhh.xfiles.util.compose.FloatingActionButton
import org.kepocnhh.xfiles.util.compose.Squares
import org.kepocnhh.xfiles.util.compose.requireLayoutDirection
import org.kepocnhh.xfiles.util.compose.toPaddings
import sp.ax.jc.clicks.clicks
import sp.ax.jc.clicks.onClick
import sp.ax.jc.dialogs.Dialog
import javax.crypto.SecretKey
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.seconds

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
internal fun UnlockedScreen(
    key: SecretKey,
    broadcast: (UnlockedScreen.Broadcast) -> Unit,
) {
    val context = LocalContext.current
    val viewModel = App.viewModel<UnlockedViewModel>()
    val logger = App.newLogger(tag = "[Unlocked|Screen]")
    val deleteState = remember { mutableStateOf<EncryptedValue?>(null) }
    DeletedDialog(
        state = deleteState,
        onConfirm = {
            viewModel.deleteValue(key, id = it.id)
        },
    )
    val loading by viewModel.loading.collectAsState(true)
    val encrypteds by viewModel.encrypteds.collectAsState(null)
    if (encrypteds == null) viewModel.requestValues(key)
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
                    val clip = ClipData.newPlainText("secret", broadcast.secret)
                    clip.description.extras = PersistableBundle().also {
                        it.putBoolean("android.content.extra.IS_SENSITIVE", true)
                    }
                    val clipboardManager = context.getSystemService(ClipboardManager::class.java)
                    if (clipboardManager != null) {
                        clipboardManager.setPrimaryClip(clip)
                        context.showToast("Copied.") // todo
                    }
                }
                is UnlockedViewModel.Broadcast.OnShow -> TODO()
            }
        }
    }
    when (val orientation = LocalConfiguration.current.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            TODO()
        }
        Configuration.ORIENTATION_PORTRAIT -> {
            UnlockedScreenPortrait(
                loading = loading,
                encrypteds = encrypteds,
                onCopy = {
                    viewModel.requestToCopy(key, id = it.id)
                },
                onAdd = { (title, value) ->
                    viewModel.addValue(key, title = title, value = value)
                },
                onDelete = {
                    deleteState.value = it
                },
                onLock = {
                    broadcast(UnlockedScreen.Broadcast.Lock)
                }
            )
        }
        else -> error("Orientation $orientation is not supported!")
    }
    DisposableEffect(Unit) {
        // todo
        logger.debug("init")
        onDispose {
            logger.debug("on dispose")
        }
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
            indication = ColorIndication(color = Colors.white),
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
    @DrawableRes icon: Int,
    contentDescription: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(size)
            .background(
                App.Theme.colors.background,
                RoundedCornerShape(size / 2),
            )
            .clip(RoundedCornerShape(size / 2))
            .onClick(enabled = enabled, onClick),
    ) {
        Image(
            modifier = Modifier
                .size(size / 2)
                .align(Alignment.Center),
            painter = painterResource(id = icon),
            contentDescription = contentDescription,
            colorFilter = ColorFilter.tint(App.Theme.colors.foreground),
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
            .fillMaxWidth()
            .height(height),
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(App.Theme.sizes.large))
                .background(App.Theme.colors.secondary)
//                .onClick {
//                    context.showToast("click ${value.title}") // todo
//                }
                .wrapContentHeight(),
        )
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
    items: Map<String, String>,
    itemContent: @Composable (EncryptedValue) -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(App.Theme.sizes.small),
        contentPadding = PaddingValues(
            top = App.Theme.dimensions.insets.calculateTopPadding() + App.Theme.sizes.small,
            bottom = App.Theme.dimensions.insets.calculateBottomPadding() + App.Theme.sizes.small + App.Theme.sizes.small + App.Theme.sizes.xxxl,
        ),
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
    onCopy: (EncryptedValue) -> Unit,
    onAdd: (Pair<String, String>) -> Unit,
    onDelete: (EncryptedValue) -> Unit,
    onLock: () -> Unit,
) {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(App.Theme.colors.background),
    ) {
        val layoutDirection = LocalConfiguration.current.requireLayoutDirection()
        when {
            encrypteds == null -> {
                // todo
            }
            encrypteds.isEmpty() -> {
                BasicText(
                    modifier = Modifier
                        .align(Alignment.Center),
                    text = "no items", // todo
                )
            }
            else -> {
                val insets = LocalView.current.rootWindowInsets.toPaddings()
                Encrypteds(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = insets.calculateStartPadding(layoutDirection),
                            end = insets.calculateEndPadding(layoutDirection),
                        )
                        .align(Alignment.Center),
                    enabled = !loading,
                    items = encrypteds,
                    itemContent = { item ->
                        EncryptedValueItem(
                            enabled = !loading,
                            value = item,
                            onShow = {
                                context.showToast("show $item") // todo
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
        val insets = LocalView.current.rootWindowInsets.toPaddings()
        ButtonsRow(
            modifier = Modifier
                .padding(
                    bottom = insets.calculateBottomPadding() + App.Theme.sizes.small,
                    end = insets.calculateEndPadding(layoutDirection) + App.Theme.sizes.small,
                )
                .align(Alignment.BottomEnd),
            enabled = encrypteds != null && !loading,
            onAdd = {
                val title = "foo${System.currentTimeMillis()}" // todo
                val value = "${System.nanoTime()}" // todo
                onAdd(title to value) // todo
            },
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
private fun Data(
    modifier: Modifier = Modifier,
    entries: Map<String, String>,
    onClick: (String) -> Unit,
    onLongClick: (String) -> Unit,
) {
    LazyColumn(modifier = modifier) {
        items(entries.keys.toList()) { name ->
            println("compose: $name")
            val value = entries[name] ?: TODO()
            val draggableState = remember { mutableStateOf(false) }
            val animatable = remember { Animatable(0f) }
            val offsetXState = remember { mutableStateOf(0f) }
            LaunchedEffect(draggableState.value) {
                if (draggableState.value) {
                    animatable.stop()
                } else {
                    animatable.snapTo(offsetXState.value)
                    animatable.animateTo(0f)
                }
            }
            if (!draggableState.value) {
                if (animatable.isRunning) {
                    offsetXState.value = animatable.value
                }
            }
            BasicText(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .offset { IntOffset(offsetXState.value.roundToInt() / 2, 0) }
                    .background(Color.Green)
                    .clicks(
                        onClick = {
                            onClick(name)
                        },
                        onLongClick = {
                            onLongClick(name)
                        }
                    )
                    .draggable(
                        orientation = Orientation.Horizontal,
                        state = rememberDraggableState { delta ->
                            offsetXState.value += delta
                        },
                        onDragStarted = {
                            draggableState.value = true
                        },
                        onDragStopped = {
                            draggableState.value = false
                        },
                    ),
                text = "$name: $value"
            )
        }
    }
}
