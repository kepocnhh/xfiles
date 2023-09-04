package org.kepocnhh.xfiles.util.compose

import android.content.res.Configuration
import android.view.View
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

@Composable
internal fun Configuration.requireLayoutDirection(): LayoutDirection {
    return when (val layoutDirection = layoutDirection) {
        View.LAYOUT_DIRECTION_LTR -> LayoutDirection.Ltr
        View.LAYOUT_DIRECTION_RTL -> LayoutDirection.Rtl
        else -> error("Layout direction $layoutDirection is not supported!")
    }
}

@Composable
internal fun Configuration.screenWidth(insets: PaddingValues): Dp {
    val layoutDirection = requireLayoutDirection()
    return screenWidthDp.dp + insets.calculateStartPadding(layoutDirection) + insets.calculateEndPadding(layoutDirection)
}

@Composable
internal fun Configuration.screenHeight(insets: PaddingValues): Dp {
    return screenHeightDp.dp + insets.calculateTopPadding() + insets.calculateBottomPadding()
}
