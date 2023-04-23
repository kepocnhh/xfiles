package org.kepocnhh.xfiles.presentation.module.router

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import org.kepocnhh.xfiles.App
import org.kepocnhh.xfiles.implementation.module.router.RouterViewModel
import org.kepocnhh.xfiles.presentation.module.nofile.NoFileScreen
import org.kepocnhh.xfiles.presentation.module.onfile.OnFileScreen

@Composable
internal fun RouterScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        val viewModel = App.viewModel<RouterViewModel>()
        when (val state = viewModel.state.collectAsState().value) {
            null -> {
                viewModel.requestState()
            }
            else -> {
                when (state.exists) {
                    true -> {
                        OnFileScreen(
                            onDelete = {
                                viewModel.requestState()
                            }
                        )
                    }
                    false -> {
                        NoFileScreen(
                            onCreate = {
                                viewModel.requestState()
                            }
                        )
                    }
                }
            }
        }
    }
}
