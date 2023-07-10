package org.kepocnhh.xfiles.util.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import sp.ax.jc.clicks.clicks

@Composable
internal fun TextFocused(
    margin: PaddingValues,
    padding: PaddingValues,
    height: Dp,
    color: Color,
    corners: Dp,
    text: String,
    textStyle: TextStyle,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    focused: Boolean,
) {
    val borderColor = if (focused) textStyle.color else Color.LightGray
    Box(
        modifier = Modifier
            .padding(margin)
            .height(height)
            .fillMaxWidth()
            .background(color, RoundedCornerShape(corners))
            .border(1.dp, borderColor, RoundedCornerShape(corners))
            .clip(RoundedCornerShape(corners))
            .clicks(
                onClick = onClick,
                onLongClick = onLongClick,
            )
            .padding(padding),
    ) {
        val timeState = remember { mutableStateOf(System.currentTimeMillis()) }
        LaunchedEffect(key1 = focused, key2 = timeState.value) {
            if (focused) {
                withContext(Dispatchers.Default) {
                    delay(100)
                }
                timeState.value = System.currentTimeMillis()
            }
        }
        val seconds = timeState.value / 500
        val value = if (focused && seconds % 2 == 0L) {
            text + "_"
        } else {
            text
        }
        BasicText(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .wrapContentHeight(),
            text = value,
            style = textStyle,
        )
    }
}
