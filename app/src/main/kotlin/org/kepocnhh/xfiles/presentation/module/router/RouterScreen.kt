package org.kepocnhh.xfiles.presentation.module.router

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import org.kepocnhh.xfiles.App
import org.kepocnhh.xfiles.Text
import org.kepocnhh.xfiles.implementation.module.router.RouterViewModel

@Composable
internal fun RouterScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "foo bar",
                color = App.Theme.colors.text,
                align = TextAlign.Center,
            )
        }
        /*
        val logger = App.newLogger(tag = "[Router]")
        val viewModel = App.viewModel<RouterViewModel>()
        val state by viewModel.state.collectAsState()
        when (state) {
            false -> {
                logger.d("no file")
                TODO()
            }
            true -> {
                logger.d("file exists")
                TODO()
            }
            null -> {
                logger.d("request file...")
                viewModel.requestFile()
            }
        }
        */
    }
}
