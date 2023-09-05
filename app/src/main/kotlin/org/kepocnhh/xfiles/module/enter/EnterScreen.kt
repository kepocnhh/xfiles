package org.kepocnhh.xfiles.module.enter

import android.content.res.Configuration
import android.view.View
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.substring
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.kepocnhh.xfiles.App
import org.kepocnhh.xfiles.module.app.Colors
import org.kepocnhh.xfiles.module.app.Strings
import org.kepocnhh.xfiles.util.android.showToast
import org.kepocnhh.xfiles.util.compose.AnimatedFadeVisibility
import org.kepocnhh.xfiles.util.compose.AnimatedHOpen
import org.kepocnhh.xfiles.util.compose.AnimatedHVisibility
import org.kepocnhh.xfiles.util.compose.AnimatedHVisibilityShadow
import org.kepocnhh.xfiles.util.compose.PinPad
import org.kepocnhh.xfiles.util.compose.append
import org.kepocnhh.xfiles.util.compose.ClickableText
import org.kepocnhh.xfiles.util.compose.Squares
import org.kepocnhh.xfiles.util.compose.requireLayoutDirection
import org.kepocnhh.xfiles.util.compose.screenHeight
import org.kepocnhh.xfiles.util.compose.screenWidth
import org.kepocnhh.xfiles.util.ct
import sp.ax.jc.dialogs.Dialog
import java.util.regex.Pattern
import javax.crypto.SecretKey
import kotlin.math.absoluteValue
import kotlin.time.Duration.Companion.seconds

internal object EnterScreen {
    sealed interface Broadcast {
        class Unlock(val key: SecretKey) : Broadcast
    }
}

@Composable
internal fun EnterScreen(broadcast: (EnterScreen.Broadcast) -> Unit) {
    val viewModel = App.viewModel<EnterViewModel>()
    val exists by viewModel.exists.collectAsState(null)
    val pinState = rememberSaveable { mutableStateOf("") }
    val errorState = rememberSaveable { mutableStateOf(false) }
    val deleteDialogState = remember { mutableStateOf(false) }
    val settingsState = remember { mutableStateOf(false) }
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
    LaunchedEffect(Unit) {
        viewModel.broadcast.collect { broadcast ->
            when (broadcast) {
                is EnterViewModel.Broadcast.OnUnlock -> {
                    broadcast(EnterScreen.Broadcast.Unlock(broadcast.key))
                }
                EnterViewModel.Broadcast.OnUnlockError -> {
                    errorState.value = true
                }
            }
        }
    }
    val duration = App.Theme.durations.animation * 2
    LaunchedEffect(errorState.value) {
        if (errorState.value) {
            withContext(Dispatchers.Default) {
                delay(duration)
            }
            pinState.value = ""
            errorState.value = false
        }
    }
    when (LocalConfiguration.current.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            EnterScreenLandscape(
                exists = exists,
                error = errorState.value,
                pinState = pinState,
                deleteDialogState = deleteDialogState,
                settingsState = settingsState,
            )
        }
        else -> {
            EnterScreenPortrait(
                exists = exists,
                error = errorState.value,
                pinState = pinState,
                deleteDialogState = deleteDialogState,
                settingsState = settingsState,
            )
        }
    }
    val orientation = LocalConfiguration.current.orientation
    val layoutDirection = LocalConfiguration.current.requireLayoutDirection()
    val width = LocalConfiguration.current.screenWidth(App.Theme.dimensions.insets)
    val targetWidth = when (orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            LocalConfiguration.current.screenHeight(App.Theme.dimensions.insets) + App.Theme.dimensions.insets.calculateEndPadding(layoutDirection)
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
    error: Boolean,
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
        AnimatedHVisibility(
            modifier = modifier2,
            visible = exists == true,
            duration = App.Theme.durations.animation,
            initialOffsetX = { it },
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
        AnimatedHVisibility(
            modifier = modifier2,
            visible = exists == false,
            duration = App.Theme.durations.animation,
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
        AnimatedFadeVisibility(
            modifier = Modifier.align(Alignment.Center),
            visible = exists == null,
            duration = App.Theme.durations.animation,
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
        LaunchedEffect(offsetState.value, error) {
            if (error) {
                withContext(Dispatchers.Default) {
                    delay(16)
                }
                offsetState.value = (offsetState.value + 3.dp).ct(maxOffset)
            } else {
                offsetState.value = maxOffset / 2
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
                color = if (error) App.Theme.colors.error else App.Theme.colors.foreground,
                fontFamily = FontFamily.Monospace,
                fontSize = 24.sp,
            )
        )
    }
}

@Composable
private fun EnterScreenLandscape(
    exists: Boolean?,
    error: Boolean,
    pinState: MutableState<String>,
    deleteDialogState: MutableState<Boolean>,
    settingsState: MutableState<Boolean>,
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
            EnterScreenInfo(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                exists = exists,
                error = error,
                pinState = pinState,
                deleteDialogState = deleteDialogState,
            )
            PinPad(
                modifier = Modifier
                    .width(parent.maxHeight)
                    .align(Alignment.CenterVertically),
                enabled = exists != null,
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
    error: Boolean,
    pinState: MutableState<String>,
    deleteDialogState: MutableState<Boolean>,
    settingsState: MutableState<Boolean>,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(App.Theme.colors.background)
            .padding(
                top = App.Theme.dimensions.insets.calculateTopPadding(),
                bottom = App.Theme.dimensions.insets.calculateBottomPadding(),
            ),
    ) {
        EnterScreenInfo(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            exists = exists,
            error = error,
            pinState = pinState,
            deleteDialogState = deleteDialogState,
        )
        PinPad(
            modifier = Modifier
                .fillMaxWidth(),
            enabled = exists != null && !error,
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
