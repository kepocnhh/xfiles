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
import org.kepocnhh.xfiles.util.android.showToast
import org.kepocnhh.xfiles.util.compose.AnimatedHVisibility
import org.kepocnhh.xfiles.util.compose.AnimatedHVisibilityShadow
import org.kepocnhh.xfiles.util.compose.ColorIndication
import org.kepocnhh.xfiles.util.compose.requireLayoutDirection
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
    val encrypteds by viewModel.encrypteds.collectAsState(null)
    if (encrypteds == null) viewModel.requestValues(key)
    BackHandler {
        broadcast(UnlockedScreen.Broadcast.Lock)
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
                items = encrypteds,
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
}

@Composable
private fun ButtonsRow(
    modifier: Modifier,
    enabled: Boolean,
    onAdd: () -> Unit,
    onLock: () -> Unit,
) {
    Row(modifier = modifier) {
        Box(
            modifier = Modifier
                .size(App.Theme.sizes.xxxl)
                .background(App.Theme.colors.foreground, RoundedCornerShape(App.Theme.sizes.large))
                .clip(RoundedCornerShape(App.Theme.sizes.large))
                .clickable(
                    enabled = enabled,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ColorIndication(color = App.Theme.colors.background),
                    onClick = onAdd,
                ),
        ) {
            Image(
                modifier = Modifier
                    .size(App.Theme.sizes.medium)
                    .align(Alignment.Center),
                painter = painterResource(id = R.drawable.plus),
                contentDescription = "unlocked:add",
                colorFilter = ColorFilter.tint(App.Theme.colors.background),
            )
        }
        Spacer(modifier = Modifier.width(App.Theme.sizes.small))
        Box(
            modifier = Modifier
                .size(App.Theme.sizes.xxxl)
                .background(App.Theme.colors.foreground, RoundedCornerShape(App.Theme.sizes.large))
                .clip(RoundedCornerShape(App.Theme.sizes.large))
                .clickable(
                    enabled = enabled,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ColorIndication(color = App.Theme.colors.background),
                    onClick = onLock,
                ),
        ) {
            Image(
                modifier = Modifier
                    .size(App.Theme.sizes.medium)
                    .align(Alignment.Center),
                painter = painterResource(id = R.drawable.key),
                contentDescription = "unlocked:lock",
                colorFilter = ColorFilter.tint(App.Theme.colors.background),
            )
        }
    }
}

@Composable
private fun EncryptedValueButton(
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
            .onClick(onClick),
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
    value: EncryptedValue,
    onShow: () -> Unit,
    onCopy: () -> Unit,
    onDelete: () -> Unit,
) {
    val context = LocalContext.current
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
                size = buttonSize,
                icon = R.drawable.eye,
                contentDescription = "unlocked:item:${value.id}:show",
                onClick = onShow,
            )
            EncryptedValueButton(
                size = buttonSize,
                icon = R.drawable.copy,
                contentDescription = "unlocked:item:${value.id}:copy",
                onClick = onCopy,
            )
            EncryptedValueButton(
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
    items: List<EncryptedValue>,
    itemContent: @Composable (EncryptedValue) -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(App.Theme.sizes.small),
        contentPadding = PaddingValues(
            top = App.Theme.dimensions.insets.calculateTopPadding() + App.Theme.sizes.small,
            bottom = App.Theme.dimensions.insets.calculateBottomPadding() + App.Theme.sizes.small + App.Theme.sizes.small + App.Theme.sizes.xxxl,
        ),
    ) {
        items(
            count = items.size,
            key = { items[it].id },
        ) { index ->
            itemContent(items[index])
        }
    }
}

