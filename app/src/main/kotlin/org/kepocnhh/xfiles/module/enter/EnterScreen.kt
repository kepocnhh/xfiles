package org.kepocnhh.xfiles.module.enter

import android.content.res.Configuration
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.kepocnhh.xfiles.App
import org.kepocnhh.xfiles.module.app.Colors
import org.kepocnhh.xfiles.module.app.Strings
import org.kepocnhh.xfiles.util.android.showToast
import org.kepocnhh.xfiles.util.compose.PinPad
import org.kepocnhh.xfiles.util.compose.append
import sp.ax.jc.dialogs.Dialog
import javax.crypto.SecretKey

internal object EnterScreen {
    sealed interface Broadcast {
        class Unlock(val key: SecretKey) : Broadcast
    }
}

@Composable
internal fun EnterScreen(broadcast: (EnterScreen.Broadcast) -> Unit) {
    val context = LocalContext.current
    val viewModel = App.viewModel<EnterViewModel>()
    val exists by viewModel.exists.collectAsState(null)
    val pinState = rememberSaveable { mutableStateOf("") }
    val deleteDialogState = remember { mutableStateOf(false) }
    if (deleteDialogState.value) {
        Dialog(
            App.Theme.strings.yes to {
                viewModel.deleteFile()
                deleteDialogState.value = false
            },
            message = "delete?",
            onDismissRequest = { deleteDialogState.value = false }
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
                    pinState.value = ""
                    context.showToast("on unlock error...")
                    // todo
                }
            }
        }
    }
    when (LocalConfiguration.current.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            EnterScreenLandscape(
                exists = exists,
                pinState = pinState,
                deleteDialogState = deleteDialogState,
            )
        }
        else -> {
            EnterScreenPortrait(
                exists = exists,
                pinState = pinState,
                deleteDialogState = deleteDialogState,
            )
        }
    }
}

@Composable
private fun EnterScreenLandscape(
    exists: Boolean?,
    pinState: MutableState<String>,
    deleteDialogState: MutableState<Boolean>,
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
            Column(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically),
            ) {
                val text = when (exists) {
                    true -> "exists"
                    false -> "does not exist"
                    null -> "loading..."
                }
                BasicText(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                    text = text,
                )
                BasicText(
                    modifier = Modifier
                        .padding(
                            bottom = 32.dp,
                            top = 32.dp,
                        )
                        .align(Alignment.CenterHorizontally),
                    text = "*".repeat(pinState.value.length),
                    style = TextStyle(
                        color = App.Theme.colors.foreground,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 24.sp,
                    )
                )
            }
            PinPad(
                modifier = Modifier
                    .width(parent.maxHeight)
                    .align(Alignment.CenterVertically),
                enabled = exists != null,
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
                onDeleteLong = {
                    deleteDialogState.value = true
                }
            )
        }
    }
}

@Composable
private fun EnterScreenPortrait(
    exists: Boolean?,
    pinState: MutableState<String>,
    deleteDialogState: MutableState<Boolean>,
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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            val modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(
                    start = App.Theme.sizes.s,
                    end = App.Theme.sizes.s,
                )
            when (exists) {
                true -> {
                    // todo
                    val text = buildAnnotatedString {
                        val annotated = App.Theme.strings.databaseExists
                        annotated.texts.forEachIndexed { index, text ->
                            val tag = annotated.tags[index]
                            if (tag == null) {
                                append(
                                    color = App.Theme.colors.foreground,
                                    fontSize = 16.sp,
                                    text = text,
                                )
                            } else {
                                append(
                                    tag = tag,
                                    annotation = "", // todo
                                    color = Colors.primary,
                                    fontSize = 16.sp,
                                    text = text,
                                )
                            }
                        }
                    }
                    ClickableText(
                        modifier = modifier,
                        text = text,
                        style = TextStyle(textAlign = TextAlign.Center),
                        onClick = { offset ->
                            when (text.getStringAnnotations(offset, offset).takeIf { it.size == 1 }?.single()?.tag) {
                                Strings.Tags.DELETE -> {
                                    deleteDialogState.value = true
                                }
                            }
                        },
                    )
                }
                false -> {
                    BasicText(
                        modifier = modifier,
                        style = TextStyle(
                            color = App.Theme.colors.foreground,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                        ),
                        text = "There is no database yet. Enter the pin code to create a new secure database.", // todo
                    )
                }
                null -> {
                    BasicText(
                        modifier = Modifier.align(Alignment.Center),
                        text = "loading...", // todo
                    )
                }
            }
        }
        BasicText(
            modifier = Modifier
                .padding(
                    bottom = App.Theme.sizes.l,
                    top = App.Theme.sizes.l,
                )
                .align(Alignment.CenterHorizontally),
            text = "*".repeat(pinState.value.length),
            style = TextStyle(
                color = App.Theme.colors.foreground,
                fontFamily = FontFamily.Monospace,
                fontSize = 24.sp,
            )
        )
        PinPad(
            modifier = Modifier
                .fillMaxWidth(),
            enabled = exists != null,
            rowHeight = App.Theme.sizes.xxxl,
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
            onDeleteLong = {
                // todo
            }
        )
    }
}
