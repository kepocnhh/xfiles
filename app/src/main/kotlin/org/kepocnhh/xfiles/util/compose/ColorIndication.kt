package org.kepocnhh.xfiles.util.compose

import androidx.compose.foundation.Indication
import androidx.compose.foundation.IndicationInstance
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.runtime.State
import androidx.compose.runtime.remember

internal class ColorIndication(
    private val pressed: Color,
    private val hovered: Color,
) : Indication {
    companion object {
        fun create(color: Color): Indication {
            return ColorIndication(
                pressed = color.copy(alpha = .2f),
                hovered = color.copy(alpha = .1f),
            )
        }
    }

    private class ColorIndicationInstance(
        private val pressed: Color,
        private val hovered: Color,
        private val isPressed: State<Boolean>,
        private val isHovered: State<Boolean>,
        private val isFocused: State<Boolean>,
    ) : IndicationInstance {
        override fun ContentDrawScope.drawIndication() {
            drawContent()
            if (isPressed.value) {
                drawRect(color = pressed, size = size)
            } else if (isHovered.value || isFocused.value) {
                drawRect(color = hovered, size = size)
            }
        }
    }

    @Composable
    override fun rememberUpdatedInstance(interactionSource: InteractionSource): IndicationInstance {
        val isPressed = interactionSource.collectIsPressedAsState()
        val isHovered = interactionSource.collectIsHoveredAsState()
        val isFocused = interactionSource.collectIsFocusedAsState()
        return remember(interactionSource, pressed, hovered) {
            ColorIndicationInstance(
                pressed = pressed,
                hovered = hovered,
                isPressed = isPressed,
                isHovered = isHovered,
                isFocused = isFocused,
            )
        }
    }
}
