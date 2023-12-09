package org.kepocnhh.xfiles.module.enter.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.kepocnhh.xfiles.App
import org.kepocnhh.xfiles.entity.SecurityService
import org.kepocnhh.xfiles.entity.SecuritySettings
import org.kepocnhh.xfiles.module.app.ColorsType
import org.kepocnhh.xfiles.module.app.Language
import org.kepocnhh.xfiles.module.app.ThemeState

@Composable
private fun SettingsCipherPreview(
    themeState: ThemeState,
    cipher: SecurityService?,
    settings: SecuritySettings?,
) {
    App.Theme.Composition(themeState) {
        Box(modifier = Modifier.background(App.Theme.colors.background)) {
            SettingsCipher(
                editable = false,
                cipher = cipher,
                settings = settings,
                onSettings = {
                    // noop
                },
            )
        }
    }
}

@Preview(name = "DARK/ENGLISH")
@Composable
private fun SettingsCipherDarkEnPreview() {
    val themeState = ThemeState(
        colorsType = ColorsType.DARK,
        language = Language.ENGLISH,
    )
    val cipher = SecurityService(provider = "foo", algorithm = "bar")
    val settings = SecuritySettings(
        pbeIterations = SecuritySettings.PBEIterations.NUMBER_2_16,
        aesKeyLength = SecuritySettings.AESKeyLength.BITS_256,
        dsaKeyLength = SecuritySettings.DSAKeyLength.BITS_1024_2,
        hasBiometric = false,
    )
    SettingsCipherPreview(
        themeState = themeState,
        cipher = cipher,
        settings = settings,
    )
}

@Preview(name = "LIGHT/RUSSIAN")
@Composable
private fun SettingsCipherLightRuPreview() {
    val themeState = ThemeState(
        colorsType = ColorsType.LIGHT,
        language = Language.RUSSIAN,
    )
    val cipher = SecurityService(provider = "foo", algorithm = "baz")
    val settings = SecuritySettings(
        pbeIterations = SecuritySettings.PBEIterations.NUMBER_2_10,
        aesKeyLength = SecuritySettings.AESKeyLength.BITS_256,
        dsaKeyLength = SecuritySettings.DSAKeyLength.BITS_1024_3,
        hasBiometric = true,
    )
    SettingsCipherPreview(
        themeState = themeState,
        cipher = cipher,
        settings = settings,
    )
}
