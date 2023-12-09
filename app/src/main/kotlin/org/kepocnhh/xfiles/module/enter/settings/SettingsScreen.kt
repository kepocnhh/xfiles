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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import org.kepocnhh.xfiles.App
import org.kepocnhh.xfiles.util.compose.requireLayoutDirection
import org.kepocnhh.xfiles.util.compose.toPaddings

@Composable
internal fun SettingsScreen(onBack: () -> Unit) {
    BackHandler {
        onBack()
    }
    when (val orientation = LocalConfiguration.current.orientation) {
        Configuration.ORIENTATION_PORTRAIT -> {
            SettingsScreenPortrait()
        }
        else -> {
            TODO("Orientation $orientation is not supported!")
        }
    }
}

@Composable
private fun SettingsScreenPortrait() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(App.Theme.colors.background),
    ) {
        val viewModel = App.viewModel<SettingsViewModel>()
        Column(modifier = Modifier.align(Alignment.Center)) {
            SettingsColors()
            SettingsLanguage()
            val exists = viewModel.databaseExists.collectAsState(null).value
            if (exists == null) {
                viewModel.requestDatabase()
            } else {
                SettingsCipher(editable = !exists)
            }
            SettingsVersion()
        }
    }
}
