package org.kepocnhh.xfiles.module.enter.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import org.kepocnhh.xfiles.App
import org.kepocnhh.xfiles.R
import org.kepocnhh.xfiles.module.app.Language
import org.kepocnhh.xfiles.module.app.Strings
import org.kepocnhh.xfiles.module.app.ThemeState
import org.kepocnhh.xfiles.module.app.strings.En
import org.kepocnhh.xfiles.module.app.strings.Ru

@Composable
private fun getStrings(language: Language): Strings {
    return when (language) {
        Language.ENGLISH -> En
        Language.RUSSIAN -> Ru
        Language.AUTO -> {
            val locale = LocalConfiguration.current.locales.get(0)
            when (locale?.language) {
                "ru" -> Ru
                else -> En
            }
        }
    }
}

@Composable
private fun getIcon(language: Language): Int {
    return when (language) {
        Language.ENGLISH -> R.drawable.us
        Language.RUSSIAN -> R.drawable.ru
        Language.AUTO -> {
            val locale = LocalConfiguration.current.locales.get(0)
            when (locale?.language) {
                "ru" -> R.drawable.ru
                else -> R.drawable.us
            }
        }
    }
}

@Composable
private fun getText(language: Language): String {
    val strings = getStrings(language)
    return when (language) {
        Language.ENGLISH -> strings.english
        Language.RUSSIAN -> strings.russian
        Language.AUTO -> strings.auto
    }
}

@Composable
private fun SettingsLanguageRow(
    language: Language,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(App.Theme.sizes.xxxl)
            .background(App.Theme.colors.background)
            .clickable(onClick = onClick),
    ) {
        Image(
            modifier = Modifier
                .padding(start = App.Theme.sizes.small)
                .size(App.Theme.sizes.medium)
                .align(Alignment.CenterStart),
            painter = painterResource(id = getIcon(language)),
            contentDescription = "language:row:icon",
        )
        BasicText(
            modifier = Modifier.align(Alignment.Center),
            style = TextStyle(
                color = App.Theme.colors.foreground,
                fontSize = 14.sp,
            ),
            text = getText(language),
        )
        if (selected) {
            Image(
                modifier = Modifier
                    .padding(end = App.Theme.sizes.small)
                    .size(App.Theme.sizes.medium)
                    .align(Alignment.CenterEnd),
                painter = painterResource(id = R.drawable.check),
                contentDescription = "language:row:check",
                colorFilter = ColorFilter.tint(App.Theme.colors.foreground),
            )
        }
    }
}

@Composable
private fun SettingsLanguageDialog(
    selected: Language,
    onSelect: (Language) -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
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
                Language.ENGLISH,
                Language.RUSSIAN,
                Language.AUTO,
            ).forEach { language ->
                SettingsLanguageRow(
                    language = language,
                    selected = selected == language,
                    onClick = {
                        onSelect(language)
                        onDismiss()
                    },
                )
            }
        }
    }
}

@Composable
internal fun SettingsLanguage(
    themeState: ThemeState,
    onLanguage: (Language) -> Unit,
) {
    val dialogState = remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(App.Theme.sizes.xxxl)
            .clickable {
                dialogState.value = true
            },
    ) {
        Image(
            modifier = Modifier
                .padding(start = App.Theme.sizes.small)
                .size(App.Theme.sizes.medium)
                .align(Alignment.CenterStart),
            painter = painterResource(id = getIcon(themeState.language)),
            contentDescription = "language:icon",
        )
        BasicText(
            modifier = Modifier.align(Alignment.Center),
            style = TextStyle(
                color = App.Theme.colors.foreground,
                fontSize = 14.sp,
            ),
            text = App.Theme.strings.settings.language,
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
            text = getText(themeState.language),
        )
    }
    if (dialogState.value) {
        SettingsLanguageDialog(
            selected = themeState.language,
            onSelect = onLanguage,
            onDismiss = {
                dialogState.value = false
            },
        )
    }
}
