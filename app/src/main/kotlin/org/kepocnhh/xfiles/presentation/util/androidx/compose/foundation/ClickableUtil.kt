package org.kepocnhh.xfiles.presentation.util.androidx.compose.foundation

import androidx.compose.foundation.Indication
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.pointerInput

internal val NoneMutableInteractionSource = MutableInteractionSource()

internal fun Modifier.onClick(
    interactionSource: MutableInteractionSource = NoneMutableInteractionSource,
    onClick: () -> Unit
): Modifier {
    return clickable(
        interactionSource = interactionSource,
        indication = null,
        onClick = onClick
    )
}

internal fun Modifier.catchClicks(): Modifier {
    return onClick(onClick = {})
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
