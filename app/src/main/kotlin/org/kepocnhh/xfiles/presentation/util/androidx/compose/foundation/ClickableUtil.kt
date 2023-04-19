package org.kepocnhh.xfiles.presentation.util.androidx.compose.foundation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.Modifier

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
