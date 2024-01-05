package org.kepocnhh.xfiles.module.enter

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.kepocnhh.xfiles.App
import org.kepocnhh.xfiles.module.app.Colors
import org.kepocnhh.xfiles.module.enter.settings.SettingsScreen
import org.kepocnhh.xfiles.provider.CipherDecrypt
import org.kepocnhh.xfiles.provider.CipherEncrypt
import org.kepocnhh.xfiles.util.android.BiometricUtil
import org.kepocnhh.xfiles.util.android.findActivity
import org.kepocnhh.xfiles.util.android.getDefaultVibrator
import org.kepocnhh.xfiles.util.android.showToast
import org.kepocnhh.xfiles.util.android.vibrate
import org.kepocnhh.xfiles.util.compose.AnimatedHOpen
import org.kepocnhh.xfiles.util.compose.AnimatedHVisibility
import org.kepocnhh.xfiles.util.compose.ClickableText
import org.kepocnhh.xfiles.util.compose.PinPad
import org.kepocnhh.xfiles.util.compose.Squares
import org.kepocnhh.xfiles.util.compose.append
import org.kepocnhh.xfiles.util.compose.screenWidth
import org.kepocnhh.xfiles.util.compose.toPaddings
import org.kepocnhh.xfiles.util.ct
import sp.ax.jc.animations.tween.fade.FadeVisibility
import sp.ax.jc.animations.tween.slide.SlideHVisibility
import sp.ax.jc.dialogs.Dialog
import javax.crypto.Cipher
import javax.crypto.SecretKey
import kotlin.math.absoluteValue

internal object EnterScreen {
    sealed interface Broadcast {
        class Unlock(val key: SecretKey) : Broadcast
    }

    enum class Error {
        UNLOCK,
    }
}

@Composable
private fun DeleteDialog(
    dialogState: MutableState<Boolean>,
    onConfirm: () -> Unit,
) {
    if (!dialogState.value) return
    Dialog(
        App.Theme.strings.yes to {
            onConfirm()
            dialogState.value = false
        },
        message = App.Theme.strings.dialogs.databaseDelete,
        onDismissRequest = { dialogState.value = false },
    )
}

private fun onPin(viewModel: EnterViewModel, pin: String, onBiometric: () -> Unit) {
    if (pin.length < 4) return
    val state = viewModel.state.value ?: return
    when {
        state.exists -> viewModel.unlockFile(pin = pin)
        state.hasBiometric -> onBiometric()
        else -> viewModel.createNewFile(pin = pin, encrypt = null)
    }
}

private fun onBiometric(viewModel: EnterViewModel, cipher: Cipher, pin: String) {
    val state = viewModel.state.value ?: error("No state!")
    if (state.exists) {
        viewModel.unlockFile(decrypt = CipherDecrypt(cipher))
    } else {
        viewModel.createNewFile(pin = pin, encrypt = CipherEncrypt(cipher))
    }
}

@Composable
private fun Init(viewModel: EnterViewModel, settingsRequested: Boolean) {
    LaunchedEffect(settingsRequested) {
        if (!settingsRequested) {
            viewModel.requestState()
        }
    }
    LaunchedEffect(Unit) {
        if (viewModel.state.value == null) {
            viewModel.requestState()
        }
    }
}

@Composable
private fun ToSettings(settingsRequested: Boolean, onBack: () -> Unit) {
    val insets = LocalView.current.rootWindowInsets.toPaddings()
    val width = LocalConfiguration.current.screenWidth(insets)
    AnimatedHOpen(
        visible = settingsRequested,
        width = width,
        targetWidth = width,
        duration = App.Theme.durations.animation,
        colorShadow = Colors.black,
        onShadow = onBack,
    ) {
        SettingsScreen(onBack = onBack)
    }
}

