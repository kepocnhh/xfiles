package org.kepocnhh.xfiles.util.compose

import android.os.Build
import android.view.WindowInsets
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

internal fun WindowInsets.toPaddings(density: Float) : PaddingValues {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        getInsets(WindowInsets.Type.systemBars()).let {
            PaddingValues(
                bottom = it.bottom.px(density),
                end = it.right.px(density),
                start = it.left.px(density),
                top = it.top.px(density),
            )
        }
    } else {
        PaddingValues(
            bottom = systemWindowInsetBottom.px(density),
            end = systemWindowInsetRight.px(density),
            start = systemWindowInsetLeft.px(density),
            top = systemWindowInsetTop.px(density),
        )
    }
}

@Composable
internal fun WindowInsets.toPaddings(density: Density = LocalDensity.current) : PaddingValues {
    return toPaddings(density = density.density)
}

internal fun Modifier.paddings(
    horizontal: PaddingValues,
    layoutDirection: LayoutDirection,
): Modifier {
    return padding(
        start = horizontal.calculateStartPadding(layoutDirection),
        end = horizontal.calculateEndPadding(layoutDirection),
    )
}

internal fun Modifier.paddings(
    horizontal: PaddingValues,
): Modifier {
    return composed {
        val layoutDirection = LocalConfiguration.current.requireLayoutDirection()
        paddings(
            horizontal = horizontal,
            layoutDirection = layoutDirection,
        )
    }
}
