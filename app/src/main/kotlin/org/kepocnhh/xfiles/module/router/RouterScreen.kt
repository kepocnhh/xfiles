package org.kepocnhh.xfiles.module.router

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import org.kepocnhh.xfiles.App
import org.kepocnhh.xfiles.module.checks.ChecksScreen
import org.kepocnhh.xfiles.module.enter.EnterScreen
import org.kepocnhh.xfiles.module.unlocked.UnlockedScreen
import sp.ax.jc.animations.style.LocalFadeStyle
import sp.ax.jc.animations.style.LocalSlideHStyle
import sp.ax.jc.animations.style.LocalTweenStyle
import sp.ax.jc.animations.style.TweenStyle
import sp.ax.jc.animations.tween.fade.FadeVisibility
import sp.ax.jc.animations.tween.fade.fadeIn
import sp.ax.jc.animations.tween.fade.fadeOut
import sp.ax.jc.animations.tween.slide.slideIn
import sp.ax.jc.animations.tween.slide.slideOut
import javax.crypto.SecretKey

@Composable
private fun enterTransition(
    tweenStyle: TweenStyle = LocalTweenStyle.current,
    initialOffsetX: (fullWidth: Int) -> Int = LocalSlideHStyle.current.initialOffsetX,
    initialAlpha: Float = LocalFadeStyle.current.initialAlpha,
): EnterTransition {
    return slideIn(
        delay = tweenStyle.delay,
        duration = tweenStyle.duration,
        easing = tweenStyle.easing,
        initialOffset = { IntOffset(x = initialOffsetX(it.width), 0) },
    ) + fadeIn(
        delay = tweenStyle.delay,
        duration = tweenStyle.duration,
        easing = tweenStyle.easing,
        initialAlpha = initialAlpha,
    )
}

@Composable
private fun exitTransition(
    tweenStyle: TweenStyle = LocalTweenStyle.current,
    targetOffsetX: (fullWidth: Int) -> Int = LocalSlideHStyle.current.targetOffsetX,
    targetAlpha: Float = LocalFadeStyle.current.targetAlpha,
): ExitTransition {
    return slideOut(
        delay = tweenStyle.delay,
        duration = tweenStyle.duration,
        easing = tweenStyle.easing,
        targetOffset = { IntOffset(x = targetOffsetX(it.width), 0) },
    ) + fadeOut(
        delay = tweenStyle.delay,
        duration = tweenStyle.duration,
        easing = tweenStyle.easing,
        targetAlpha = targetAlpha,
    )
}

@Composable
private fun OnChecked() {
    val animatedState = rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(animatedState.value) {
        if (!animatedState.value) {
            animatedState.value = true
        }
    }
    val keyState = remember { mutableStateOf<SecretKey?>(null) }
    val initState = rememberSaveable { mutableStateOf(true) }
    LaunchedEffect(keyState.value) {
        if (keyState.value != null && initState.value) {
            initState.value = false
        }
    }
    val tweenStyle = LocalTweenStyle.current.copy()
    AnimatedVisibility(
//        visible = keyState.value == null,
        visible = animatedState.value && keyState.value == null,
        enter = enterTransition(
            tweenStyle = tweenStyle,
            initialOffsetX = { if (initState.value) it else -it },
        ),
        exit = exitTransition(
            tweenStyle = tweenStyle,
            targetOffsetX = { -it },
        ),
    ) {
        EnterScreen(
            broadcast = { broadcast ->
                when (broadcast) {
                    is EnterScreen.Broadcast.Unlock -> {
                        keyState.value = broadcast.key
                    }
                }
            },
        )
    }
    AnimatedVisibility(
//        visible = keyState.value != null,
        visible = animatedState.value && keyState.value != null,
        enter = enterTransition(
            tweenStyle = tweenStyle,
        ),
        exit = exitTransition(
            tweenStyle = tweenStyle,
        ),
    ) {
        UnlockedScreen(
            key = remember {
                val key = keyState.value ?: error("No key!")
                mutableStateOf(key)
            }.value,
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
        FadeVisibility(
            visible = !checkedState.value,
//            duration = 2.seconds,
        ) {
            ChecksScreen(
                onComplete = {
                    checkedState.value = true
                },
                onExit = onBack,
            )
        }
        if (checkedState.value) {
            OnChecked()
        }
    }
}
