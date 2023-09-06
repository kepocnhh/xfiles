package org.kepocnhh.xfiles.module.enter.settings

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import org.kepocnhh.xfiles.App

@Composable
internal fun SettingsCipher() {
    // Cipher
    // SecretKeyFactory
    // KeyPairGenerator
    // Signature
    BasicText(
        modifier = Modifier
            .fillMaxWidth()
            .height(App.Theme.sizes.xxxl),
        style = TextStyle(
            color = App.Theme.colors.foreground,
            fontSize = 14.sp,
        ),
        text = "Cipher: ...",
    )
}
