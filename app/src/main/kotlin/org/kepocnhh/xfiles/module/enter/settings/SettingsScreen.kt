package org.kepocnhh.xfiles.module.enter.settings

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import org.kepocnhh.xfiles.App
import org.kepocnhh.xfiles.util.compose.requireLayoutDirection
import org.kepocnhh.xfiles.util.compose.toPaddings

internal object SettingsScreen {
    val LocalSizes = staticCompositionLocalOf<Sizes> { error("no sizes") }

    data class Sizes(val rowHeight: Dp)
}

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
private fun Columns(modifier: Modifier, sizes: SettingsScreen.Sizes) {
    val viewModel = App.viewModel<SettingsViewModel>()
    CompositionLocalProvider(
        SettingsScreen.LocalSizes provides sizes,
    ) {
        Column(modifier = modifier) {
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

@Composable
private fun SettingsScreenPortrait() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(App.Theme.colors.background),
    ) {
        Columns(
            modifier = Modifier.align(Alignment.Center),
            sizes = SettingsScreen.Sizes(rowHeight = App.Theme.sizes.xxxl),
        )
    }
}

@Composable
private fun SettingsScreenLandscape() {
    val insets = LocalView.current.rootWindowInsets.toPaddings()
    val layoutDirection = LocalConfiguration.current.requireLayoutDirection()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(App.Theme.colors.background)
            .padding(end = insets.calculateEndPadding(layoutDirection)),
    ) {
        Columns(
            modifier = Modifier.align(Alignment.Center),
            sizes = SettingsScreen.Sizes(rowHeight = App.Theme.sizes.xl),
        )
    }
}
