package org.kepocnhh.xfiles.presentation.module.onfile

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import org.kepocnhh.xfiles.App

@Composable
private fun KeyboardRow(buttons: Set<Char>, onClick: (Char) -> Unit) {
    Row(Modifier.fillMaxWidth().height(App.Theme.dimensions.sizes.xl)) {
        for (it in buttons) {
            BasicText(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .clickable {
                        onClick(it)
                    }
                    .wrapContentHeight(),
                text = "$it",
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

@Composable
internal fun NewItemScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(App.Theme.colors.background),
    ) {
        var value by remember { mutableStateOf("") }
        Column(Modifier.fillMaxSize().padding(bottom = App.Theme.dimensions.insets.bottom)) {
            Box(
                modifier = Modifier.fillMaxWidth()
                    .weight(1f),
            ) {
                BasicText(
                    modifier = Modifier.fillMaxWidth()
                        .height(App.Theme.dimensions.sizes.xxl)
                        .wrapContentHeight()
                        .align(Alignment.Center),
                    text = value,
                    style = TextStyle(
                        textAlign = TextAlign.Center,
                        color = App.Theme.colors.text,
                    ),
                )
            }
            Keyboard(
                onClick = {
                    value += it
                }
            )
        }
    }
}
