package org.kepocnhh.xfiles.module.unlocked

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.kepocnhh.xfiles.util.compose.AnimatedText
import org.kepocnhh.xfiles.util.compose.Keyboard
import org.kepocnhh.xfiles.util.compose.TextFocused
import sp.ax.jc.clicks.onClick
import sp.ax.jc.dialogs.Dialog

private enum class Focused {
    KEY, VALUE,
}

private fun ClipData.getFirstTextOrNull(): String? {
    for (i in 0 until itemCount) {
        val text = getItemAt(i).text ?: continue
        return text.toString()
    }
    return null
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
        val textDialogState = remember { mutableStateOf(false) }
        val context = LocalContext.current
        if (textDialogState.value) {
            Dialog(
                "paste" to {
                    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = clipboardManager.primaryClip
                    val text = clip?.getFirstTextOrNull()
                    if (text != null) {
                        when (focusedState.value) {
                            Focused.KEY -> {
                                keyState.value = text
                            }
                            Focused.VALUE -> {
                                valueState.value = text
                            }
                        }
                    }
                    textDialogState.value = false
                },
                "clear" to {
                    when (focusedState.value) {
                        Focused.KEY -> {
                            keyState.value = ""
                        }
                        Focused.VALUE -> {
                            valueState.value = ""
                        }
                    }
                    textDialogState.value = false
                },
                message = "?",
                onDismissRequest = { textDialogState.value = false },
            )
        }
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
                onLongClick = {
                    when (focusedState.value) {
                        Focused.KEY -> {
                            textDialogState.value = true
                        }
                        Focused.VALUE -> {
                            focusedState.value = Focused.KEY
                        }
                    }
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
                onLongClick = {
                    when (focusedState.value) {
                        Focused.KEY -> {
                            focusedState.value = Focused.VALUE
                        }
                        Focused.VALUE -> {
                            textDialogState.value = true
                        }
                    }
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
