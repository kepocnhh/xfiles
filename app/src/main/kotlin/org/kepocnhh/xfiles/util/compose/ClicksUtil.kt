package org.kepocnhh.xfiles.util.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.platform.inspectable

private fun Modifier.catchClicks(
    interactionSource: MutableInteractionSource,
): Modifier {
    return inspectable(
        inspectorInfo = debugInspectorInfo {
            name = "catchClicks"
            properties["interactionSource"] = interactionSource
        },
    ) {
        Modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    // noop
                },
            )
    }
}

internal fun Modifier.catchClicks(): Modifier {
    return composed {
        Modifier.catchClicks(
            interactionSource = remember { MutableInteractionSource() },
        )
    }
}
