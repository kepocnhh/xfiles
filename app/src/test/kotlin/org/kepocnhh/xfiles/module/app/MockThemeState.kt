package org.kepocnhh.xfiles.module.app

internal fun mockThemeState(
    colorsType: ColorsType = ColorsType.AUTO,
    language: Language = Language.AUTO,
): ThemeState {
    return ThemeState(
        colorsType = colorsType,
        language = language,
    )
}
