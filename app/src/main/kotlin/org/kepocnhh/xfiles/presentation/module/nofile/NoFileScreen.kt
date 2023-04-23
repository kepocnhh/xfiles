package org.kepocnhh.xfiles.presentation.module.nofile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.kepocnhh.xfiles.App
import org.kepocnhh.xfiles.implementation.module.nofile.NoFileVewModel

@Composable
internal fun NoFileScreen(onCreate: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(App.Theme.colors.background),
    ) {
        val logger = App.newLogger(tag = "[NoFile]")
        val viewModel = App.viewModel<NoFileVewModel>()
        logger.debug("view model: ${viewModel.hashCode()}")
        val broadcast by viewModel.broadcast.collectAsState(null)
        logger.debug("broadcast: $broadcast")
        when (broadcast) {
            NoFileVewModel.Broadcast.Create -> {
                onCreate()
            }
            null -> {
                // noop
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
        ) {
            BasicText(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .wrapContentHeight(),
                text = "no file",
                style = TextStyle(
                    textAlign = TextAlign.Center,
                    color = App.Theme.colors.text,
                ),
            )
            BasicText(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clickable {
                        viewModel.newFile()
                    }
                    .wrapContentHeight(),
                text = "+",
                style = TextStyle(
                    textAlign = TextAlign.Center,
                    color = App.Theme.colors.primary,
                ),
            )
        }
    }
}
