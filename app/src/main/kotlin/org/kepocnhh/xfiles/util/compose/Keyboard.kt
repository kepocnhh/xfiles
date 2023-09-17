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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.kepocnhh.xfiles.App
import sp.ax.jc.clicks.clicks

internal object Keyboard {
    enum class Fun {
        SPACE_BAR,
        BACKSPACE,
    }

    val letters = listOf(
        (48..57).map { it.toChar() }.toCharArray(),
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

private fun Char.up(): Char {
    if (!Character.isLowerCase(this)) return this
    val uppercased = uppercaseChar()
    if (Character.isUpperCase(uppercased)) return uppercased
    return this
}

@Composable
private fun KeyboardRow(
    modifier: Modifier,
    enabled: Boolean,
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
                        enabled = enabled,
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
    modifier: Modifier,
    enabled: Boolean,
    rowHeight: Dp = App.Theme.sizes.xl,
    rows: List<CharArray>,
    keyTextStyle: TextStyle = TextStyle(
        color = App.Theme.colors.text,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace,
        fontSize = 14.sp, // todo
    ),
    onClick: (Char) -> Unit,
    onClickFun: (Keyboard.Fun) -> Unit,
    onLongClickFun: (Keyboard.Fun) -> Unit,
) {
    Column(modifier = modifier) {
        rows.forEach { chars ->
            KeyboardRow(
                modifier = Modifier.height(rowHeight),
                enabled = enabled,
                chars = chars,
                onClick = onClick,
                textStyle = keyTextStyle,
            )
        }
        val textStyle = TextStyle(
            color = App.Theme.colors.text,
            textAlign = TextAlign.Center,
            fontSize = 14.sp, // todo
        ) // todo
        Row(modifier = Modifier.height(rowHeight)) {
            Spacer(modifier = Modifier.width(64.dp))
            BasicText(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .clickable(enabled = enabled) {
                        onClickFun(Keyboard.Fun.SPACE_BAR)
                    }
                    .wrapContentHeight(),
                text = "space",
                style = textStyle,
            )
            BasicText(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(64.dp)
                    .clicks(
                        enabled = enabled,
                        onClick = {
                            onClickFun(Keyboard.Fun.BACKSPACE)
                        },
                        onLongClick = {
                            onLongClickFun(Keyboard.Fun.BACKSPACE)
                        },
                    )
                    .wrapContentHeight(),
                text = "<",
                style = textStyle,
            ) // todo icon
        }
    }
}
