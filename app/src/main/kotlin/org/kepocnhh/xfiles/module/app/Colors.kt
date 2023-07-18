package org.kepocnhh.xfiles.module.app

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
internal sealed class Colors(
    val background: Color,
    val foreground: Color,
) {
    object Dark : Colors(
        background = Color(0xff121212),
        foreground = white,
    )

    object Light : Colors(
        background = white,
        foreground = black,
    )

    companion object {
        val black = Color(0xff000000)
        val white = Color(0xffffffff)
    }
}
