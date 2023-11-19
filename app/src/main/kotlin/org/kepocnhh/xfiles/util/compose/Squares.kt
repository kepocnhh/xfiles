package org.kepocnhh.xfiles.util.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.kepocnhh.xfiles.util.ct

@Composable
internal fun Squares(
    color: Color,
    width: Dp,
    padding: Dp,
    radius: Dp,
) {
    Squares(
        modifier = { Modifier.size(it) },
        color = color,
        width = width,
        padding = padding,
        radius = radius,
    )
}

@Composable
internal fun Squares(
    modifier: (DpSize) -> Modifier,
    color: Color,
    width: Dp,
    padding: Dp,
    radius: Dp,
) {
    val alphaState = remember { mutableFloatStateOf(1f) }
    val size = DpSize(width = width + padding + width, height = width + padding + width)
    Canvas(
        modifier = modifier(size),
    ) {
        val squareSize = Size(width.toPx(), width.toPx())
        val cornerRadius = CornerRadius(x = radius.toPx(), y = radius.toPx())
        val paddingOffset = Offset(x = padding.toPx(), y = padding.toPx())
        drawRoundRect(
            color = color.copy(alpha = (alphaState.floatValue - 0.00f).ct(1f)),
            topLeft = Offset.Zero,
            size = squareSize,
            cornerRadius = cornerRadius,
        )
        drawRoundRect(
            color = color.copy(alpha = (alphaState.floatValue - 0.25f).ct(1f)),
            topLeft = Offset.Zero.copy(x = squareSize.width + paddingOffset.x),
            size = squareSize,
            cornerRadius = cornerRadius,
        )
        drawRoundRect(
            color = color.copy(alpha = (alphaState.floatValue - 0.75f).ct(1f)),
            topLeft = Offset.Zero.copy(y = squareSize.height + paddingOffset.y),
            size = squareSize,
            cornerRadius = cornerRadius,
        )
        drawRoundRect(
            color = color.copy(alpha = (alphaState.floatValue - 0.50f).ct(1f)),
            topLeft = Offset(
                x = squareSize.width + paddingOffset.x,
                y = squareSize.height + paddingOffset.y,
            ),
            size = squareSize,
            cornerRadius = cornerRadius,
        )
    }
    LaunchedEffect(alphaState.floatValue) {
        withContext(Dispatchers.Default) {
            delay(16)
        }
        alphaState.floatValue = (alphaState.floatValue + 0.025f).ct(1f)
    }
}
