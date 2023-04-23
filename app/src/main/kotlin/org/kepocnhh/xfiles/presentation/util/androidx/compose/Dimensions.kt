package org.kepocnhh.xfiles.presentation.util.androidx.compose

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp

@Immutable
internal class Dimensions(val insets: Insets, val sizes: Sizes)

@Immutable
internal class Sizes(
    val xxxs: Dp,
    val xxs: Dp,
    val xs: Dp,
    val s: Dp,
    val m: Dp,
    val l: Dp,
    val xl: Dp,
    val xxl: Dp,
    val xxxl: Dp,
)