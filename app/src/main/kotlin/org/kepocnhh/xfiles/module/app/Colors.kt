package org.kepocnhh.xfiles.module.app

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
internal sealed class Colors(
    val background: Color,
    val foreground: Color,
    val primary: Color,
    val error: Color,
) {
    object Dark : Colors(
        background = Color(0xff121212),
        foreground = white,
        primary = primary,
        error = error,
    )

    object Light : Colors(
        background = white,
        foreground = black,
        primary = primary,
        error = error,
    )

    companion object {
        val black = Color(0xff000000)
        val white = Color(0xffffffff)
        val primary = Color(0xff1E88E5)
        val error = Color(0xffe53935)
    }
}
