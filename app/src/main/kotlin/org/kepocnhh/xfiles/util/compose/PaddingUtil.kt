package org.kepocnhh.xfiles.util.compose

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.LayoutDirection

internal fun Modifier.verticalPaddings(
    paddingValues: PaddingValues,
): Modifier {
    return padding(
        top = paddingValues.calculateTopPadding(),
        bottom = paddingValues.calculateBottomPadding(),
    )
}

internal fun Modifier.horizontalPaddings(
    paddingValues: PaddingValues,
    layoutDirection: LayoutDirection,
): Modifier {
    return padding(
        start = paddingValues.calculateStartPadding(layoutDirection),
        end = paddingValues.calculateEndPadding(layoutDirection),
    )
}

internal fun Modifier.horizontalPaddings(
    paddingValues: PaddingValues,
): Modifier {
    return composed {
        val layoutDirection = LocalConfiguration.current.requireLayoutDirection()
        horizontalPaddings(
            paddingValues = paddingValues,
            layoutDirection = layoutDirection,
        )
    }
}