@Composable
private fun UnlockedScreenPortrait(
    items: List<EncryptedValue>?,
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
//        val items = (1..24).map {
//            EncryptedValue(id = "foo$it", title = "foo$it")
//        }
        when {
            items == null -> {
                // todo
            }
            items.isEmpty() -> {
                BasicText(
                    modifier = Modifier
                        .align(Alignment.Center),
                    text = "no items", // todo
                )
            }
            else -> {
                Encrypteds(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = App.Theme.dimensions.insets.calculateStartPadding(
                                layoutDirection
                            ),
                            end = App.Theme.dimensions.insets.calculateEndPadding(layoutDirection),
                        )
                        .align(Alignment.Center),
                    items = items,
                    itemContent = { item ->
                        EncryptedValueItem(
                            value = item,
                            onShow = {
                                context.showToast("show $item")
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
                    bottom = App.Theme.dimensions.insets.calculateBottomPadding() + App.Theme.sizes.small,
                    end = App.Theme.dimensions.insets.calculateEndPadding(layoutDirection) + App.Theme.sizes.small,
                )
                .align(Alignment.BottomEnd),
            enabled = items != null,
            onAdd = {
                val title = "foo${System.currentTimeMillis()}" // todo
                val value = "${System.nanoTime()}" // todo
                onAdd(title to value) // todo
            },
            onLock = onLock,
        )
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

@Deprecated(message = "!")
@Composable
private fun UnlockedScreenDeprecated(
    key: SecretKey,
    broadcast: (UnlockedScreen.Broadcast) -> Unit,
) {
    val context = LocalContext.current
    val viewModel = App.viewModel<UnlockedViewModel>()
    var secret by remember { mutableStateOf<String?>(null) }
    val clickedState = remember { mutableStateOf<String?>(null) }
    val addedState = remember { mutableStateOf(false) }
    val entries by viewModel.data.collectAsState(null)
    LaunchedEffect(Unit) {
        viewModel.broadcast.collect { broadcast ->
            when (broadcast) {
                is UnlockedViewModel.Broadcast.OnCopy -> {
                    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("secret", broadcast.secret)
                    clip.description.extras = PersistableBundle().also {
                        it.putBoolean("android.content.extra.IS_SENSITIVE", true)
                    }
                    clipboardManager.setPrimaryClip(clip)
                }
                is UnlockedViewModel.Broadcast.OnShow -> {
                    secret = broadcast.secret
                }
            }
        }
    }
    BackHandler {
        broadcast(UnlockedScreen.Broadcast.Lock)
    }
    if (secret != null) {
        Dialog(
            "ok" to {
                secret = null
            },
            onDismissRequest = {
                secret = null
            },
            message = "$secret",
        )
    }
    val clicked = clickedState.value
    if (clicked != null) {
        Dialog(
            "delete" to {
                viewModel.deleteData(key, name = clicked)
                clickedState.value = null
            },
            "copy" to {
//                viewModel.requestToCopy(key, name = clicked) // todo
                clickedState.value = null
            },
            onDismissRequest = {
                clickedState.value = null
            },
            message = "\"$clicked\"?", // todo
        )
    }
    when (val orientation = LocalConfiguration.current.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            UnlockedScreenLandscape(
                viewModel = viewModel,
                clickedState = clickedState,
                addedState = addedState,
                entries = entries,
                key = key,
                broadcast = broadcast,
            )
        }
        Configuration.ORIENTATION_PORTRAIT -> {
            UnlockedScreenPortraitDeprecated(
                viewModel = viewModel,
                clickedState = clickedState,
                addedState = addedState,
                entries = entries,
                key = key,
                broadcast = broadcast,
            )
        }
        else -> error("Orientation $orientation is not supported!")
    }
    AnimatedHVisibilityShadow(
        visible = addedState.value,
        duration = App.Theme.durations.animation,
//        duration = 2.seconds,
        initialOffsetX = { it },
    ) {
        AddItemScreen(
            keys = entries!!.keys,
            onCancel = {
                addedState.value = false
            },
            onAdd = { name, value ->
                viewModel.addData(key, name = name, value = value)
                addedState.value = false
            }
        )
    }
}

@Composable
private fun UnlockedScreenLandscape(
    viewModel: UnlockedViewModel,
    clickedState: MutableState<String?>,
    addedState: MutableState<Boolean>,
    entries: Map<String, String>?,
    key: SecretKey,
    broadcast: (UnlockedScreen.Broadcast) -> Unit,
) {
    val layoutDirection = when (val i = LocalConfiguration.current.layoutDirection) {
        View.LAYOUT_DIRECTION_LTR -> LayoutDirection.Ltr
        View.LAYOUT_DIRECTION_RTL -> LayoutDirection.Rtl
        else -> error("Layout direction $i is not supported!")
    }
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val parent = this
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(App.Theme.colors.background)
                .padding(
                    top = App.Theme.dimensions.insets.calculateTopPadding(),
                    end = App.Theme.dimensions.insets.calculateEndPadding(layoutDirection),
                ),
        ) {
            when (entries) {
                null -> {
                    Spacer(Modifier.width(parent.maxHeight)) // todo loading
                    viewModel.requestData(key)
                }
                else -> Data(
                    modifier = Modifier.width(parent.maxHeight),
                    entries = entries,
                    onClick = { name ->
                        clickedState.value = name
                    },
                    onLongClick = { name ->
                        viewModel.requestToShow(key, name = name)
                    },
                )
            }
            Row(
                modifier = Modifier
                    .align(Alignment.Bottom)
                    .weight(1f)
                    .height(64.dp)
            ) {
                BasicText(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .clickable { broadcast(UnlockedScreen.Broadcast.Lock) }
                        .wrapContentHeight(),
                    text = "lock",
                    style = TextStyle(
                        textAlign = TextAlign.Center,
                    ),
                )
                BasicText(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .clickable {
                            addedState.value = true
                        }
                        .wrapContentHeight(),
                    text = "add",
                    style = TextStyle(
                        textAlign = TextAlign.Center,
                    ),
                )
            }
        }
    }
}

@Deprecated(message = "!")
@Composable
private fun UnlockedScreenPortraitDeprecated(
    viewModel: UnlockedViewModel,
    clickedState: MutableState<String?>,
    addedState: MutableState<Boolean>,
    entries: Map<String, String>?,
    key: SecretKey,
    broadcast: (UnlockedScreen.Broadcast) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(App.Theme.colors.background)
            .padding(
                top = App.Theme.dimensions.insets.calculateTopPadding(),
                bottom = App.Theme.dimensions.insets.calculateBottomPadding(),
            ),
    ) {
        when (entries) {
            null -> viewModel.requestData(key)
            else -> Data(
                entries = entries,
                onClick = { name ->
                    clickedState.value = name
                },
                onLongClick = { name ->
                    viewModel.requestToShow(key, name = name)
                },
            )
        }
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(64.dp)
        ) {
            BasicText(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .clickable { broadcast(UnlockedScreen.Broadcast.Lock) }
                    .wrapContentHeight(),
                text = "lock",
                style = TextStyle(
                    textAlign = TextAlign.Center,
                ),
            )
            BasicText(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .clickable {
                        addedState.value = true
                    }
                    .wrapContentHeight(),
                text = "add",
                style = TextStyle(
                    textAlign = TextAlign.Center,
                ),
            )
        }
    }
}
