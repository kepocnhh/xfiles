package org.kepocnhh.xfiles.module.enter.settings

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import org.kepocnhh.xfiles.App
import org.kepocnhh.xfiles.util.compose.requireLayoutDirection

@Composable
internal fun SettingsScreen(onBack: () -> Unit) {
    BackHandler {
        onBack()
    }
    when (LocalConfiguration.current.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            SettingsScreenLandscape()
        }
        else -> {
            SettingsScreenPortrait()
        }
    }
}

@Composable
private fun Columns(modifier: Modifier) {
    Column(modifier = modifier) {
        SettingsColors()
        SettingsLanguage()
        SettingsCipher()
    }
}

@Composable
private fun SettingsScreenPortrait() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(App.Theme.colors.background),
    ) {
        Columns(modifier = Modifier.align(Alignment.Center))
    }
}

@Composable
private fun SettingsScreenLandscape() {
    val layoutDirection = LocalConfiguration.current.requireLayoutDirection()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(App.Theme.colors.background)
            .padding(end = App.Theme.dimensions.insets.calculateEndPadding(layoutDirection)),
    ) {
        Columns(modifier = Modifier.align(Alignment.Center))
    }
}
