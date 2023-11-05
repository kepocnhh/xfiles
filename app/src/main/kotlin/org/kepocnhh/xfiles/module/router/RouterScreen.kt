package org.kepocnhh.xfiles.module.router

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.kepocnhh.xfiles.App
import org.kepocnhh.xfiles.module.enter.EnterScreen
import org.kepocnhh.xfiles.module.unlocked.UnlockedScreen
import org.kepocnhh.xfiles.util.compose.Squares
import sp.ax.jc.animations.tween.fade.FadeVisibility
import sp.ax.jc.animations.tween.slide.SlideHVisibility
import javax.crypto.SecretKey

@Composable
private fun BoxScope.OnChecked(onBack: () -> Unit) {
    val keyState = rememberSaveable { mutableStateOf<SecretKey?>(null) }
    // todo fade
    SlideHVisibility(
        visible = keyState.value == null,
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
        visible = keyState.value != null,
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
private fun BoxScope.OnError(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.Center),
    ) {
        BasicText(
            modifier = Modifier
                .fillMaxWidth()
                .height(App.Theme.sizes.xxxl)
                .wrapContentSize(),
            style = App.Theme.textStyle,
            text = "Security services error!",
        )
        BasicText(
            modifier = Modifier
                .fillMaxWidth()
                .height(App.Theme.sizes.xxxl)
                .clickable {
                    onBack()
                }
                .wrapContentSize(),
            style = App.Theme.textStyle.copy(color = App.Theme.colors.primary),
            text = "Exit",
        )
    }
}

@Composable
internal fun RouterScreen(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(App.Theme.colors.background),
    ) {
        val viewModel = App.viewModel<RouterViewModel>()
        val state = viewModel.state.collectAsState().value
        LaunchedEffect(state) {
            if (state == null) {
                viewModel.checkSecurityServices()
            }
        }
        when (state) {
            RouterViewModel.State.CHECKED -> OnChecked(onBack = onBack)
            RouterViewModel.State.ERROR -> OnError(onBack = onBack)
            else -> {
                Squares(
                    modifier = Modifier.align(Alignment.Center),
                    color = App.Theme.colors.foreground,
                    width = App.Theme.sizes.large,
                    padding = App.Theme.sizes.small,
                    radius = App.Theme.sizes.xs,
                )
            }
        }
    }
}
