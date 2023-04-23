package org.kepocnhh.xfiles.presentation.util.androidx.compose.ui.window

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

internal val LocalDialogStyle = staticCompositionLocalOf {
    DialogStyle(
        background = Color.White,
        paddings = PaddingValues(
            bottom = 16.dp
        ),
        minWidth = 128.dp,
        corners = 16.dp,
        button = DialogStyle.Button(
            paddings = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 16.dp,
                bottom = 16.dp,
            ),
            corners = 16.dp,
            fontSize = 14.sp,
            textColor = Color.Black
        ),
        message = DialogStyle.Message(
            paddings = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 16.dp,
                bottom = 16.dp,
            ),
            fontSize = 16.sp,
            textColor = Color.Black
        ),
        buttons = DialogStyle.Buttons(
            alignment = Alignment.End,
            space = 16.dp
        )
    )
}

internal data class DialogStyle(
    val background: Color,
    val paddings: PaddingValues,
    val message: Message,
    val button: Button,
    val buttons: Buttons,
    val minWidth: Dp,
    val corners: Dp,
) {
    data class Message(
        val paddings: PaddingValues,
        val fontSize: TextUnit,
        val textColor: Color,
    )
    data class Button(
        val paddings: PaddingValues,
        val corners: Dp,
        val fontSize: TextUnit,
        val textColor: Color,
    )
    data class Buttons(
        val alignment: Alignment.Horizontal,
        val space: Dp,
    )
}

@Composable
internal fun Dialog(
    onDismissRequest: () -> Unit,
    style: DialogStyle = LocalDialogStyle.current,
    message: String,
    buttons: Set<String>,
    onClick: (Int) -> Unit,
) {
    check(buttons.isNotEmpty())
    Dialog(onDismissRequest = onDismissRequest) {
        Column(
            modifier = Modifier.fillMaxWidth()
//                .defaultMinSize(minWidth = style.minWidth)
                .background(
                    color = style.background,
                    shape = RoundedCornerShape(style.corners)
                )
                .padding(style.paddings),
        ) {
            BasicText(
                modifier = Modifier.padding(style.message.paddings),
                text = message,
                style = TextStyle(
                    fontSize = style.message.fontSize,
                    color = style.message.textColor,
                ),
            )
            Row(
                modifier = Modifier
                    .align(style.buttons.alignment),
            ) {
                for ((index, name) in buttons.withIndex()) {
                    Spacer(modifier = Modifier.width(style.buttons.space))
                    BasicText(
                        modifier = Modifier
                            .clip(RoundedCornerShape(style.button.corners))
                            .clickable {
                                onClick(index)
                            }
                            .padding(style.button.paddings)
                            .wrapContentHeight(),
                        text = name,
                        style = TextStyle(
                            textAlign = TextAlign.Center,
                            fontSize = style.button.fontSize,
                            color = style.button.textColor,
                        )
                    )
                }
                Spacer(modifier = Modifier.width(style.buttons.space))
            }
        }
    }
}
