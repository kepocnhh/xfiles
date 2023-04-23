package org.kepocnhh.xfiles.presentation.module.onfile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import org.kepocnhh.xfiles.App
import org.kepocnhh.xfiles.implementation.module.onfile.OnFileViewModel
import org.kepocnhh.xfiles.presentation.util.androidx.compose.foundation.clicks

@Composable
internal fun OnFileScreen(onDelete: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(App.Theme.colors.background),
    ) {
        val logger = App.newLogger(tag = "[Items]")
        val viewModel = App.viewModel<OnFileViewModel>()
        val broadcast by viewModel.broadcast.collectAsState(null)
        when (broadcast) {
            OnFileViewModel.Broadcast.Delete -> {
                onDelete()
            }
            null -> {
                // noop
            }
        }
        when (val items = viewModel.state.collectAsState().value) {
            null -> {
                viewModel.requestItems()
            }
            else -> {
                if (items.isEmpty()) {
                    BasicText(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(App.Theme.dimensions.sizes.xxl)
                            .wrapContentHeight()
                            .align(Alignment.Center),
                        text = "no items",
                        style = TextStyle(
                            textAlign = TextAlign.Center,
                            color = App.Theme.colors.text,
                        ),
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth()
                            .padding(top = App.Theme.dimensions.insets.top)
                    ) {
                        val keys = items.keys.toList()
                        items(
                            count = keys.size,
                            key = { index -> keys[index] }
                        ) { index ->
                            val key = keys[index]
                            BasicText(
                                modifier = Modifier.fillMaxWidth(),
                                text = key,
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    color = App.Theme.colors.text,
                                ),
                            )
                            BasicText(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(App.Theme.dimensions.sizes.xxl)
                                    .clicks(
                                        onLongClick = {
                                            viewModel.deleteItem(key)
                                        }
                                    )
                                    .wrapContentHeight(),
                                text = checkNotNull(items[key]),
                                style = TextStyle(
                                    fontSize = 18.sp,
                                    color = App.Theme.colors.text,
                                ),
                            )
                        }
                    }
                }
            }
        }
        var newItem by remember { mutableStateOf(false) }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = App.Theme.dimensions.insets.bottom),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(App.Theme.dimensions.sizes.xxl)
                    .align(Alignment.BottomCenter),
            ) {
                BasicText(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .clickable {
                            newItem = true
                        }
                        .wrapContentHeight(),
                    text = "new item",
                    style = TextStyle(
                        textAlign = TextAlign.Center,
                        color = App.Theme.colors.primary,
                    ),
                )
                BasicText(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .clickable {
                            viewModel.deleteFile()
                        }
                        .wrapContentHeight(),
                    text = "delete",
                    style = TextStyle(
                        textAlign = TextAlign.Center,
                        color = App.Theme.colors.text,
                    ),
                )
            }
        }
        if (newItem) {
            NewItemScreen(
                onBack = {
                    newItem = false
                },
                onNewItem = {
                    newItem = false
                    viewModel.requestItems()
                },
            )
        }
    }
}