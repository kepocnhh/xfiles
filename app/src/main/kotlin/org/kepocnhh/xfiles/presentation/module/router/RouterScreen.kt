package org.kepocnhh.xfiles.presentation.module.router

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.kepocnhh.xfiles.App
import org.kepocnhh.xfiles.implementation.module.router.RouterViewModel
import org.kepocnhh.xfiles.presentation.module.nofile.NoFileScreen
import org.kepocnhh.xfiles.presentation.util.androidx.compose.ToScreen

@Composable
internal fun RouterScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        val logger = App.newLogger(tag = "[Router]")
        val viewModel = App.viewModel<RouterViewModel>()
        val exists by viewModel.state.collectAsState()
        when (exists) {
            false -> {
                logger.debug("no file")
                ToScreen {
                    NoFileScreen()
                }
            }
            true -> {
                logger.debug("file exists")
                TODO()
            }
            null -> {
                logger.debug("request file...")
                viewModel.requestFile()
            }
        }
    }
}
