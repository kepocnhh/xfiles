package org.kepocnhh.xfiles.util.compose

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
internal sealed class Colors(
    val background: Color,
    val primary: Color,
    val text: Color,
) {
    object Dark : Colors(
        background = gray,
        primary = Color(0xff3174d8),
        text = white,
    )

    object Light : Colors(
        background = white,
        primary = Color(0xff3174d8),
        text = black,
    )

    companion object {
        val gray = Color(0xff222222)
        val black = Color(0xff000000)
        val white = Color(0xffffffff)
    }
}
