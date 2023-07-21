package org.kepocnhh.xfiles.module.unlocked

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.res.Configuration
import android.os.PersistableBundle
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import org.kepocnhh.xfiles.App
import org.kepocnhh.xfiles.util.compose.AnimatedHVisibility
import sp.ax.jc.clicks.clicks
import sp.ax.jc.dialogs.Dialog
import javax.crypto.SecretKey
import kotlin.math.roundToInt

internal object UnlockedScreen {
    sealed interface Broadcast {
        object Lock : Broadcast
    }
}

@Composable
private fun Data(
    entries: Map<String, String>,
    onClick: (String) -> Unit,
    onLongClick: (String) -> Unit,
) {
    LazyColumn {
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

@Composable
internal fun UnlockedScreen(
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
                viewModel.requestToCopy(key, name = clicked)
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
            TODO()
        }
        Configuration.ORIENTATION_PORTRAIT -> {
            UnlockedScreenPortrait(
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
    AnimatedHVisibility(
        visible = addedState.value,
        duration = App.Theme.durations.animation,
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
private fun UnlockedScreenPortrait(
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
