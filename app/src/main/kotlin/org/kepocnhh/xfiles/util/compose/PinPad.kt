package org.kepocnhh.xfiles.util.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import sp.ax.jc.clicks.clicks

@Composable
private fun PinRow(
    modifier: Modifier = Modifier,
    first: Char,
    second: Char,
    third: Char,
    textStyle: TextStyle,
    onClick: (Char) -> Unit,
) {
    Row(modifier = modifier) {
        BasicText(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .clickable {
                    onClick(first)
                }
                .wrapContentHeight(),
            text = "$first",
            style = textStyle,
        )
        BasicText(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .clickable {
                    onClick(second)
                }
                .wrapContentHeight(),
            text = "$second",
            style = textStyle,
        )
        BasicText(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .clickable {
                    onClick(third)
                }
                .wrapContentHeight(),
            text = "$third",
            style = textStyle,
        )
    }
}

@Composable
internal fun PinPad(
    modifier: Modifier = Modifier,
    rowHeight: Dp,
    textStyle: TextStyle,
    onClick: (Char) -> Unit,
    onDelete: () -> Unit,
    onDeleteLong: () -> Unit,
) {
    Column(modifier = modifier) {
        PinRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(rowHeight),
            '1', '2', '3',
            textStyle = textStyle,
            onClick = onClick,
        )
        PinRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(rowHeight),
            '4', '5', '6',
            textStyle = textStyle,
            onClick = onClick,
        )
        PinRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(rowHeight),
            '7', '8', '9',
            textStyle = textStyle,
            onClick = onClick,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(rowHeight),
        ) {
            Spacer(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .wrapContentHeight(),
            )
            val char = '0'
            BasicText(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .clickable {
                        onClick(char)
                    }
                    .wrapContentHeight(),
                text = "$char",
                style = textStyle,
            )
            BasicText(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .clicks(
                        onClick = onDelete,
                        onLongClick = onDeleteLong,
                    )
                    .wrapContentHeight(),
                text = "x",
                style = textStyle,
            ) // todo icon
        }
    }
}
