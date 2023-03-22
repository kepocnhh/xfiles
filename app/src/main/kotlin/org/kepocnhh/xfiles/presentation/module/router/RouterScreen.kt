package org.kepocnhh.xfiles.presentation.module.router

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.kepocnhh.xfiles.App
import org.kepocnhh.xfiles.implementation.module.router.RouterViewModel

@Composable
internal fun RouterScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
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
    }
}
