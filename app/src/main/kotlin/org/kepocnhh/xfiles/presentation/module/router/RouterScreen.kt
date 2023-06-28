package org.kepocnhh.xfiles.presentation.module.router

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
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
                AnimatedVisibility(
                    visible = state.exists,
                    enter = slideInHorizontally()
                            + expandHorizontally(expandFrom = Alignment.End)
                            + fadeIn(),
                    exit = slideOutHorizontally(targetOffsetX = { it })
                            + shrinkHorizontally()
                            + fadeOut(),
                ) {
                    OnFileScreen(
                        onDelete = {
                            viewModel.requestState()
                        }
                    )
                }
//                if (!state.exists) {
//                    NoFileScreen(
//                        onCreate = {
//                            viewModel.requestState()
//                        }
//                    )
//                }
                AnimatedVisibility(
                    visible = !state.exists,
                    enter = slideInHorizontally()
                            + expandHorizontally(expandFrom = Alignment.End)
                            + fadeIn(),
                    exit = slideOutHorizontally(targetOffsetX = { it })
                            + shrinkHorizontally()
                            + fadeOut(),
                ) {
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
