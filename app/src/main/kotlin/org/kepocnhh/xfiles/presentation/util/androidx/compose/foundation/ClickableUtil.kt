package org.kepocnhh.xfiles.presentation.util.androidx.compose.foundation

import androidx.compose.foundation.Indication
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.pointerInput

internal fun Modifier.onLongClick(
    interactionSource: MutableInteractionSource,
    indication: Indication,
    block: () -> Unit,
): Modifier {
    return indication(interactionSource = interactionSource, indication = indication)
        .pointerInput(null) {
            detectTapGestures(
                onPress = {
                    val press = PressInteraction.Press(it)
                    interactionSource.emit(press)
                    tryAwaitRelease()
                    interactionSource.emit(PressInteraction.Release(press))
                },
                onLongPress = {
                    block()
                },
            )
        }
}

internal fun Modifier.onLongClick(block: () -> Unit): Modifier {
    return composed {
        Modifier.onLongClick(
            interactionSource = remember { MutableInteractionSource() },
            indication = LocalIndication.current,
            block = block,
        )
    }
}

internal fun Modifier.onClick(
    interactionSource: MutableInteractionSource,
    indication: Indication,
    block: () -> Unit,
): Modifier {
    return indication(interactionSource = interactionSource, indication = indication)
        .pointerInput(null) {
            detectTapGestures(
                onPress = {
                    val press = PressInteraction.Press(it)
                    interactionSource.emit(press)
                    tryAwaitRelease()
                    interactionSource.emit(PressInteraction.Release(press))
                },
                onTap = {
                    block()
                },
            )
        }
}

internal fun Modifier.onClick(block: () -> Unit): Modifier {
    return composed {
        Modifier.onClick(
            interactionSource = remember { MutableInteractionSource() },
            indication = LocalIndication.current,
            block = block,
        )
    }
}

internal fun Modifier.clicks(
    interactionSource: MutableInteractionSource,
    indication: Indication,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
): Modifier {
    return indication(interactionSource = interactionSource, indication = indication)
        .pointerInput(null) {
            detectTapGestures(
                onPress = {
                    val press = PressInteraction.Press(it)
                    interactionSource.emit(press)
                    tryAwaitRelease()
                    interactionSource.emit(PressInteraction.Release(press))
                },
                onLongPress = {
                    onLongClick()
                },
                onTap = {
                    onClick()
                },
            )
        }
}

internal fun Modifier.clicks(
    onClick: () -> Unit = {},
    onLongClick: () -> Unit,
): Modifier {
    return composed {
        Modifier.clicks(
            interactionSource = remember { MutableInteractionSource() },
            indication = LocalIndication.current,
            onClick = onClick,
            onLongClick = onLongClick,
        )
    }
}
