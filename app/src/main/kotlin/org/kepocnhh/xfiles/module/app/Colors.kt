package org.kepocnhh.xfiles.module.app

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
internal sealed class Colors(
    val background: Color,
    val foreground: Color,
    val primary: Color,
    val secondary: Color,
    val error: Color,
    val text: Color,
//    val icon: Color,
//    val button: Color,
) {
    object Dark : Colors(
        background = Color(0xff101010),
//        background = Color(0xff121212),
        foreground = white,
        primary = primary,
        secondary = Color(0xff202020),
//        secondary = Color(0xff242424),
//        secondary = Color(0xff484848),
        error = error,
        text = white,
//        icon = black,
//        icon = white,
//        button = Color(0xff202020),
//        button = Color(0xff242424),
//        button = Color(0xff303030),
    )

    object Light : Colors(
        background = white,
        foreground = black,
        primary = primary,
        secondary = Color(0xffeeeeee),
//        secondary = Color(0xffdddddd),
        error = error,
        text = black,
//        icon = black,
//        icon = white,
//        button = Color(0xff121212),
//        button = Color(0xffeeeeee),
//        button = Color(0xffdddddd),
    )

    companion object {
        val black = Color(0xff000000)
        val white = Color(0xffffffff)
        val primary = Color(0xff1E88E5)
        val error = Color(0xffe53935)
    }
}
