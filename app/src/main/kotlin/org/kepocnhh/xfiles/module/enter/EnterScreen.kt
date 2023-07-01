package org.kepocnhh.xfiles.module.enter

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.kepocnhh.xfiles.util.android.showToast
import org.kepocnhh.xfiles.util.compose.PinPad
import sp.ax.jc.dialogs.Dialog
import kotlin.time.Duration.Companion.seconds

@Composable
private fun Top(
    exists: Boolean?,
    onDelete: () -> Unit,
) {
    val durationMillis = 250
    AnimatedVisibility(
        visible = exists == null,
        enter = fadeIn(tween(durationMillis)),
        exit = fadeOut(tween(durationMillis)),
    ) {
        Box(Modifier.fillMaxSize()) {
            println("loading...")
            BasicText(modifier = Modifier.align(Alignment.Center), text = "loading...")
        }
    }
    AnimatedVisibility(
        visible = exists == true,
        enter = slideInHorizontally(tween(durationMillis), initialOffsetX = { it }),
        exit = slideOutHorizontally(tween(durationMillis), targetOffsetX = { it }),
    ) {
        Box(Modifier.fillMaxSize()) {
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
        Box(Modifier.fillMaxSize()) {
            BasicText(modifier = Modifier.align(Alignment.Center), text = "none")
        }
    }
}

@Composable
internal fun EnterScreen() {
    val context = LocalContext.current
    val viewModel = viewModel<EnterViewModel>()
    val pin = remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        viewModel.broadcast.collect { broadcast ->
            when (broadcast) {
                EnterViewModel.Broadcast.OnCreate -> {
                    pin.value = "" // todo
                    context.showToast("on create...")
                    // todo
                }
                EnterViewModel.Broadcast.OnUnlock -> {
                    context.showToast("on unlock...")
                    // todo
                }
                EnterViewModel.Broadcast.OnUnlockError -> {
                    pin.value = ""
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
                    viewModel.requestFile(context.cacheDir)
                }
            }
        }
        println("before: ${pin.value}")
        LaunchedEffect(pin.value) {
            println("after: ${pin.value}")
            if (pin.value.length == 4) {
                when (exists.value) {
                    true -> {
                        viewModel.unlockFile(context.cacheDir, pin.value)
                    }
                    false -> {
                        viewModel.createNewFile(context.cacheDir, pin.value)
                    }
                    null -> {
                        // noop
                    }
                }
            }
        }
        BasicText(modifier = Modifier
            .clickable { pin.value = "" }
            .padding(8.dp), text = "x")
        BasicText(text = pin.value)
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
                pin.value += char
            },
        )
        Spacer(modifier = Modifier.height(128.dp))
    }
}
