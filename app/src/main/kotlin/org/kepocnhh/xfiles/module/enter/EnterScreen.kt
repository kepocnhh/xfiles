package org.kepocnhh.xfiles.module.enter

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.kepocnhh.xfiles.App
import org.kepocnhh.xfiles.util.android.showToast
import org.kepocnhh.xfiles.util.compose.PinPad
import sp.ax.jc.dialogs.Dialog
import javax.crypto.SecretKey
import kotlin.time.Duration.Companion.seconds

@Composable
private fun Top(
    exists: Boolean?,
    onDelete: () -> Unit,
) {
    val durationMillis = 250
//    val durationMillis = 2_000
    AnimatedVisibility(
        visible = exists == null,
        enter = fadeIn(tween(durationMillis)),
        exit = fadeOut(tween(durationMillis)),
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Yellow)) {
            println("loading...")
            BasicText(modifier = Modifier.align(Alignment.Center), text = "loading...")
        }
    }
    AnimatedVisibility(
        visible = exists == true,
        enter = slideInHorizontally(tween(durationMillis), initialOffsetX = { it })
        + fadeIn(tween(durationMillis)),
        exit = slideOutHorizontally(tween(durationMillis), targetOffsetX = { it })
        + fadeOut(tween(durationMillis)),
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Red)) {
            Column(modifier = Modifier.align(Alignment.Center)) {
                BasicText(modifier = Modifier.padding(8.dp), text = "exists")
                BasicText(modifier = Modifier
                    .clickable { onDelete() }
                    .padding(8.dp), text = "delete")
            }
        }
    }
    AnimatedVisibility(
        visible = exists == false,
        enter = slideInHorizontally(tween(durationMillis), initialOffsetX = { -it }),
        exit = slideOutHorizontally(tween(durationMillis), targetOffsetX = { -it }),
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Green)) {
            BasicText(modifier = Modifier.align(Alignment.Center), text = "none")
        }
    }
}

internal object EnterScreen {
    sealed interface Broadcast {
        class Unlock(val key: SecretKey) : Broadcast
    }
}

@Composable
internal fun EnterScreen(broadcast: (EnterScreen.Broadcast) -> Unit) {
    when (LocalConfiguration.current.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            TODO("EnterScreen:LANDSCAPE")
        }
        else -> {
            EnterScreenPortrait(broadcast = broadcast)
        }
    }
}

@Composable
private fun EnterScreenPortrait(broadcast: (EnterScreen.Broadcast) -> Unit) {
    val context = LocalContext.current
    val viewModel = App.viewModel<EnterViewModel>()
    val exists by viewModel.exists.collectAsState(null)
    val pinState = remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        if (exists == null) {
            viewModel.requestFile()
        }
    }
    LaunchedEffect(pinState.value) {
        val pin = pinState.value
        if (pin.length == 4) {
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
                context.showToast("on delete long") // todo
            }
        )
    }
}

@Composable
private fun EnterScreenOld(broadcast: (EnterScreen.Broadcast) -> Unit) {
    val context = LocalContext.current
    val viewModel = viewModel<EnterViewModel>()
    val pinState = remember { mutableStateOf("") }
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
    val exists = viewModel.exists.collectAsState(null)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            val deleteDialog = remember { mutableStateOf(false) }
            Top(
                exists = exists.value,
                onDelete = {
                    deleteDialog.value = true
                }
            )
            if (deleteDialog.value) {
                Dialog(
                    "ok" to {
                        viewModel.deleteFile(context.cacheDir)
                        deleteDialog.value = false
                    },
                    message = "delete?",
                    onDismissRequest = { deleteDialog.value = false }
                )
            }
            println("exists: ${exists.value}")
            LaunchedEffect(Unit) {
                if (exists.value == null) {
                    viewModel.requestFile()
                }
            }
        }
        LaunchedEffect(pinState.value) {
            if (pinState.value.length == 4) {
//                when (exists.value) {
//                    true -> {
//                        viewModel.unlockFile(context.cacheDir, pinState.value)
//                    }
//                    false -> {
//                        viewModel.createNewFile(context.cacheDir, pinState.value)
//                    }
//                    null -> {
//                        // noop
//                    }
//                }
            }
        }
        BasicText(modifier = Modifier
            .clickable { pinState.value = "" }
            .padding(8.dp), text = "x")
        BasicText(text = pinState.value)
        PinPad(
            modifier = Modifier
                .fillMaxWidth(),
            rowHeight = 64.dp,
            textStyle = TextStyle(
                textAlign = TextAlign.Center,
                color = Color.Black,
                fontSize = 24.sp,
            ),
            onClick = { char ->
                pinState.value += char
            },
            onDelete = {
                // todo
            },
            onDeleteLong = {
                // todo
            },
        )
        Spacer(modifier = Modifier.height(128.dp))
    }
}
