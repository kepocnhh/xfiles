package org.kepocnhh.xfiles.module.unlocked

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import javax.crypto.SecretKey

internal object UnlockedScreen {
    sealed interface Broadcast {
        object Lock : Broadcast
    }
}

@Composable
private fun Data(values: Map<String, String>) {
    Column(verticalArrangement = Arrangement.Center) {
        values.forEach { (k, v) ->
            BasicText(text = "$k: $v")
        }
    }
}

@Composable
internal fun UnlockedScreen(
    key: SecretKey,
    broadcast: (UnlockedScreen.Broadcast) -> Unit,
) {
    BackHandler {
        broadcast(UnlockedScreen.Broadcast.Lock)
    }
    val context = LocalContext.current
    val viewModel = viewModel<UnlockedViewModel>()
    val data = viewModel.data.collectAsState(null)
    val added = remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(top = 64.dp), // todo
    ) {
        when (val values = data.value) {
            null -> viewModel.requestData(context.cacheDir, key)
            else -> Data(values)
        }
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 64.dp)
                .height(64.dp)
        ) {
            BasicText(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .clickable { broadcast(UnlockedScreen.Broadcast.Lock) },
                text = "lock",
            )
            BasicText(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .clickable {
                        added.value = true
                    },
                text = "add",
            )
        }
    }
    val durationMillis = 250
    AnimatedVisibility(
        visible = added.value,
        enter = slideInHorizontally(tween(durationMillis), initialOffsetX = { it })
                + fadeIn(tween(durationMillis)),
        exit = slideOutHorizontally(tween(durationMillis), targetOffsetX = { it })
                + fadeOut(tween(durationMillis)),
    ) {
        AddItemScreen(
            keys = data.value!!.keys,
            onCancel = {
                added.value = false
            },
            onAdd = { name, value ->
                viewModel.addData(context.cacheDir, key, name = name, value = value)
                added.value = false
            }
        )
    }
}
