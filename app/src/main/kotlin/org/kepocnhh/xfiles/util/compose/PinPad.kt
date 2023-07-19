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
    enabled: Boolean = true,
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
                .let {
                    if (enabled) {
                        it.clickable {
                            onClick(first)
                        }
                    } else {
                        it
                    }
                }
                .wrapContentHeight(),
            text = "$first",
            style = textStyle,
        )
        BasicText(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .let {
                    if (enabled) {
                        it.clickable {
                            onClick(second)
                        }
                    } else {
                        it
                    }
                }
                .wrapContentHeight(),
            text = "$second",
            style = textStyle,
        )
        BasicText(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .let {
                    if (enabled) {
                        it.clickable {
                            onClick(third)
                        }
                    } else {
                        it
                    }
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
    enabled: Boolean = true,
    onClick: (Char) -> Unit,
    onDelete: () -> Unit,
    onDeleteLong: () -> Unit,
) {
    Column(modifier = modifier) {
        PinRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(rowHeight),
            enabled = enabled,
            '1', '2', '3',
            textStyle = textStyle,
            onClick = onClick,
        )
        PinRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(rowHeight),
            enabled = enabled,
            '4', '5', '6',
            textStyle = textStyle,
            onClick = onClick,
        )
        PinRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(rowHeight),
            enabled = enabled,
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
                    .let {
                        if (enabled) {
                            it.clickable {
                                onClick(char)
                            }
                        } else {
                            it
                        }
                    }
                    .wrapContentHeight(),
                text = "$char",
                style = textStyle,
            )
            BasicText(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .let {
                        if (enabled) {
                            it.clicks(
                                onClick = {
                                    onDelete()
                                },
                                onLongClick = {
                                    onDeleteLong()
                                },
                            )
                        } else {
                            it
                        }
                    }
                    .wrapContentHeight(),
                text = "x",
                style = textStyle,
            ) // todo icon
        }
    }
}
