package org.kepocnhh.xfiles.util.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import sp.ax.jc.clicks.clicks

private fun Char.up(): Char {
    if (!Character.isLowerCase(this)) return this
    val uppercased = uppercaseChar()
    if (Character.isUpperCase(uppercased)) return uppercased
    return this
}

@Composable
private fun KeyboardRow(
    modifier: Modifier = Modifier,
    chars: CharArray,
    onClick: (Char) -> Unit,
    textStyle: TextStyle,
) {
    Row(modifier = modifier) {
        chars.forEach {
            BasicText(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .clicks(
                        onClick = {
                            onClick(it)
                        },
                        onLongClick = {
                            onClick(it.up())
                        }
                    )
                    .wrapContentHeight(),
                text = it.toString(),
                style = textStyle,
            )
        }
    }
}

@Composable
internal fun Keyboard(
    modifier: Modifier = Modifier,
    onClick: (Char) -> Unit,
    onBackspace: () -> Unit,
) {
    Column(modifier = modifier) {
        val height = 48.dp
        val textStyle = TextStyle(
            textAlign = TextAlign.Center,
        )
        listOf(
            charArrayOf('q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p'),
            charArrayOf('a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l'),
            charArrayOf('z', 'x', 'c', 'v', 'b', 'n', 'm'),
        ).forEach { chars ->
            KeyboardRow(
                modifier = Modifier.height(height),
                chars = chars,
                onClick = onClick,
                textStyle = textStyle,
            )
        }
        Row(modifier = Modifier.height(height)) {
            Spacer(modifier = Modifier.width(64.dp))
            BasicText(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .clickable {
                        onClick(' ')
                    }
                    .wrapContentHeight(),
                text = "space",
                style = textStyle,
            )
            BasicText(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(64.dp)
                    .clickable {
                        onBackspace()
                    }
                    .wrapContentHeight(),
                text = "<",
                style = textStyle,
            )
        }
    }
}
