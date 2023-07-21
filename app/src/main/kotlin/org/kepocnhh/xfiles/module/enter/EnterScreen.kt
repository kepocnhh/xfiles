package org.kepocnhh.xfiles.module.enter

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.kepocnhh.xfiles.App
import org.kepocnhh.xfiles.util.android.showToast
import org.kepocnhh.xfiles.util.compose.PinPad
import sp.ax.jc.dialogs.Dialog
import javax.crypto.SecretKey

internal object EnterScreen {
    sealed interface Broadcast {
        class Unlock(val key: SecretKey) : Broadcast
    }
}

@Composable
internal fun EnterScreen(broadcast: (EnterScreen.Broadcast) -> Unit) {
    when (LocalConfiguration.current.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            EnterScreenLandscape(broadcast = broadcast)
        }
        else -> {
            EnterScreenPortrait(broadcast = broadcast)
        }
    }
}

@Composable
private fun EnterScreenLandscape(broadcast: (EnterScreen.Broadcast) -> Unit) {
    TODO()
}

@Composable
private fun EnterScreenPortrait(broadcast: (EnterScreen.Broadcast) -> Unit) {
    val context = LocalContext.current
    val viewModel = App.viewModel<EnterViewModel>()
    val exists by viewModel.exists.collectAsState(null)
    var pin by remember { mutableStateOf("") }
    var deleteDialog by remember { mutableStateOf(false) }
    if (deleteDialog) {
        Dialog(
            "ok" to {
                viewModel.deleteFile()
                deleteDialog = false
            },
            message = "delete?",
            onDismissRequest = { deleteDialog = false }
        )
    }
    LaunchedEffect(Unit) {
        if (exists == null) {
            viewModel.requestFile()
        }
    }
    LaunchedEffect(pin) {
        if (pin.length == 4) {
            when (exists) {
                true -> {
                    viewModel.unlockFile(pin)
                }
                false -> {
                    viewModel.createNewFile(pin)
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
                    pin = ""
                    context.showToast("on unlock error...")
                    // todo
                }
            }
        }
    }
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
            val text = when (exists) {
                true -> "exists"
                false -> "does not exist"
                null -> "loading..."
            }
            BasicText(
                modifier = Modifier
                    .align(Alignment.Center),
                text = text,
            )
            // todo
        }
        BasicText(
            modifier = Modifier
                .padding(
                    bottom = 32.dp,
                    top = 32.dp,
                )
                .align(Alignment.CenterHorizontally),
            text = "*".repeat(pin.length),
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
            rowHeight = 64.dp,
            textStyle = TextStyle(
                textAlign = TextAlign.Center,
                color = App.Theme.colors.foreground,
                fontSize = 24.sp,
            ),
            onClick = { char ->
                pin += char
            },
            onDelete = {
                pin = ""
            },
            onDeleteLong = {
                deleteDialog = true
            }
        )
    }
}
