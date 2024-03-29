package org.kepocnhh.xfiles.util.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Suppress("FunctionMinLength")
@Stable
internal fun Int.px(density: Float): Dp {
    return (this / density).dp
}

@Suppress("FunctionMinLength")
@Stable
@Composable
internal fun Int.px(density: Density = LocalDensity.current): Dp {
    return px(density = density.density)
}
