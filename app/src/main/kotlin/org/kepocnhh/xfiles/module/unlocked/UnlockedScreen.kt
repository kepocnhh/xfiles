package org.kepocnhh.xfiles.module.unlocked

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

internal object UnlockedScreen {
    sealed interface Broadcast {
        object Lock : Broadcast
    }
}

@Composable
internal fun UnlockedScreen(broadcast: (UnlockedScreen.Broadcast) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        Column(modifier = Modifier.align(Alignment.Center)) {
            BasicText(modifier = Modifier, text = "unlocked")
            BasicText(modifier = Modifier
                .clickable { broadcast(UnlockedScreen.Broadcast.Lock) }
                .padding(8.dp), text = "lock")
        }
    }
}
