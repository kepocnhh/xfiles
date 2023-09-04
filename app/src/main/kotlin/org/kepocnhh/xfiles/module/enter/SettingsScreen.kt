package org.kepocnhh.xfiles.module.enter

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import org.kepocnhh.xfiles.App
import org.kepocnhh.xfiles.R
import org.kepocnhh.xfiles.module.app.Colors
import org.kepocnhh.xfiles.module.app.ColorsType
import org.kepocnhh.xfiles.module.theme.ThemeViewModel
import org.kepocnhh.xfiles.util.compose.AnimatedFadeVisibility
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
private fun getColors(colorsType: ColorsType): Colors {
    return when (colorsType) {
        ColorsType.DARK -> Colors.Dark
        ColorsType.LIGHT -> Colors.Light
        ColorsType.AUTO -> if (isSystemInDarkTheme()) {
            Colors.Dark
        } else {
            Colors.Light
        }
    }
}

@Composable
private fun getIcon(colorsType: ColorsType): Int {
    return when (colorsType) {
        ColorsType.DARK -> R.drawable.moon
        ColorsType.LIGHT -> R.drawable.sun
        ColorsType.AUTO -> if (isSystemInDarkTheme()) {
            R.drawable.moon
        } else {
            R.drawable.sun
        }
    }
}

@Composable
private fun getText(colorsType: ColorsType): String {
    return when (colorsType) {
        ColorsType.DARK -> App.Theme.strings.dark
        ColorsType.LIGHT -> App.Theme.strings.light
        ColorsType.AUTO -> App.Theme.strings.auto
    }
}

@Composable
private fun SettingsColorRow(
    colorsType: ColorsType,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val colors = getColors(colorsType)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(App.Theme.sizes.xxxl)
            .background(colors.background)
            .clickable(onClick = onClick),
    ) {
        Image(
            modifier = Modifier
                .padding(start = App.Theme.sizes.small)
                .size(App.Theme.sizes.medium)
                .align(Alignment.CenterStart),
            painter = painterResource(id = getIcon(colorsType)),
            contentDescription = "colors:row:icon",
            colorFilter = ColorFilter.tint(colors.foreground),
        )
        BasicText(
            modifier = Modifier.align(Alignment.Center),
            style = TextStyle(
                color = colors.foreground,
                fontSize = 14.sp,
            ),
            text = getText(colorsType),
        )
        if (selected) {
            Image(
                modifier = Modifier
                    .padding(end = App.Theme.sizes.small)
                    .size(App.Theme.sizes.medium)
                    .align(Alignment.CenterEnd),
                painter = painterResource(id = R.drawable.check),
                contentDescription = "colors:row:check",
                colorFilter = ColorFilter.tint(colors.foreground),
            )
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
        Image(
            modifier = Modifier
                .padding(start = App.Theme.sizes.small)
                .size(App.Theme.sizes.medium)
                .align(Alignment.CenterStart),
            painter = painterResource(id = getIcon(theme.colorsType)),
            contentDescription = "colors:icon",
            colorFilter = ColorFilter.tint(App.Theme.colors.foreground),
        )
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
                .padding(end = App.Theme.sizes.small)
                .align(Alignment.CenterEnd),
            style = TextStyle(
                color = App.Theme.colors.foreground,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
            ),
            text = getText(theme.colorsType),
        )
    }
    if (dialogState.value) {
        Dialog(
            onDismissRequest = {
                dialogState.value = false
            },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = App.Theme.colors.background,
                        shape = RoundedCornerShape(App.Theme.sizes.medium),
                    )
                    .padding(
                        top = App.Theme.sizes.medium,
                        bottom = App.Theme.sizes.medium,
                    ),
            ) {
                setOf(
                    ColorsType.LIGHT,
                    ColorsType.DARK,
                    ColorsType.AUTO,
                ).forEach { colorsType ->
                    SettingsColorRow(
                        colorsType = colorsType,
                        selected = theme.colorsType == colorsType,
                        onClick = {
                            themeViewModel.setColorsType(colorsType)
                            dialogState.value = false
                        }
                    )
                }
            }
        }
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
