package org.kepocnhh.xfiles.util.compose

import androidx.compose.foundation.Indication
import androidx.compose.foundation.IndicationInstance
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope

internal class ColorIndication(
    private val pressed: Color,
    private val hovered: Color,
) : Indication {
    private class ColorIndicationInstance(
        private val pressed: Color,
        private val hovered: Color,
        private val pressedState: State<Boolean>,
        private val hoveredState: State<Boolean>,
        private val focusedState: State<Boolean>,
    ) : IndicationInstance {
        override fun ContentDrawScope.drawIndication() {
            drawContent()
            if (pressedState.value) {
                drawRect(color = pressed, size = size)
            } else if (hoveredState.value || focusedState.value) {
                drawRect(color = hovered, size = size)
            }
        }
    }

    @Composable
    override fun rememberUpdatedInstance(interactionSource: InteractionSource): IndicationInstance {
        val pressedState = interactionSource.collectIsPressedAsState()
        val hoveredState = interactionSource.collectIsHoveredAsState()
        val focusedState = interactionSource.collectIsFocusedAsState()
        return remember(interactionSource, pressed, hovered) {
            ColorIndicationInstance(
                pressed = pressed,
                hovered = hovered,
                pressedState = pressedState,
                hoveredState = hoveredState,
                focusedState = focusedState,
            )
        }
    }

    companion object {
        fun create(color: Color): Indication {
            return ColorIndication(
                pressed = color.copy(alpha = .2f),
                hovered = color.copy(alpha = .1f),
            )
        }
    }
}
