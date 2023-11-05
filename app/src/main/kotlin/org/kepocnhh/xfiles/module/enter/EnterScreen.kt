package org.kepocnhh.xfiles.module.enter

import android.content.res.Configuration
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.kepocnhh.xfiles.App
import org.kepocnhh.xfiles.module.app.Colors
import org.kepocnhh.xfiles.module.enter.settings.SettingsScreen
import org.kepocnhh.xfiles.util.android.getDefaultVibrator
import org.kepocnhh.xfiles.util.android.vibrate
import org.kepocnhh.xfiles.util.compose.AnimatedFadeVisibility
import org.kepocnhh.xfiles.util.compose.AnimatedHOpen
import org.kepocnhh.xfiles.util.compose.AnimatedHVisibility
import org.kepocnhh.xfiles.util.compose.ClickableText
import org.kepocnhh.xfiles.util.compose.PinPad
import org.kepocnhh.xfiles.util.compose.Squares
import org.kepocnhh.xfiles.util.compose.requireLayoutDirection
import org.kepocnhh.xfiles.util.compose.screenHeight
import org.kepocnhh.xfiles.util.compose.screenWidth
import org.kepocnhh.xfiles.util.compose.toPaddings
import org.kepocnhh.xfiles.util.compose.verticalPaddings
import org.kepocnhh.xfiles.util.ct
import sp.ax.jc.animations.style.SlideStyle
import sp.ax.jc.animations.tween.fade.FadeVisibility
import sp.ax.jc.animations.tween.slide.SlideHVisibility
import sp.ax.jc.dialogs.Dialog
import javax.crypto.SecretKey
import kotlin.math.absoluteValue
import kotlin.time.Duration.Companion.seconds

internal object EnterScreen {
    sealed interface Broadcast {
        class Unlock(val key: SecretKey) : Broadcast
    }

    enum class Error {
        UNLOCK,
        SECURITY,
    }
}

