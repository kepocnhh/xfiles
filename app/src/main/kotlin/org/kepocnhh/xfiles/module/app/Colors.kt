package org.kepocnhh.xfiles.module.app

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

@Immutable
internal class Colors private constructor(
    val basement: Color,
    val background: Color,
    val secondary: Color,
    val foreground: Color,
    val primary: Color,
    val error: Color,
    val text: Color,
    val textHint: Color,
    val capitals: Color,
    val icon: Color,
) {
    companion object {
        private val primary = Color(0xff1E88E5)
        private val error = Color(0xffe53935)
        val digits = primary
        val signs = Color(0xff09af00)
        private const val shift = 0x00101010
        val black = Color(0xff000000)
        val white = Color(0xffffffff)
        val dark = Color(0xff070707).let { basement ->
            Colors(
                basement = basement,
                background = basement.shifted(shift, 1),
                foreground = white,
                primary = primary,
                secondary = basement.shifted(shift, 2),
                error = error,
                text = white,
                textHint = basement.shifted(shift, 4),
                capitals = basement.shifted(shift, 10),
                icon = white,
            )
        }
        val light = Color(0xfff8f8f8).let { basement ->
            Colors(
                basement = basement,
                background = basement.shifted(-shift, 1),
                foreground = black,
                primary = primary,
                secondary = basement.shifted(-shift, 2),
                error = error,
                text = black,
                textHint = basement.shifted(-shift, 4),
                capitals = basement.shifted(-shift, 10),
                icon = black,
            )
        }

        private fun Color.shifted(shift: Int, times: Int): Color {
            check(shift != 0)
            check(times > 0)
            return Color(toArgb() + shift * times)
        }
    }
}
