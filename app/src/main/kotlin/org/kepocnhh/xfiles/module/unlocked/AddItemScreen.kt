package org.kepocnhh.xfiles.module.unlocked

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.kepocnhh.xfiles.util.compose.AnimatedText
import org.kepocnhh.xfiles.util.compose.Keyboard
import org.kepocnhh.xfiles.util.compose.TextFocused
import sp.ax.jc.clicks.onClick

private enum class Focused {
    KEY, VALUE,
}

@Composable
internal fun AddItemScreen(
    keys: Set<String>,
    onAdd: (String, String) -> Unit,
    onCancel: () -> Unit,
) {
    BackHandler {
        onCancel()
    }
    Box(
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {},
            )
            .fillMaxSize()
            .background(Color.White),
    ) {
        val keyState = remember { mutableStateOf("") }
        val valueState = remember { mutableStateOf("") }
        val focusedState = remember { mutableStateOf(Focused.KEY) }
        Column(
            modifier = Modifier
                .padding(top = 64.dp)
        ) {
            BasicText(
                modifier = Modifier,
                text = "key:",
            )
            TextFocused(
                margin = PaddingValues(start = 16.dp, end = 16.dp),
                padding = PaddingValues(start = 16.dp, end = 16.dp),
                height = 56.dp,
                color = Color.White,
                corners = 16.dp,
                text = keyState.value,
                textStyle = TextStyle(
                    color = Color.Black,
                ),
                onClick = {
                    focusedState.value = Focused.KEY
                },
                focused = focusedState.value == Focused.KEY,
            )
            BasicText(
                modifier = Modifier,
                text = "value:",
            )
            TextFocused(
                margin = PaddingValues(start = 16.dp, end = 16.dp),
                padding = PaddingValues(start = 16.dp, end = 16.dp),
                height = 56.dp,
                color = Color.White,
                corners = 16.dp,
                text = valueState.value,
                textStyle = TextStyle(
                    color = Color.Black,
                ),
                onClick = {
                    focusedState.value = Focused.VALUE
                },
                focused = focusedState.value == Focused.VALUE,
            )
            BasicText(
                modifier = Modifier
                    .padding(16.dp)
                    .height(56.dp)
                    .fillMaxWidth()
                    .background(Color.Blue, RoundedCornerShape(16.dp))
                    .clickable {
                        if (keyState.value.isEmpty()) {
                            // todo
                        } else if (valueState.value.isEmpty()) {
                            // todo
                        } else if (keys.contains(keyState.value)) {
                            // todo
                        } else {
                            onAdd(keyState.value, valueState.value)
                        }
                    }
                    .wrapContentHeight(),
                text = "ok",
                style = TextStyle(
                    color = Color.White,
                    textAlign = TextAlign.Center,
                ),
            )
        }
        Keyboard(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 64.dp),
            onClick = {
                when (focusedState.value) {
                    Focused.KEY -> keyState.value += it
                    Focused.VALUE -> valueState.value += it
                }
            },
            onBackspace = {
                when (focusedState.value) {
                    Focused.KEY -> {
                        if (keyState.value.isNotEmpty()) {
                            keyState.value = keyState.value.take(keyState.value.lastIndex)
                        }
                    }
                    Focused.VALUE -> {
                        if (valueState.value.isNotEmpty()) {
                            valueState.value = valueState.value.take(valueState.value.lastIndex)
                        }
                    }
                }
            }
        )
        // todo
    }
}
