package org.kepocnhh.xfiles.util.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import org.kepocnhh.xfiles.App
import sp.ax.jc.clicks.clicks

internal object Keyboard {
    enum class Fun {
        SPACE_BAR,
        BACKSPACE,
    }

    val letters = listOf(
        charArrayOf('1', '2', '3', '4', '5', '6', '7', '8', '9', '0'),
        charArrayOf('q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p'),
        charArrayOf('a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l'),
        charArrayOf('z', 'x', 'c', 'v', 'b', 'n', 'm'),
    )

    val special = listOf(
        33..40,
        41..47,
        58..64,
        (91..96) + (123..126),
    ).map { ints ->
        ints.map { it.toChar() }.toCharArray()
    }
}

@Suppress("FunctionMinLength")
private fun Char.up(): Char {
    if (!Character.isLowerCase(this)) return this
    val uppercased = uppercaseChar()
    if (Character.isUpperCase(uppercased)) return uppercased
    return this
}

@Composable
private fun KeyboardRow(
    height: Dp,
    enabled: Boolean,
    chars: CharArray,
    onClick: (Char) -> Unit,
    textStyle: TextStyle,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(height),
    ) {
        chars.forEach { char ->
            BasicText(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .clicks(
                        enabled = enabled,
                        onClick = {
                            onClick(char)
                        },
                        onLongClick = {
                            onClick(char.up())
                        },
                    )
                    .wrapContentHeight(),
                text = char.toString(),
                style = textStyle,
            )
        }
    }
}

@Composable
internal fun KeyboardRows(
    modifier: Modifier,
    enabled: Boolean,
    rowHeight: Dp = App.Theme.sizes.xl,
    rows: List<CharArray>,
    onClick: (Char) -> Unit,
) {
    Column(modifier = modifier) {
        rows.forEach { chars ->
            KeyboardRow(
                height = rowHeight,
                enabled = enabled,
                chars = chars,
                onClick = onClick,
                textStyle = TextStyle(
                    color = App.Theme.colors.text,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp, // todo
                ),
            )
        }
    }
}
