package org.kepocnhh.xfiles.presentation.util.androidx.compose

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
internal sealed class Colors(
    val background: Color,
    val primary: Color,
    val text: Color,
) {
    object Dark : Colors(
        background = black,
        primary = Color(0xff3174d8),
        text = white,
    )

    companion object {
        val black = Color(0xff000000)
        val white = Color(0xffffffff)
    }
}
