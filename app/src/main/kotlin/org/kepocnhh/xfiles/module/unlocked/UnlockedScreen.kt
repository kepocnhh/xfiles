package org.kepocnhh.xfiles.module.unlocked

import android.content.ClipData
import android.content.ClipboardManager
import android.content.res.Configuration
import android.os.PersistableBundle
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import org.kepocnhh.xfiles.App
import org.kepocnhh.xfiles.R
import org.kepocnhh.xfiles.entity.EncryptedValue
import org.kepocnhh.xfiles.module.app.Colors
import org.kepocnhh.xfiles.module.unlocked.items.AddItemScreen
import org.kepocnhh.xfiles.util.android.showToast
import org.kepocnhh.xfiles.util.compose.AnimatedFadeVisibility
import org.kepocnhh.xfiles.util.compose.AnimatedHVisibility
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
import javax.crypto.SecretKey

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
    val showState = remember { mutableStateOf<String?>(null) }
    ShowDialog(
        state = showState,
    )
    val addItemState = remember { mutableStateOf(false) }
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
        Spacer(modifier = Modifier.fillMaxSize().background(Colors.black))
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
