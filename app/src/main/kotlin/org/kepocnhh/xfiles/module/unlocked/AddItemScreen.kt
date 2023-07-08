package org.kepocnhh.xfiles.module.unlocked

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.kepocnhh.xfiles.util.compose.Keyboard
import sp.ax.jc.clicks.onClick

@Composable
internal fun AddItemScreen(
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
            .background(Color.Green),
    ) {
        val text = remember { mutableStateOf("") }
        BasicText(
            modifier = Modifier
                .padding(top = 64.dp),
            text = text.value,
        )
        Keyboard(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 64.dp),
            onClick = {
                text.value += it
            },
            onBackspace = {
                if (text.value.isNotEmpty()) {
                    text.value = text.value.substring(0, text.value.lastIndex)
                }
            }
        )
        // todo
    }
}
