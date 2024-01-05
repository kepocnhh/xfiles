package org.kepocnhh.xfiles.module.enter.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.kepocnhh.xfiles.App
import org.kepocnhh.xfiles.BuildConfig
import org.kepocnhh.xfiles.entity.SecurityService
import org.kepocnhh.xfiles.entity.SecuritySettings
import org.kepocnhh.xfiles.module.app.ThemeState
import org.kepocnhh.xfiles.module.theme.ThemeViewModel

@Composable
internal fun SettingsScreen(onBack: () -> Unit) {
    BackHandler {
        onBack()
    }
    val themeViewModel = App.viewModel<ThemeViewModel>()
    val themeState = themeViewModel.state.collectAsState().value
    val viewModel = App.viewModel<SettingsViewModel>()
    val databaseExists = viewModel.databaseExists.collectAsState(null).value
    val cipher = viewModel.cipher.collectAsState(null).value
    val settings = viewModel.settings.collectAsState(null).value
    LaunchedEffect(Unit) {
        if (themeState == null) {
            themeViewModel.requestThemeState()
        }
    }
    LaunchedEffect(Unit) {
        if (databaseExists == null) {
            viewModel.requestDatabase()
        }
    }
    LaunchedEffect(Unit) {
        if (cipher == null) {
            viewModel.requestCipher()
        }
    }
    LaunchedEffect(Unit) {
        if (settings == null) {
            viewModel.requestSettings()
        }
    }
    if (themeState == null) return
    if (databaseExists == null) return
    if (cipher == null) return
    if (settings == null) return
    SettingsScreen(
        themeState = themeState,
        onThemeState = themeViewModel::setThemeState,
        databaseExists = databaseExists,
        cipher = cipher,
        settings = settings,
        onSettings = viewModel::setSettings,
    )
}

@Suppress("LongParameterList")
@Composable
internal fun SettingsScreen(
    themeState: ThemeState,
    onThemeState: (ThemeState) -> Unit,
    databaseExists: Boolean,
    cipher: SecurityService,
    settings: SecuritySettings,
    onSettings: (SecuritySettings) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(App.Theme.colors.background),
    ) {
        Column(modifier = Modifier.align(Alignment.Center)) {
            SettingsColors(
                themeState = themeState,
                onColorsType = {
                    onThemeState(themeState.copy(colorsType = it))
                },
            )
            SettingsLanguage(
                themeState = themeState,
                onLanguage = {
                    onThemeState(themeState.copy(language = it))
                },
            )
            SettingsCipher(
                editable = !databaseExists,
                cipher = cipher,
                settings = settings,
                onSettings = onSettings,
            )
            SettingsVersion(
                versionName = BuildConfig.VERSION_NAME,
                versionCode = BuildConfig.VERSION_CODE,
            )
        }
    }
}
