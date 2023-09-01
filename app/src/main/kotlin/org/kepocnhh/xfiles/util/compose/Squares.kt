package org.kepocnhh.xfiles.util.compose

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

private fun Float.ct(k: Float): Float {
    return (this % k + k) % k
}

@Composable
internal fun Squares(
    modifier: Modifier = Modifier,
    color: Color,
    width: Dp,
    padding: Dp,
    radius: Dp,
) {
    val alphaState = remember { mutableStateOf(1f) }
    Canvas(modifier = modifier) {
        val size = Size(width.toPx(), width.toPx())
        val cornerRadius = CornerRadius(x = radius.toPx(), y = radius.toPx())
        drawRoundRect(
            color = color.copy(alpha = alphaState.value.ct(1f)),
            topLeft = Offset(
                x = - size.width - padding.toPx() / 2,
                y = - size.height - padding.toPx() / 2,
            ),
            size = size,
            cornerRadius = cornerRadius,
        )
        drawRoundRect(
            color = color.copy(alpha = (alphaState.value - 0.25f).ct(1f)),
            topLeft = Offset(
                x = padding.toPx() / 2,
                y = - size.height - padding.toPx() / 2,
            ),
            size = size,
            cornerRadius = cornerRadius,
        )
        drawRoundRect(
            color = color.copy(alpha = (alphaState.value - 0.5f).ct(1f)),
            topLeft = Offset(
                x = padding.toPx() / 2,
                y = padding.toPx() / 2,
            ),
            size = size,
            cornerRadius = cornerRadius,
        )
        drawRoundRect(
            color = color.copy(alpha = (alphaState.value - 0.75f).ct(1f)),
            topLeft = Offset(
                x = - size.width - padding.toPx() / 2,
                y = padding.toPx() / 2,
            ),
            size = size,
            cornerRadius = cornerRadius,
        )
    }
    LaunchedEffect(alphaState.value) {
        withContext(Dispatchers.Default) {
            delay(16)
        }
        alphaState.value = (alphaState.value + 0.025f).ct(1f)
    }
}
