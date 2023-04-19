package org.kepocnhh.xfiles.presentation.util.androidx.compose

import android.os.Build
import android.view.WindowInsets
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Immutable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import org.kepocnhh.xfiles.presentation.util.androidx.compose.ui.unit.px

@Immutable
internal data class Insets(
    val bottom: Dp,
    val end: Dp,
    val start: Dp,
    val top: Dp,
) {
    companion object {
        fun empty(): Insets {
            return Insets(
                bottom = Dp.Hairline,
                end = Dp.Hairline,
                start = Dp.Hairline,
                top = Dp.Hairline,
            )
        }
    }
}

internal fun WindowInsets.toInsets(density: Float) : Insets {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        getInsets(WindowInsets.Type.systemBars()).let {
            Insets(
                bottom = it.bottom.px(density),
                end = it.right.px(density),
                start = it.left.px(density),
                top = it.top.px(density),
            )
        }
    } else {
        Insets(
            bottom = systemWindowInsetBottom.px(density),
            end = systemWindowInsetRight.px(density),
            start = systemWindowInsetLeft.px(density),
            top = systemWindowInsetTop.px(density),
        )
    }
}

@Composable
internal fun WindowInsets.toInsets(density: Density = LocalDensity.current) : Insets {
    return toInsets(density = density.density)
}

internal fun Modifier.padding(insets: Insets): Modifier {
    return padding(
        bottom = insets.bottom,
        end = insets.end,
        start = insets.start,
        top = insets.top,
    )
}
