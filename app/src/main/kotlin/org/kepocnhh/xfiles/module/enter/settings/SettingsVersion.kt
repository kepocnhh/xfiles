package org.kepocnhh.xfiles.module.enter.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import org.kepocnhh.xfiles.App
import org.kepocnhh.xfiles.BuildConfig

@Composable
internal fun SettingsVersion() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(App.Theme.sizes.xxxl)
            .padding(horizontal = App.Theme.sizes.small),
    ) {
        BasicText(
            modifier = Modifier.align(Alignment.CenterStart),
            style = App.Theme.textStyle,
            text = App.Theme.strings.settings.version,
        )
        BasicText(
            modifier = Modifier.align(Alignment.Center),
            style = App.Theme.textStyle.copy(
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
            ),
            text = BuildConfig.VERSION_NAME,
        )
        BasicText(
            modifier = Modifier.align(Alignment.CenterEnd),
            style = App.Theme.textStyle.copy(
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
            ),
            text = BuildConfig.VERSION_CODE.toString(),
        )
    }
}
