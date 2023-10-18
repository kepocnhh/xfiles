package org.kepocnhh.xfiles.util.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import kotlin.math.absoluteValue
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
    modifierShadow: Modifier = modifier,
    colorShadow: Color = Color.Black,
    label: String = "AnimatedHVisibilityShadow",
    duration: Duration,
    initialOffsetX: (fullWidth: Int) -> Int = { -it },
    targetOffsetX: (fullWidth: Int) -> Int = initialOffsetX,
    content: @Composable AnimatedVisibilityScope.() -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifierShadow,
        label = label,
        enter = fadeIn(tween(duration.inWholeMilliseconds.toInt())),
        exit = fadeOut(tween(duration.inWholeMilliseconds.toInt())),
        content = {
            Spacer(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colorShadow),
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
internal fun AnimatedHOpen(
    visible: Boolean,
    colorShadow: Color,
    onShadow: () -> Unit,
    width: Dp,
    shadowWidth: Dp = width,
    targetWidth: Dp,
    duration: Duration,
    content: @Composable AnimatedVisibilityScope.() -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        modifier = Modifier
            .fillMaxHeight()
            .width(shadowWidth)
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = null,
                onClick = onShadow,
            ),
        label = "AnimatedHOpen:Shadow",
        enter = fadeIn(tween(duration.inWholeMilliseconds.toInt())),
        exit = fadeOut(tween(duration.inWholeMilliseconds.toInt())),
        content = {
            Spacer(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colorShadow),
            )
        },
    )
    AnimatedVisibility(
        visible = visible,
        modifier = Modifier
            .fillMaxHeight()
            .width(targetWidth)
            .offset(x = width - targetWidth)
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = null,
                onClick = {},
            ),
        label = "AnimatedHOpen:Target",
        enter = slideInHorizontally(
            animationSpec = tween(duration.inWholeMilliseconds.toInt()),
            initialOffsetX = { it },
        ),
        exit = slideOutHorizontally(
            animationSpec = tween(duration.inWholeMilliseconds.toInt()),
            targetOffsetX = { it },
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

@Composable
internal fun SlideVAndFade(
    visible: Boolean,
    modifier: Modifier = Modifier,
    label: String = "SlideVAndFade",
    duration: Duration,
    initialOffsetY: (fullHeight: Int) -> Int = { it },
    targetOffsetY: (fullHeight: Int) -> Int = initialOffsetY,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        label = label,
        enter = slideInVAndFade(
            durationMillis = duration.inWholeMilliseconds.toInt(),
            initialOffsetY = initialOffsetY,
        ),
        exit = slideOutVAndFade(
            durationMillis = duration.inWholeMilliseconds.toInt(),
            targetOffsetY = targetOffsetY,
        ),
        content = content,
    )
}
