package org.kepocnhh.xfiles.module.router

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import org.kepocnhh.xfiles.App
import org.kepocnhh.xfiles.module.enter.EnterScreen
import org.kepocnhh.xfiles.module.unlocked.UnlockedScreen
import org.kepocnhh.xfiles.util.compose.AnimatedHVisibility
import javax.crypto.SecretKey

@Composable
internal fun RouterScreen(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(App.Theme.colors.background),
    ) {
        val keyState = rememberSaveable { mutableStateOf<SecretKey?>(null) }
        AnimatedHVisibility(
            visible = keyState.value == null,
            duration = App.Theme.durations.animation,
        ) {
            EnterScreen(
                onBack = onBack,
                broadcast = { broadcast ->
                    when (broadcast) {
                        is EnterScreen.Broadcast.Unlock -> {
                            keyState.value = broadcast.key
                        }
                    }
                },
            )
        }
        AnimatedHVisibility(
            visible = keyState.value != null,
            duration = App.Theme.durations.animation,
            initialOffsetX = { it },
        ) {
            UnlockedScreen(
                key = remember { mutableStateOf(keyState.value!!) }.value,
            ) { broadcast ->
                when (broadcast) {
                    UnlockedScreen.Broadcast.Lock -> {
                        keyState.value = null
                    }
                }
            }
        }
    }
}
