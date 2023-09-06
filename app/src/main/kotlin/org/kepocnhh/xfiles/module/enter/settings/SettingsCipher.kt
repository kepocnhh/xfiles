package org.kepocnhh.xfiles.module.enter.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.kepocnhh.xfiles.App

@Composable
internal fun SettingsCipher() {
    val viewModel = App.viewModel<SettingsViewModel>()
    val cipher by viewModel.cipher.collectAsState(null)
    if (cipher == null) {
        viewModel.requestCipher()
    }
    // SecretKeyFactory
    // KeyPairGenerator
    // Signature
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(App.Theme.sizes.xl)
            .padding(start = App.Theme.sizes.small, end = App.Theme.sizes.small),
    ) {
        BasicText(
            modifier = Modifier.align(Alignment.CenterStart),
            style = TextStyle(
                color = App.Theme.colors.foreground,
                fontSize = 14.sp,
            ),
            text = "Cipher:",
        )
        BasicText(
            modifier = Modifier.align(Alignment.CenterEnd),
            style = TextStyle(
                color = App.Theme.colors.foreground,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
            ),
            text = cipher?.algorithm.orEmpty(),
        )
    }
}
