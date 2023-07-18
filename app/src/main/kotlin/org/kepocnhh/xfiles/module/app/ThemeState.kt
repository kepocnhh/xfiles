package org.kepocnhh.xfiles.module.app

internal enum class ColorsType {
    DARK,
    LIGHT,
    AUTO
}

internal data class ThemeState(
    val colorsType: ColorsType,
)