@Composable
internal fun EnterScreen(onBack: () -> Unit, broadcast: (EnterScreen.Broadcast) -> Unit) {
    val viewModel = App.viewModel<EnterViewModel>()
    val exists by viewModel.exists.collectAsState()
    val pinState = rememberSaveable { mutableStateOf("") }
    val errorState = rememberSaveable { mutableStateOf<EnterScreen.Error?>(null) }
    val deleteDialogState = remember { mutableStateOf(false) }
    val settingsState = remember { mutableStateOf(false) }
    val context = LocalContext.current
    if (deleteDialogState.value) {
        Dialog(
            App.Theme.strings.yes to {
                viewModel.deleteFile()
                pinState.value = ""
                deleteDialogState.value = false
            },
            message = App.Theme.strings.dialogs.databaseDelete,
            onDismissRequest = { deleteDialogState.value = false },
        )
    }
    LaunchedEffect(Unit) {
        if (exists == null) {
            viewModel.requestFile()
        }
    }
    LaunchedEffect(pinState.value) {
        if (pinState.value.length == 4) {
            when (exists) {
                true -> {
                    viewModel.unlockFile(pinState.value)
                }
                false -> {
                    viewModel.createNewFile(pinState.value)
                }
                null -> {
                    // noop
                }
            }
        }
    }
    val durations = App.Theme.durations
    LaunchedEffect(Unit) {
        viewModel.broadcast.collect { broadcast ->
            when (broadcast) {
                is EnterViewModel.Broadcast.OnUnlock -> {
                    broadcast(EnterScreen.Broadcast.Unlock(broadcast.key))
                }
                EnterViewModel.Broadcast.OnUnlockError -> {
                    errorState.value = EnterScreen.Error.UNLOCK
                    context.getDefaultVibrator().vibrate(duration = durations.animation)
                }
                EnterViewModel.Broadcast.OnSecurityError -> {
                    errorState.value = EnterScreen.Error.SECURITY
                }
            }
        }
    }
    val duration = App.Theme.durations.animation * 2
    if (errorState.value == EnterScreen.Error.SECURITY) {
        Dialog(
            // todo
            "ok" to {
                onBack()
            },
            message = "foo bar", // todo
            onDismissRequest = {
                               // todo
            },
        )
    }
    LaunchedEffect(errorState.value) {
        when (errorState.value) {
            EnterScreen.Error.UNLOCK -> {
                withContext(Dispatchers.Default) {
                    delay(duration)
                }
                pinState.value = ""
                errorState.value = null
            }
            EnterScreen.Error.SECURITY -> {
                // noop
            }
            null -> {
                // noop
            }
        }
    }
    // todo orientation
//    when (LocalConfiguration.current.orientation) {
    when (App.Theme.orientation) {
        App.Orientation.LANDSCAPE -> {
            EnterScreenLandscape(
                exists = exists,
                errorState = errorState,
                pinState = pinState,
                deleteDialogState = deleteDialogState,
                settingsState = settingsState,
            )
        }
        App.Orientation.PORTRAIT -> {
            EnterScreenPortrait(
                exists = exists,
                errorState = errorState,
                pinState = pinState,
                deleteDialogState = deleteDialogState,
                settingsState = settingsState,
            )
        }
    }
    val orientation = LocalConfiguration.current.orientation
    val layoutDirection = LocalConfiguration.current.requireLayoutDirection()
    val insets = LocalView.current.rootWindowInsets.toPaddings()
    val width = LocalConfiguration.current.screenWidth(insets)
    val targetWidth = when (orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            LocalConfiguration.current.screenHeight(insets) + insets.calculateEndPadding(layoutDirection)
        }
        Configuration.ORIENTATION_PORTRAIT -> width
        else -> TODO()
    }
    val colorShadow = when (orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> Colors.black.copy(alpha = 0.75f)
        Configuration.ORIENTATION_PORTRAIT -> Colors.black
        else -> TODO()
    }
    AnimatedHOpen(
        visible = settingsState.value,
        width = width,
        targetWidth = targetWidth,
        duration = App.Theme.durations.animation,
        colorShadow = colorShadow,
        onShadow = {
            settingsState.value = false
        },
    ) {
        SettingsScreen(
            onBack = {
                settingsState.value = false
            },
        )
    }
}

@Composable
private fun EnterScreenInfo(
    modifier: Modifier,
    exists: Boolean?,
    errorState: MutableState<EnterScreen.Error?>,
    deleteDialogState: MutableState<Boolean>,
    pinState: MutableState<String>,
) {
    Box(modifier = modifier) {
        val modifier2 = Modifier
            .fillMaxWidth()
            .align(Alignment.Center)
            .padding(
                start = App.Theme.sizes.small,
                end = App.Theme.sizes.small,
            )
        val initialOffsetX: (fullWidth: Int) -> Int = when (val orientation = LocalConfiguration.current.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> { it -> -it }
            Configuration.ORIENTATION_PORTRAIT -> { it -> it }
            else -> error("Orientation $orientation is not supported!")
        }
        AnimatedHVisibility(
            modifier = modifier2,
            visible = exists == true,
            duration = App.Theme.durations.animation,
            initialOffsetX = initialOffsetX,
        ) {
            Column {
                val textStyle = TextStyle(
                    color = App.Theme.colors.foreground,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                )
                BasicText(
                    modifier = Modifier.fillMaxWidth(),
                    style = textStyle,
                    text = App.Theme.strings.databaseExists,
                )
                Spacer(modifier = Modifier.height(App.Theme.sizes.small))
                val tag = "databaseDelete"
                ClickableText(
                    modifier = Modifier.fillMaxWidth(),
                    text = App.Theme.strings.databaseDelete(tag),
                    style = textStyle,
                    styles = mapOf(tag to TextStyle(App.Theme.colors.primary)),
                    onClick = {
                        when (it) {
                            tag -> {
                                deleteDialogState.value = true
                            }
                        }
                    },
                )
            }
        }
        // todo fade
        SlideHVisibility(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(vertical = App.Theme.sizes.small),
            visible = exists == false,
        ) {
            BasicText(
                style = TextStyle(
                    color = App.Theme.colors.foreground,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                ),
                text = App.Theme.strings.noDatabase,
            )
        }
        FadeVisibility(
            modifier = Modifier.align(Alignment.Center),
            visible = exists == null,
        ) {
            Squares(
                color = App.Theme.colors.foreground,
                width = App.Theme.sizes.large,
                padding = App.Theme.sizes.small,
                radius = App.Theme.sizes.xs,
            )
        }
        val maxOffset = 16.dp
        val offsetState = remember { mutableStateOf(maxOffset / 2) }
        LaunchedEffect(offsetState.value, errorState.value) {
            when (errorState.value) {
                EnterScreen.Error.UNLOCK -> {
                    withContext(Dispatchers.Default) {
                        delay(16)
                    }
                    offsetState.value = (offsetState.value + 3.dp).ct(maxOffset)
                }
                EnterScreen.Error.SECURITY -> {
                    // noop
                }
                null -> {
                    offsetState.value = maxOffset / 2
                }
            }
        }
        BasicText(
            modifier = Modifier
                .padding(
                    bottom = App.Theme.sizes.large,
                    top = App.Theme.sizes.large,
                )
                .align(Alignment.BottomCenter)
                .offset(x = (offsetState.value - maxOffset / 2).value.absoluteValue.dp),
            text = "*".repeat(pinState.value.length),
            style = TextStyle(
                color = if (errorState.value == EnterScreen.Error.UNLOCK) App.Theme.colors.error else App.Theme.colors.foreground,
                fontFamily = FontFamily.Monospace,
                fontSize = 24.sp,
            )
        )
    }
}

