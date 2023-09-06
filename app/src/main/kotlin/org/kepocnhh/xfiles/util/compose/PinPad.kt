package org.kepocnhh.xfiles.util.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import org.kepocnhh.xfiles.App
import org.kepocnhh.xfiles.R

@Composable
private fun PinRow(
    modifier: Modifier = Modifier,
    enabled: Boolean,
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
    enabled: Boolean,
    visibleDelete: Boolean,
    onDelete: () -> Unit,
    onSettings: () -> Unit,
    onClick: (Char) -> Unit,
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
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .let {
                        if (enabled) {
                            it.clickable {
                                onSettings()
                            }
                        } else {
                            it
                        }
                    },
            ) {
                Image(
                    modifier = Modifier
                        .size(App.Theme.sizes.medium)
                        .align(Alignment.Center),
                    painter = painterResource(id = R.drawable.gear),
                    contentDescription = "delete",
                    colorFilter = ColorFilter.tint(textStyle.color),
                )
            }
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
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .let {
                        if (enabled && visibleDelete) {
                            it.clickable {
                                onDelete()
                            }
                        } else {
                            it
                        }
                    },
            ) {
                AnimatedFadeVisibility(
                    modifier = Modifier
                        .align(Alignment.Center),
                    visible = visibleDelete,
                    duration = App.Theme.durations.animation,
                ) {
                    Image(
                        modifier = Modifier
                            .size(App.Theme.sizes.medium),
                        painter = painterResource(id = R.drawable.cross),
                        contentDescription = "delete",
                        colorFilter = ColorFilter.tint(textStyle.color),
                        contentScale = ContentScale.Fit,
                    )
                }
            }
        }
    }
}
