package org.kepocnhh.xfiles.util.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