@Composable
private fun EnterScreenLandscape(
    exists: Boolean?,
    errorState: MutableState<EnterScreen.Error?>,
    pinState: MutableState<String>,
    deleteDialogState: MutableState<Boolean>,
    settingsState: MutableState<Boolean>,
) {
    val insets = LocalView.current.rootWindowInsets.toPaddings()
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
                    top = insets.calculateTopPadding(),
                    end = insets.calculateEndPadding(layoutDirection),
                ),
        ) {
            EnterScreenInfo(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                exists = exists,
                errorState = errorState,
                pinState = pinState,
                deleteDialogState = deleteDialogState,
            )
            PinPad(
                modifier = Modifier
                    .width(parent.maxHeight)
                    .align(Alignment.CenterVertically),
                enabled = exists != null && errorState.value == null,
                visibleDelete = pinState.value.isNotEmpty(),
                rowHeight = 64.dp,
                textStyle = TextStyle(
                    textAlign = TextAlign.Center,
                    color = App.Theme.colors.foreground,
                    fontSize = 24.sp,
                ),
                onClick = { char ->
                    pinState.value += char
                },
                onDelete = {
                    pinState.value = ""
                },
                onSettings = {
                    settingsState.value = true
                }
            )
        }
    }
}

@Composable
private fun EnterScreenPortrait(
    exists: Boolean?,
    errorState: MutableState<EnterScreen.Error?>,
    pinState: MutableState<String>,
    deleteDialogState: MutableState<Boolean>,
    settingsState: MutableState<Boolean>,
) {
    val insets = LocalView.current.rootWindowInsets.toPaddings()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(App.Theme.colors.background)
            .verticalPaddings(insets),
    ) {
        EnterScreenInfo(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            exists = exists,
            errorState = errorState,
            pinState = pinState,
            deleteDialogState = deleteDialogState,
        )
        PinPad(
            modifier = Modifier
                .fillMaxWidth(),
            enabled = exists != null && errorState.value == null,
            visibleDelete = pinState.value.isNotEmpty(),
            onDelete = {
                pinState.value = ""
            },
            onSettings = {
                settingsState.value = true
            },
            rowHeight = App.Theme.sizes.xxxl,
            textStyle = TextStyle(
                textAlign = TextAlign.Center,
                color = App.Theme.colors.foreground,
                fontSize = 24.sp,
            ),
            onClick = { char ->
                pinState.value += char
            },
        )
    }
}
