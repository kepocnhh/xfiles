package org.kepocnhh.xfiles.module.router

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Easing
import androidx.compose.animation.togetherWith
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
import sp.ax.jc.animations.style.FadeStyle
import sp.ax.jc.animations.style.LocalFadeStyle
import sp.ax.jc.animations.style.LocalSlideHStyle
import sp.ax.jc.animations.style.LocalTweenStyle
import sp.ax.jc.animations.style.SlideStyle
import sp.ax.jc.animations.style.TweenStyle
import sp.ax.jc.animations.tween.fade.FadeVisibility
import sp.ax.jc.animations.tween.fade.fadeIn
import sp.ax.jc.animations.tween.fade.fadeOut
import sp.ax.jc.animations.tween.slide.SlideHVisibility
import sp.ax.jc.animations.tween.slide.slideIn
import sp.ax.jc.animations.tween.slide.slideOut
import javax.crypto.SecretKey
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

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
private fun contentTransform(
    tweenStyle: TweenStyle = LocalTweenStyle.current,
    slideStyle: SlideStyle.Horizontal = LocalSlideHStyle.current,
    fadeStyle: FadeStyle = LocalFadeStyle.current,
): ContentTransform {
    val enter = enterTransition(
        tweenStyle = tweenStyle,
        initialOffsetX = slideStyle.initialOffsetX,
        initialAlpha = fadeStyle.initialAlpha,
    )
    val exit = exitTransition(
        tweenStyle = tweenStyle,
        targetOffsetX = slideStyle.targetOffsetX,
        targetAlpha = fadeStyle.targetAlpha,
    )
    return enter togetherWith exit
}

@Composable
private fun OnChecked(onBack: () -> Unit) {
    // todo start animation if visible on start
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
    val tweenStyle = LocalTweenStyle.current.copy(
//        duration = 2.seconds,
    )
    // todo fade
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
            OnChecked(onBack = onBack)
        }
    }
}