@Suppress("LongMethod", "CyclomaticComplexMethod")
@Composable
internal fun EnterScreen(broadcast: (EnterScreen.Broadcast) -> Unit) {
    val logger = App.newLogger("[Enter]")
    val viewModel = App.viewModel<EnterViewModel>()
    val pinState = rememberSaveable { mutableStateOf("") }
    val errorState = rememberSaveable { mutableStateOf<EnterScreen.Error?>(null) }
    val deleteDialogState = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    DeleteDialog(
        dialogState = deleteDialogState,
        onConfirm = {
            viewModel.deleteFile()
            coroutineScope.launch {
                withContext(App.contexts.default) {
                    BiometricUtil.deleteSecretKey()
                }
                pinState.value = ""
            }
        },
    )
    val settingsState = remember { mutableStateOf(false) }
    Init(viewModel, settingsRequested = settingsState.value)
    val state = viewModel.state.collectAsState().value
    val context = LocalContext.current
    val activity = context.findActivity<FragmentActivity>() ?: error("No activity!")
    val strings = App.Theme.strings
    LaunchedEffect(pinState.value) {
        onPin(
            viewModel = viewModel,
            pin = pinState.value,
            onBiometric = {
                logger.debug("has biometric...")
                BiometricUtil.authenticate(
                    activity = activity,
                    title = strings.biometric.promptTitle,
                )
            },
        )
    }
    val durations = App.Theme.durations
    LaunchedEffect(Unit) {
        viewModel.broadcast.collect { broadcast ->
            when (broadcast) {
                is EnterViewModel.Broadcast.OnUnlock -> {
                    broadcast(EnterScreen.Broadcast.Unlock(broadcast.key))
                }
                is EnterViewModel.Broadcast.OnUnlockError -> {
                    errorState.value = EnterScreen.Error.UNLOCK
                    context.getDefaultVibrator().vibrate(duration = durations.animation)
                }
                is EnterViewModel.Broadcast.OnBiometric -> {
                    logger.debug("on biometric...")
                    BiometricUtil.authenticate(
                        activity = activity,
                        title = strings.biometric.promptTitle,
                        iv = broadcast.iv,
                    )
                }
            }
        }
    }
    LaunchedEffect(strings) {
        BiometricUtil.broadcast.collect { broadcast ->
            when (broadcast) {
                is BiometricUtil.Broadcast.OnSucceeded -> {
                    logger.debug("on biometric succeeded...")
                    onBiometric(
                        viewModel = viewModel,
                        cipher = broadcast.cipher,
                        pin = pinState.value,
                    )
                }
                is BiometricUtil.Broadcast.OnError -> {
                    logger.debug("on biometric error...")
                    val exists = viewModel.state.value?.exists ?: error("No state!")
                    if (exists) {
                        when (broadcast.type) {
                            BiometricUtil.Broadcast.OnError.Type.USER_CANCELED -> {
                                viewModel.requestState()
                            }
                            BiometricUtil.Broadcast.OnError.Type.CAN_NOT_AUTHENTICATE -> {
                                viewModel.requestState()
                                context.showToast(strings.enter.cantAuthWithDC)
                            }
                            BiometricUtil.Broadcast.OnError.Type.UNRECOVERABLE_KEY -> {
                                viewModel.requestState()
                                context.showToast(strings.enter.unrecoverableDC)
                            }
                            null -> error("Unlock. On biometric unknown error!")
                        }
                    } else {
                        when (broadcast.type) {
                            BiometricUtil.Broadcast.OnError.Type.USER_CANCELED -> {
                                pinState.value = ""
                            }
                            BiometricUtil.Broadcast.OnError.Type.CAN_NOT_AUTHENTICATE -> {
                                pinState.value = ""
                                context.showToast(strings.enter.cantAuthWithDC)
                            }
                            else -> error("Create. On biometric unknown error: ${broadcast.type}")
                        }
                    }
                }
            }
        }
    }
    LaunchedEffect(errorState.value) {
        when (errorState.value) {
            EnterScreen.Error.UNLOCK -> {
                withContext(App.contexts.default) {
                    delay(durations.animation * 2)
                }
                pinState.value = ""
                errorState.value = null
            }
            null -> {
                // noop
            }
        }
    }
    EnterScreen(
        state = state,
        errorState = errorState,
        pinState = pinState,
        onDelete = {
            deleteDialogState.value = true
        },
        onSettings = {
            settingsState.value = true
        },
        onBiometric = {
            logger.debug("request biometric...")
            viewModel.requestBiometric()
        },
    )
    ToSettings(
        settingsRequested = settingsState.value,
        onBack = {
            settingsState.value = false
        },
    )
}

private fun buildPin(length: Int, color: Color): AnnotatedString {
    return buildAnnotatedString {
        val maxLength = 4
        check(length in 0..maxLength)
        if (length == maxLength) {
            append("*".repeat(maxLength))
        } else {
            for (i in 0 until maxLength) {
                if (i < length) {
                    append("*")
                } else {
                    append(color, "*")
                }
            }
        }
    }
}

