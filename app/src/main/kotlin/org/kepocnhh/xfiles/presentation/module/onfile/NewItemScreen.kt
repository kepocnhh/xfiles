package org.kepocnhh.xfiles.presentation.module.onfile

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import org.kepocnhh.xfiles.App
import org.kepocnhh.xfiles.implementation.module.onfile.NewItemViewModel
import org.kepocnhh.xfiles.presentation.util.androidx.compose.foundation.ButtonsRow
import org.kepocnhh.xfiles.showToast
import sp.ax.jc.clicks.clicks

@Composable
private fun KeyboardRow(buttons: Set<Char>, onClick: (Char) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(App.Theme.dimensions.sizes.xl),
    ) {
        for (char in buttons) {
            BasicText(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .clicks(
                        onClick = {
                            onClick(char)
                        },
                        onLongClick = {
                            if (char.isLowerCase()) {
                                val upper = char.uppercaseChar()
                                if (upper.isUpperCase()) {
                                    onClick(upper)
                                }
                            }
                        },
                    )
                    .wrapContentHeight(),
                text = "$char",
                style = TextStyle(
                    textAlign = TextAlign.Center,
                    color = App.Theme.colors.text,
                ),
            )
        }
    }
}

@Composable
private fun Keyboard(onClick: (Char) -> Unit) {
    Column(Modifier.fillMaxWidth()) {
        listOf(
            "1234567890",
            "qwertyuiop",
            "asdfghjkl",
            "zxcvbnm",
        ).forEach { buttons ->
            KeyboardRow(
                buttons = buttons.toCharArray().toSet(),
                onClick = onClick
            )
        }
    }
}

private enum class State {
    KEY,
    VALUE,
}

@Composable
internal fun NewItemScreen(
    onBack: () -> Unit,
    onNewItem: () -> Unit,
) {
    BackHandler {
        onBack()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(App.Theme.colors.background),
    ) {
        val context = LocalContext.current
        val viewModel = App.viewModel<NewItemViewModel>()
        val broadcast by viewModel.broadcast.collectAsState(null)
        when (broadcast) {
            NewItemViewModel.Broadcast.Error -> {
                context.showToast("Error!")
            }
            NewItemViewModel.Broadcast.Success -> {
                onNewItem()
            }
            null -> {
                // noop
            }
        }
        var state by remember { mutableStateOf(State.KEY) }
        var key by remember { mutableStateOf("") }
        var value by remember { mutableStateOf("") }
        Column(
            Modifier
                .fillMaxSize()
                .padding(bottom = App.Theme.dimensions.insets.calculateBottomPadding())) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center),
                ) {
                    BasicText(
                        modifier = Modifier.fillMaxWidth(),
                        text = "key:",
                        style = TextStyle(
                            fontSize = 14.sp,
                            color = App.Theme.colors.text,
                        ),
                    )
                    BasicText(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(App.Theme.dimensions.sizes.xxl)
                            .clickable {
                                state = State.KEY
                            }
                            .wrapContentHeight(),
                        text = key,
                        style = TextStyle(
                            fontSize = 18.sp,
                            color = App.Theme.colors.text,
                        ),
                    )
                    BasicText(
                        modifier = Modifier.fillMaxWidth(),
                        text = "value:",
                        style = TextStyle(
                            fontSize = 14.sp,
                            color = App.Theme.colors.text,
                        ),
                    )
                    BasicText(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(App.Theme.dimensions.sizes.xxl)
                            .clickable {
                                state = State.VALUE
                            }
                            .wrapContentHeight(),
                        text = value,
                        style = TextStyle(
                            fontSize = 18.sp,
                            color = App.Theme.colors.text,
                        ),
                    )
                }
            }
            Keyboard(
                onClick = {
                    when (state) {
                        State.KEY -> {
                            key += it
                        }
                        State.VALUE -> {
                            value += it
                        }
                    }
                }
            )
            ButtonsRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(App.Theme.dimensions.sizes.xxl),
                names = setOf("x", "<", "v"),
                onClick = { index ->
                    when (index) {
                        0 -> onBack()
                        1 -> {
                            when (state) {
                                State.KEY -> {
                                    if (key.isNotEmpty()) {
                                        key = key.substring(0, key.lastIndex)
                                    }
                                }
                                State.VALUE -> {
                                    if (value.isNotEmpty()) {
                                        value = value.substring(0, value.lastIndex)
                                    }
                                }
                            }
                        }
                        2 -> viewModel.add(key, value)
                    }
                }
            )
        }
    }
}
