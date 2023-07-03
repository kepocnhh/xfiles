package org.kepocnhh.xfiles.module.unlocked

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
    val context = LocalContext.current
    val viewModel = viewModel<UnlockedViewModel>()
    val data = viewModel.data.collectAsState(null)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        when (val values = data.value) {
            null -> viewModel.requestData(context.cacheDir, key)
            else -> Data(values)
        }
        BasicText(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .clickable { broadcast(UnlockedScreen.Broadcast.Lock) }
                .padding(8.dp)
                .padding(bottom = 128.dp),
            text = "lock",
        )
    }
}
