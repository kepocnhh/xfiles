package org.kepocnhh.xfiles.util.compose

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize

internal fun slideInHAndFade(
    slideAnimationSpec: FiniteAnimationSpec<IntOffset>,
    fadeAnimationSpec: FiniteAnimationSpec<Float>,
    initialOffsetX: (fullWidth: Int) -> Int,
): EnterTransition {
    return slideInHorizontally(
        animationSpec = slideAnimationSpec,
        initialOffsetX = initialOffsetX
    ) + fadeIn(fadeAnimationSpec)
}

internal fun slideInHAndFade(
    durationMillis: Int,
    initialOffsetX: (fullWidth: Int) -> Int,
): EnterTransition {
    return slideInHAndFade(
        slideAnimationSpec = tween(durationMillis),
        fadeAnimationSpec = tween(durationMillis),
        initialOffsetX = initialOffsetX
    )
}

internal fun slideOutHAndFade(
    slideAnimationSpec: FiniteAnimationSpec<IntOffset>,
    fadeAnimationSpec: FiniteAnimationSpec<Float>,
    targetOffsetX: (fullWidth: Int) -> Int,
): ExitTransition {
    return slideOutHorizontally(
        animationSpec = slideAnimationSpec,
        targetOffsetX = targetOffsetX
    ) + fadeOut(fadeAnimationSpec)
}

internal fun slideOutHAndFade(
    durationMillis: Int,
    targetOffsetX: (fullWidth: Int) -> Int,
): ExitTransition {
    return slideOutHAndFade(
        slideAnimationSpec = tween(durationMillis),
        fadeAnimationSpec = tween(durationMillis),
        targetOffsetX = targetOffsetX
    )
}

internal fun slideInVFadeExpand(
    slideAnimationSpec: FiniteAnimationSpec<IntOffset>,
    fadeAnimationSpec: FiniteAnimationSpec<Float>,
    expandAnimationSpec: FiniteAnimationSpec<IntSize>,
    expandFrom: Alignment.Vertical,
    initialOffsetY: (fullHeight: Int) -> Int,
): EnterTransition {
    return slideInVertically(
        animationSpec = slideAnimationSpec,
        initialOffsetY = initialOffsetY
    ) + fadeIn(fadeAnimationSpec) + expandVertically(
        animationSpec = expandAnimationSpec,
        expandFrom = expandFrom,
    )
}

internal fun slideInVFadeExpand(
    durationMillis: Int,
    expandFrom: Alignment.Vertical = Alignment.Top,
    initialOffsetY: (fullHeight: Int) -> Int,
): EnterTransition {
    return slideInVFadeExpand(
        slideAnimationSpec = tween(durationMillis),
        fadeAnimationSpec = tween(durationMillis),
        expandAnimationSpec = tween(durationMillis),
        expandFrom = expandFrom,
        initialOffsetY = initialOffsetY
    )
}

internal fun slideOutVAndFade(
    slideAnimationSpec: FiniteAnimationSpec<IntOffset>,
    fadeAnimationSpec: FiniteAnimationSpec<Float>,
    targetOffsetY: (fullHeight: Int) -> Int,
): ExitTransition {
    return slideOutVertically(
        animationSpec = slideAnimationSpec,
        targetOffsetY = targetOffsetY
    ) + fadeOut(fadeAnimationSpec)
}

internal fun slideOutVAndFade(
    durationMillis: Int,
    targetOffsetY: (fullHeight: Int) -> Int,
): ExitTransition {
    return slideOutVAndFade(
        slideAnimationSpec = tween(durationMillis),
        fadeAnimationSpec = tween(durationMillis),
        targetOffsetY = targetOffsetY
    )
}
