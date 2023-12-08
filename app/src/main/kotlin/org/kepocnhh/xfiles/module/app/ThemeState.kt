package org.kepocnhh.xfiles.module.app

internal enum class ColorsType {
    DARK,
    LIGHT,
    AUTO,
}

internal enum class Language {
    ENGLISH,
    RUSSIAN,
    AUTO,
}

internal data class ThemeState(
    val colorsType: ColorsType,
    val language: Language,
)
