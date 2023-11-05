package org.kepocnhh.xfiles.module.router

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import org.kepocnhh.xfiles.App
import org.kepocnhh.xfiles.module.checks.ChecksScreen
import org.kepocnhh.xfiles.module.enter.EnterScreen
import org.kepocnhh.xfiles.module.unlocked.UnlockedScreen
import sp.ax.jc.animations.tween.fade.FadeVisibility
import sp.ax.jc.animations.tween.slide.SlideHVisibility
import javax.crypto.SecretKey

@Composable
private fun OnChecked(onBack: () -> Unit) {
    // todo start animation if visible on start
    val animatedState = rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(animatedState.value) {
        if (!animatedState.value) {
            animatedState.value = true
        }
    }
    val keyState = rememberSaveable { mutableStateOf<SecretKey?>(null) }
    // todo fade
    SlideHVisibility(
        visible = animatedState.value && keyState.value == null,
        initialOffsetX = { -it },
        targetOffsetX = { -it },
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
    // todo fade
    SlideHVisibility(
        visible = animatedState.value && keyState.value != null,
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

@Composable
internal fun RouterScreen(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(App.Theme.colors.background),
    ) {
        val checkedState = rememberSaveable { mutableStateOf(false) }
        FadeVisibility(visible = !checkedState.value) {
            ChecksScreen(
                onComplete = {
                    checkedState.value = true
                },
                onExit = onBack,
            )
        }
        if (checkedState.value) {
            OnChecked(onBack = onBack)
        }
    }
}
