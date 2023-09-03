package org.kepocnhh.xfiles.module.enter

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.kepocnhh.xfiles.App
import org.kepocnhh.xfiles.module.app.ColorsType
import org.kepocnhh.xfiles.module.theme.ThemeViewModel

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
private fun SettingsColors() {
    val themeViewModel = App.viewModel<ThemeViewModel>()
    val theme = themeViewModel.state.collectAsState().value
    if (theme == null) {
        themeViewModel.requestThemeState()
        return
    }
    val dialogState = remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(App.Theme.sizes.xxxl)
            .clickable {
                dialogState.value = true
            }
    ) {
        BasicText(
            modifier = Modifier.align(Alignment.Center),
            style = TextStyle(
                color = App.Theme.colors.foreground,
                fontSize = 14.sp,
            ),
            text = App.Theme.strings.colors,
        )
        BasicText(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = App.Theme.sizes.s),
            style = TextStyle(
                color = App.Theme.colors.foreground,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
            ),
            text = when (theme.colorsType) {
                ColorsType.DARK -> App.Theme.strings.dark
                ColorsType.LIGHT -> App.Theme.strings.light
                ColorsType.AUTO -> App.Theme.strings.auto
            }
        )
    }
    if (dialogState.value) {
        // todo
    }
}

@Composable
private fun Columns(modifier: Modifier) {
    Column(modifier = modifier) {
        SettingsColors()
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
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(App.Theme.colors.background),
    ) {
        TODO()
    }
}
