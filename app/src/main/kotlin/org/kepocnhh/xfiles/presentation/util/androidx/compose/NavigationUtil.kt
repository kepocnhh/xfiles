package org.kepocnhh.xfiles.presentation.util.androidx.compose

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import org.kepocnhh.xfiles.presentation.util.androidx.compose.foundation.catchClicks
import org.kepocnhh.xfiles.presentation.util.androidx.compose.foundation.onClick
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@Composable
internal fun ToScreen(content: @Composable () -> Unit) {
    val initialWidth = LocalConfiguration.current.screenWidthDp.dp
    val targetWidth = initialWidth // todo orientation
    val actualValue = rememberSaveable { mutableStateOf(1f) }
    val animatable = remember { Animatable(initialValue = actualValue.value) }
    val delay = 250.milliseconds // todo
    val targetValue = 0f
    LaunchedEffect(Unit) {
        animatable.animateTo(
            targetValue = targetValue,
            animationSpec = tween(
                durationMillis = delay.inWholeMilliseconds.toInt(),
                easing = LinearEasing
            ),
        )
    }
    Box(modifier = Modifier.fillMaxSize()) {
        actualValue.value = animatable.value
        val alpha = animatable.value
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Colors.black.copy(alpha = alpha)),
        )
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(targetWidth)
                .offset(x = initialWidth * animatable.value + (initialWidth - targetWidth))
                .catchClicks(),
        ) {
            content()
        }
    }
}

@Composable
internal fun ToScreen(
    backState: MutableState<Boolean>,
    onBack: () -> Unit,
    content: @Composable () -> Unit,
) {
    val initialWidth = LocalConfiguration.current.screenWidthDp.dp
    val targetWidth = initialWidth // todo orientation
    val actualValue = rememberSaveable { mutableStateOf(1f) }
    val animatable = remember { Animatable(initialValue = actualValue.value) }
    val delay = 1.seconds // todo
//    val delay = 250.milliseconds // todo
    val targetValue = if (backState.value) 1f else 0f
    LaunchedEffect(backState.value) {
        animatable.animateTo(
            targetValue = targetValue,
            animationSpec = tween(
                durationMillis = delay.inWholeMilliseconds.toInt(),
                easing = LinearEasing
            ),
        )
    }
    if (backState.value) {
        if (animatable.value == targetValue) onBack()
    }
    BackHandler {
        backState.value = true
    }
    Box(modifier = Modifier.fillMaxSize()) {
        actualValue.value = animatable.value
        val alpha = animatable.value
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Colors.black.copy(alpha = alpha))
                .onClick {
                    if (!backState.value) backState.value = true
                },
        )
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(targetWidth)
                .offset(x = initialWidth * animatable.value + (initialWidth - targetWidth))
                .catchClicks(),
        ) {
            content()
        }
    }
}