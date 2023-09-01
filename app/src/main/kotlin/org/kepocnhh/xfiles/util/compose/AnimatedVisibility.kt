package org.kepocnhh.xfiles.util.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import kotlin.time.Duration

@Composable
internal fun AnimatedHVisibility(
    visible: Boolean,
    modifier: Modifier = Modifier,
    label: String = "AnimatedHVisibility",
    duration: Duration,
    initialOffsetX: (fullWidth: Int) -> Int = { -it },
    targetOffsetX: (fullWidth: Int) -> Int = initialOffsetX,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        label = label,
        enter = slideInHAndFade(
            durationMillis = duration.inWholeMilliseconds.toInt(),
            initialOffsetX = initialOffsetX,
        ),
        exit = slideOutHAndFade(
            durationMillis = duration.inWholeMilliseconds.toInt(),
            targetOffsetX = targetOffsetX,
        ),
        content = content,
    )
}

@Composable
internal fun AnimatedHVisibilityShadow(
    visible: Boolean,
    modifier: Modifier = Modifier,
    label: String = "AnimatedHVisibilityShadow",
    duration: Duration,
    initialOffsetX: (fullWidth: Int) -> Int = { -it },
    targetOffsetX: (fullWidth: Int) -> Int = initialOffsetX,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        label = label,
        enter = fadeIn(tween(duration.inWholeMilliseconds.toInt())),
        exit = fadeOut(tween(duration.inWholeMilliseconds.toInt())),
        content = {
                  Spacer(
                      modifier = Modifier
                          .fillMaxSize()
                          .background(Color.Black),
                  )
        },
    )
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        label = label,
        enter = slideInHorizontally(
            animationSpec = tween(duration.inWholeMilliseconds.toInt()),
            initialOffsetX = initialOffsetX,
        ),
        exit = slideOutHorizontally(
            animationSpec = tween(duration.inWholeMilliseconds.toInt()),
            targetOffsetX = targetOffsetX,
        ),
        content = content,
    )
}

@Composable
internal fun AnimatedFadeVisibility(
    visible: Boolean,
    modifier: Modifier = Modifier,
    label: String = "AnimatedFadeVisibility",
    duration: Duration,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        label = label,
        enter = fadeIn(tween(duration.inWholeMilliseconds.toInt())),
        exit = fadeOut(tween(duration.inWholeMilliseconds.toInt())),
        content = content,
    )
}
