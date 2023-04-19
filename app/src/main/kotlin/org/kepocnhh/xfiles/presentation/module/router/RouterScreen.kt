package org.kepocnhh.xfiles.presentation.module.router

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import org.kepocnhh.xfiles.App
import org.kepocnhh.xfiles.implementation.module.router.RouterViewModel
import org.kepocnhh.xfiles.presentation.module.items.ItemsScreen
import org.kepocnhh.xfiles.presentation.module.nofile.NoFileScreen
import org.kepocnhh.xfiles.presentation.util.androidx.compose.ToScreen

@Composable
internal fun RouterScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        val logger = App.newLogger(tag = "[Router]")
        val viewModel = App.viewModel<RouterViewModel>()
        val exists = viewModel.state.collectAsState().value
        if (exists == null) {
            logger.debug("request file...")
            viewModel.requestFile()
        } else {
            var toNoFile by rememberSaveable { mutableStateOf(!exists) }
            var toItems by rememberSaveable { mutableStateOf(exists) }
            val fromNoFileState = rememberSaveable { mutableStateOf(false) }
            val fromItemsState = rememberSaveable { mutableStateOf(false) }
            if (exists) {
                toItems = true
                fromItemsState.value = false
            } else {
                toNoFile = true
                fromNoFileState.value = false
            }
            logger.debug("on file $exists")
            logger.debug("to no file: $toNoFile")
//            logger.debug("to items: $toItems")
            if (toNoFile) {
                logger.debug("to no file")
                ToScreen(
                    backState = fromNoFileState,
                    onBack = {
                        logger.debug("on back from no file")
                        toNoFile = false
                        viewModel.requestFile()
                    },
                ) {
                    NoFileScreen(
                        onCreate = {
                            fromNoFileState.value = true
                        }
                    )
                }
            }
            if (toItems) {
                logger.debug("to items")
                ToScreen(
                    backState = fromItemsState,
                    onBack = {
                        logger.debug("on back from items")
                        toItems = false
                        viewModel.requestFile()
                    },
                ) {
                    ItemsScreen(
                        onDelete = {
                            fromItemsState.value = true
                        }
                    )
                }
            }
        }
    }
}
