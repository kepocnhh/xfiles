package org.kepocnhh.xfiles.util.compose

import androidx.compose.foundation.background
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import sp.ax.jc.clicks.clicks

private fun Long.even(): Boolean {
    return rem(2) == 0L
}

@Suppress("LongParameterList")
@Composable
internal fun TextFocused(
    margin: PaddingValues,
    padding: PaddingValues,
    enabled: Boolean,
    height: Dp,
    color: Color,
    corners: Dp,
    text: String,
    textStyle: TextStyle,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    focused: Boolean,
    contentDescription: String,
    valueDescription: String,
) {
//    val borderColor = if (focused) textStyle.color else Color.LightGray
    Box(
        modifier = Modifier
            .semantics {
                this.contentDescription = contentDescription
            }
            .padding(margin)
            .height(height)
            .fillMaxWidth()
            .background(color, RoundedCornerShape(corners))
//            .border(1.dp, borderColor, RoundedCornerShape(corners))
            .clip(RoundedCornerShape(corners))
            .clicks(
                enabled = enabled,
                onClick = onClick,
                onLongClick = onLongClick,
            )
            .padding(padding),
    ) {
        val timeStart = remember { mutableStateOf<Long?>(null) }
        val timeState = remember { mutableStateOf<Long?>(null) }
        LaunchedEffect(focused, timeState.value, timeStart.value) {
            if (focused) {
                if (timeStart.value == null) {
                    timeStart.value = System.currentTimeMillis()
                } else {
                    @Suppress("InjectDispatcher")
                    withContext(Dispatchers.Default) {
                        delay(100)
                    }
                    timeState.value = System.currentTimeMillis()
                }
            } else if (timeState.value != null) {
                timeState.value = null
            } else if (timeStart.value != null) {
                timeStart.value = null
            }
        }
        val start = timeStart.value
        val millis = timeState.value
        val value = when {
            !focused -> text
            start == null -> text
            millis == null -> text
            millis.minus(start).div(500).even() -> text + "_"
            else -> text
        }
        BasicText(
            modifier = Modifier
                .semantics {
                    this.contentDescription = valueDescription
                }
                .align(Alignment.CenterStart)
                .wrapContentHeight(),
            text = value,
            style = textStyle,
        )
    }
}
