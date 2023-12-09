package org.kepocnhh.xfiles.module.enter.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.kepocnhh.xfiles.App
import org.kepocnhh.xfiles.module.app.ColorsType
import org.kepocnhh.xfiles.module.app.Language
import org.kepocnhh.xfiles.module.app.ThemeState

@Composable
private fun SettingsLanguagePreview(themeState: ThemeState) {
    App.Theme.Composition(themeState) {
        Box(modifier = Modifier.background(App.Theme.colors.background)) {
            SettingsLanguage(
                themeState = themeState,
                onLanguage = {
                    // noop
                },
            )
        }
    }
}

@Preview(name = "DARK/ENGLISH")
@Composable
private fun SettingsLanguageDarkEnPreview() {
    val themeState = ThemeState(
        colorsType = ColorsType.DARK,
        language = Language.ENGLISH,
    )
    SettingsLanguagePreview(themeState = themeState)
}

@Preview(name = "LIGHT/ENGLISH")
@Composable
private fun SettingsLanguageLightEnPreview() {
    val themeState = ThemeState(
        colorsType = ColorsType.LIGHT,
        language = Language.ENGLISH,
    )
    SettingsLanguagePreview(themeState = themeState)
}

@Preview(name = "DARK/RUSSIAN")
@Composable
private fun SettingsLanguageDarkRuPreview() {
    val themeState = ThemeState(
        colorsType = ColorsType.DARK,
        language = Language.RUSSIAN,
    )
    SettingsLanguagePreview(themeState = themeState)
}

@Preview(name = "LIGHT/RUSSIAN")
@Composable
private fun SettingsLanguageLightRuPreview() {
    val themeState = ThemeState(
        colorsType = ColorsType.LIGHT,
        language = Language.RUSSIAN,
    )
    SettingsLanguagePreview(themeState = themeState)
}
