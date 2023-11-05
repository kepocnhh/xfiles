package org.kepocnhh.xfiles.module.router

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import org.kepocnhh.xfiles.App
import org.kepocnhh.xfiles.module.enter.EnterScreen
import org.kepocnhh.xfiles.module.unlocked.UnlockedScreen
import org.kepocnhh.xfiles.util.compose.AnimatedHVisibility
import sp.ax.jc.animations.style.FadeStyle
import sp.ax.jc.animations.style.LocalFadeStyle
import sp.ax.jc.animations.style.LocalSlideHStyle
import sp.ax.jc.animations.style.LocalTweenStyle
import sp.ax.jc.animations.style.SlideStyle
import sp.ax.jc.animations.style.TweenStyle
import sp.ax.jc.animations.tween.fade.fadeIn
import sp.ax.jc.animations.tween.fade.fadeOut
import sp.ax.jc.animations.tween.slide.slideIn
import sp.ax.jc.animations.tween.slide.slideOut
import javax.crypto.SecretKey
import kotlin.time.Duration

private fun contentTransform(
    tweenStyle: TweenStyle,
    slideStyle: SlideStyle.Horizontal,
    fadeStyle: FadeStyle,
): ContentTransform {
    val enter = slideIn(
        delay = tweenStyle.delay,
        duration = tweenStyle.duration,
        easing = tweenStyle.easing,
        initialOffset = { IntOffset(slideStyle.initialOffsetX(it.width), 0) },
    ) + fadeIn(
        delay = tweenStyle.delay,
        duration = tweenStyle.duration,
        easing = tweenStyle.easing,
        initialAlpha = fadeStyle.initialAlpha,
    )
    val exit = slideOut(
        delay = tweenStyle.delay,
        duration = tweenStyle.duration,
        easing = tweenStyle.easing,
        targetOffset = { IntOffset(slideStyle.targetOffsetX(it.width), 0) },
    ) + fadeOut(
        delay = tweenStyle.delay,
        duration = tweenStyle.duration,
        easing = tweenStyle.easing,
        targetAlpha = fadeStyle.targetAlpha,
    )
    return enter togetherWith exit
}

@Composable
internal fun RouterScreen(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(App.Theme.colors.background),
    ) {
        val keyState = rememberSaveable { mutableStateOf<SecretKey?>(null) }
        val tweenStyle = LocalTweenStyle.current.copy(
            easing = LinearEasing,
        )
//        val slideStyle = LocalSlideHStyle.current
        val fadeStyle = LocalFadeStyle.current
        AnimatedContent(
            targetState = keyState.value,
            transitionSpec = {
                when (targetState) {
                    null -> {
                        val slideStyle = SlideStyle.Horizontal(
                            initialOffsetX = { -it },
                            targetOffsetX = { it },
                        )
                        contentTransform(tweenStyle, slideStyle, fadeStyle)
                    }
                    else -> {
                        val slideStyle = SlideStyle.Horizontal(
                            initialOffsetX = { it },
                            targetOffsetX = { -it },
                        )
                        contentTransform(tweenStyle, slideStyle, fadeStyle)
                    }
                }
            },
            label = "AnimatedContent:RouterScreen",
        ) {
            when (it) {
                null -> EnterScreen(
                    onBack = onBack,
                    broadcast = { broadcast ->
                        when (broadcast) {
                            is EnterScreen.Broadcast.Unlock -> {
                                keyState.value = broadcast.key
                            }
                        }
                    },
                )
                else -> UnlockedScreen(
                    key = remember { mutableStateOf(it) }.value,
                ) { broadcast ->
                    when (broadcast) {
                        UnlockedScreen.Broadcast.Lock -> {
                            keyState.value = null
                        }
                    }
                }
            }
        }
//        AnimatedHVisibility(
//            visible = keyState.value == null,
//            duration = App.Theme.durations.animation,
//        ) {
//            EnterScreen(
//                onBack = onBack,
//                broadcast = { broadcast ->
//                    when (broadcast) {
//                        is EnterScreen.Broadcast.Unlock -> {
//                            keyState.value = broadcast.key
//                        }
//                    }
//                },
//            )
//        }
//        AnimatedHVisibility(
//            visible = keyState.value != null,
//            duration = App.Theme.durations.animation,
//            initialOffsetX = { it },
//        ) {
//            UnlockedScreen(
//                key = remember { mutableStateOf(keyState.value!!) }.value,
//            ) { broadcast ->
//                when (broadcast) {
//                    UnlockedScreen.Broadcast.Lock -> {
//                        keyState.value = null
//                    }
//                }
//            }
//        }
    }
}
