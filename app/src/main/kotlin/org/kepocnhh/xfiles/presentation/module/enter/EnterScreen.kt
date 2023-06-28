package org.kepocnhh.xfiles.presentation.module.enter

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.kepocnhh.xfiles.App
import org.kepocnhh.xfiles.showToast
import sp.ax.jc.clicks.onClick

@Composable
internal fun EnterScreen() {
    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = Modifier
            .fillMaxSize()
            .background(App.Theme.colors.background),
    ) {
        val context = LocalContext.current
        PinPad(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 128.dp),
            textStyle = TextStyle(
                textAlign = TextAlign.Center,
                color = App.Theme.colors.text,
                fontSize = 24.sp,
            ),
            onClick = { char ->
                context.showToast("click: $char")
            },
        )
    }
}

@Composable
private fun PinRow(
    height: Dp,
    first: Char,
    second: Char,
    third: Char,
    textStyle: TextStyle,
    onClick: (Char) -> Unit
) {
    PinRow(
        modifier = Modifier
            .fillMaxWidth()
            .height(height),
        first = first,
        second = second,
        third = third,
        textStyle = textStyle,
        onClick = onClick
    )
}

@Composable
private fun PinRow(
    modifier: Modifier = Modifier,
    first: Char,
    second: Char,
    third: Char,
    textStyle: TextStyle,
    onClick: (Char) -> Unit
) {
    Row(modifier) {
        BasicText(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .onClick {
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
                .onClick {
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
                .onClick {
                    onClick(third)
                }
                .wrapContentHeight(),
            text = "$second",
            style = textStyle,
        )
    }
}

@Composable
private fun PinPad(
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle(
        textAlign = TextAlign.Center,
        color = Color.Black
    ),
    onClick: (Char) -> Unit,
) {
    Column(modifier = modifier) {
        PinRow(
            height = App.Theme.dimensions.sizes.xxxl,
            '1', '2', '3', textStyle, onClick
        )
        PinRow(
            height = App.Theme.dimensions.sizes.xxxl,
            '4', '5', '6', textStyle, onClick
        )
        PinRow(
            height = App.Theme.dimensions.sizes.xxxl,
            '7', '8', '9', textStyle, onClick
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(App.Theme.dimensions.sizes.xxxl),
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
                    .onClick {
                        onClick(char)
                    }
                    .wrapContentHeight(),
                text = "$char",
                style = textStyle,
            )
            Spacer(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .wrapContentHeight(),
            )
        }
    }
}
