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
private fun SettingsVersionPreview(themeState: ThemeState) {
    App.Theme.Composition(themeState) {
        Box(modifier = Modifier.background(App.Theme.colors.background)) {
            SettingsVersion()
        }
    }
}

@Preview(name = "DARK/ENGLISH")
@Composable
private fun SettingsVersionDarkEnPreview() {
    val themeState = ThemeState(
        colorsType = ColorsType.DARK,
        language = Language.ENGLISH,
    )
    SettingsVersionPreview(themeState = themeState)
}

@Preview(name = "LIGHT/RUSSIAN")
@Composable
private fun SettingsVersionLightRuPreview() {
    val themeState = ThemeState(
        colorsType = ColorsType.LIGHT,
        language = Language.RUSSIAN,
    )
    SettingsVersionPreview(themeState = themeState)
}
