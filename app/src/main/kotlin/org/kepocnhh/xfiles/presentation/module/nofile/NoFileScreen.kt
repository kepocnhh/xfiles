package org.kepocnhh.xfiles.presentation.module.nofile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import org.kepocnhh.xfiles.App
import org.kepocnhh.xfiles.Text

@Composable
internal fun NoFileScreen() {
    Box(Modifier.fillMaxSize()) {
        val logger = App.newLogger(tag = "[NoFile]")
        Text(text = "no file", color = App.Theme.colors.text, align = TextAlign.Center)
    }
}
