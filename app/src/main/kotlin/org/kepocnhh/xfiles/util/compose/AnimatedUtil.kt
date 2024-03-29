package org.kepocnhh.xfiles.util.compose

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.ui.unit.IntOffset

internal fun slideInHAndFade(
    slideAnimationSpec: FiniteAnimationSpec<IntOffset>,
    fadeAnimationSpec: FiniteAnimationSpec<Float>,
    initialOffsetX: (fullWidth: Int) -> Int,
): EnterTransition {
    return slideInHorizontally(
        animationSpec = slideAnimationSpec,
        initialOffsetX = initialOffsetX,
    ) + fadeIn(fadeAnimationSpec)
}

internal fun slideInHAndFade(
    durationMillis: Int,
    initialOffsetX: (fullWidth: Int) -> Int,
): EnterTransition {
    return slideInHAndFade(
        slideAnimationSpec = tween(durationMillis),
        fadeAnimationSpec = tween(durationMillis),
        initialOffsetX = initialOffsetX,
    )
}

internal fun slideOutHAndFade(
    slideAnimationSpec: FiniteAnimationSpec<IntOffset>,
    fadeAnimationSpec: FiniteAnimationSpec<Float>,
    targetOffsetX: (fullWidth: Int) -> Int,
): ExitTransition {
    return slideOutHorizontally(
        animationSpec = slideAnimationSpec,
        targetOffsetX = targetOffsetX,
    ) + fadeOut(fadeAnimationSpec)
}

internal fun slideOutHAndFade(
    durationMillis: Int,
    targetOffsetX: (fullWidth: Int) -> Int,
): ExitTransition {
    return slideOutHAndFade(
        slideAnimationSpec = tween(durationMillis),
        fadeAnimationSpec = tween(durationMillis),
        targetOffsetX = targetOffsetX,
    )
}

internal fun slideInVFade(
    slideAnimationSpec: FiniteAnimationSpec<IntOffset>,
    fadeAnimationSpec: FiniteAnimationSpec<Float>,
    initialOffsetY: (fullHeight: Int) -> Int,
): EnterTransition {
    return slideInVertically(
        animationSpec = slideAnimationSpec,
        initialOffsetY = initialOffsetY,
    ) + fadeIn(fadeAnimationSpec)
}

internal fun slideInVFade(
    durationMillis: Int,
    initialOffsetY: (fullHeight: Int) -> Int,
): EnterTransition {
    return slideInVFade(
        slideAnimationSpec = tween(durationMillis),
        fadeAnimationSpec = tween(durationMillis),
        initialOffsetY = initialOffsetY,
    )
}

internal fun slideOutVAndFade(
    slideAnimationSpec: FiniteAnimationSpec<IntOffset>,
    fadeAnimationSpec: FiniteAnimationSpec<Float>,
    targetOffsetY: (fullHeight: Int) -> Int,
): ExitTransition {
    return slideOutVertically(
        animationSpec = slideAnimationSpec,
        targetOffsetY = targetOffsetY,
    ) + fadeOut(fadeAnimationSpec)
}

internal fun slideOutVAndFade(
    durationMillis: Int,
    targetOffsetY: (fullHeight: Int) -> Int,
): ExitTransition {
    return slideOutVAndFade(
        slideAnimationSpec = tween(durationMillis),
        fadeAnimationSpec = tween(durationMillis),
        targetOffsetY = targetOffsetY,
    )
}