@Composable
private fun DatabaseExists(onDelete: () -> Unit) {
    Column {
        val textStyle = TextStyle(
            color = App.Theme.colors.foreground,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
        )
        BasicText(
            modifier = Modifier
                .semantics {
                    contentDescription = "EnterScreen:databaseExists"
                }
                .fillMaxWidth(),
            style = textStyle,
            text = App.Theme.strings.databaseExists,
        )
        Spacer(modifier = Modifier.height(App.Theme.sizes.small))
        val tag = "databaseDelete"
        ClickableText(
            modifier = Modifier.fillMaxWidth(),
            text = String.format(App.Theme.strings.databaseDelete, tag),
            style = textStyle,
            styles = mapOf(tag to TextStyle(App.Theme.colors.primary)),
            onClick = {
                when (it) {
                    tag -> onDelete()
                }
            },
        )
    }
}

@Suppress("LongMethod")
@Composable
private fun EnterScreenInfo(
    modifier: Modifier,
    exists: Boolean?,
    errorState: MutableState<EnterScreen.Error?>,
    onDelete: () -> Unit,
    pinLength: Int,
) {
    Box(modifier = modifier) {
        val modifier2 = Modifier
            .fillMaxWidth()
            .align(Alignment.Center)
            .padding(horizontal = App.Theme.sizes.small)
        AnimatedHVisibility(
            modifier = modifier2,
            visible = exists == true,
            duration = App.Theme.durations.animation,
            initialOffsetX = { it },
        ) {
            DatabaseExists(onDelete = onDelete)
        }
        SlideHVisibility(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(horizontal = App.Theme.sizes.small),
            visible = exists == false,
        ) {
            BasicText(
                modifier = Modifier
                    .semantics {
                        contentDescription = "EnterScreen:noDatabase"
                    },
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
                    withContext(App.contexts.default) {
                        delay(16)
                    }
                    offsetState.value = (offsetState.value + 3.dp).ct(maxOffset)
                }
                null -> {
                    offsetState.value = maxOffset / 2
                }
            }
        }
        val color = when (errorState.value) {
            EnterScreen.Error.UNLOCK -> App.Theme.colors.error
            else -> App.Theme.colors.foreground
        }
        BasicText(
            modifier = Modifier
                .padding(vertical = App.Theme.sizes.large)
                .align(Alignment.BottomCenter)
                .offset(x = (offsetState.value - maxOffset / 2).value.absoluteValue.dp),
            text = buildPin(
                length = pinLength,
                color = App.Theme.colors.secondary,
            ),
            style = TextStyle(
                color = color,
                fontFamily = FontFamily.Monospace,
                fontSize = 24.sp,
                letterSpacing = 24.sp,
            ),
        )
    }
}

@Suppress("LongParameterList")
@Composable
internal fun EnterScreen(
    state: EnterViewModel.State?,
    errorState: MutableState<EnterScreen.Error?>,
    pinState: MutableState<String>,
    onDelete: () -> Unit,
    onSettings: () -> Unit,
    onBiometric: () -> Unit,
) {
    Column(
        modifier = Modifier
            .semantics {
                contentDescription = "EnterScreen"
                isTraversalGroup = true
            }
            .fillMaxSize()
            .background(App.Theme.colors.background)
            .padding(App.Theme.insets),
    ) {
        EnterScreenInfo(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            exists = if (state == null) null else if (state.loading) null else state.exists,
            errorState = errorState,
            pinLength = pinState.value.length,
            onDelete = onDelete,
        )
        PinPad(
            modifier = Modifier
                .fillMaxWidth(),
            enabled = state != null && !state.loading && errorState.value == null,
            visibleDelete = pinState.value.isNotEmpty(),
            rowHeight = App.Theme.sizes.xxxl,
            textStyle = TextStyle(
                textAlign = TextAlign.Center,
                color = App.Theme.colors.foreground,
                fontSize = 24.sp,
            ),
            hasBiometric = state?.hasBiometric ?: false,
            exists = state?.exists ?: false,
            listeners = PinPad.Listeners(
                onClick = { char ->
                    pinState.value += char
                },
                onBiometric = onBiometric,
                onDelete = {
                    pinState.value = ""
                },
                onSettings = onSettings,
            ),
        )
    }
}
