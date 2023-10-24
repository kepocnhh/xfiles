package org.kepocnhh.xfiles.module.app

internal fun mockThemeState(
    colorsType: ColorsType = ColorsType.values().first(),
    language: Language = Language.values().first(),
): ThemeState {
    return ThemeState(
        colorsType = colorsType,
        language = language,
    )
}
