package org.kepocnhh.xfiles.module.app

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/*
01] 00 = 00 | 255 = ff |
08] 07 = 07 | 247 = f8 | downstairs
16] 15 = 0f | 239 = ef |
24] 23 = 17 | 231 = e8 | background
32] 31 = 1f | 223 = df |
40] 39 = 27 | 215 = d8 | secondary
48] 47 = 2f | 207 = cf |
56] 55 = 37 | 199 = c8 |
64] 63 = 3f | 191 = bf |
*/

@Immutable
internal sealed class Colors(
    val downstairs: Color,
    val background: Color,
    val secondary: Color,
    val foreground: Color,
    val primary: Color,
    val error: Color,
    val text: Color,
    val icon: Color,
) {
    object Dark : Colors(
        downstairs = Color(0xff070707),
//        background = Color(0xff000000),
//        background = Color(0xff070707),
//        background = Color(0xff0f0f0f),
        background = Color(0xff171717),
        foreground = white,
        primary = primary,
//        secondary = Color(0xff0f0f0f),
//        secondary = Color(0xff171717),
        secondary = Color(0xff272727),
        error = error,
        text = white,
        icon = white,
    )

    object Light : Colors(
        downstairs = Color(0xfff8f8f8),
//        background = white,
//        background = Color(0xffffffff),
//        background = Color(0xfff8f8f8),
//        background = Color(0xffefefef),
        background = Color(0xffe8e8e8),
        foreground = black,
        primary = primary,
//        secondary = Color(0xffefefef),
//        secondary = Color(0xffe8e8e8),
        secondary = Color(0xffd8d8d8),
        error = error,
        text = black,
        icon = black,
    )

    companion object {
        val black = Color(0xff000000)
        val white = Color(0xffffffff)
        val primary = Color(0xff1E88E5)
        val error = Color(0xffe53935)
    }
}
