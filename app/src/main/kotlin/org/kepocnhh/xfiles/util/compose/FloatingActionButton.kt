package org.kepocnhh.xfiles.util.compose

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.Indication
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import org.kepocnhh.xfiles.App
import org.kepocnhh.xfiles.module.app.Colors

@Composable
internal fun FloatingActionButton(
    size: Dp = App.Theme.sizes.xxxl,
    color: Color = App.Theme.colors.secondary,
    enabled: Boolean,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    indication: Indication? = LocalIndication.current,
    @DrawableRes icon: Int,
    iconSize: Dp = App.Theme.sizes.medium,
    iconColor: Color = App.Theme.colors.icon,
    contentDescription: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(size)
            .shadow(
                elevation = App.Theme.sizes.xs,
                shape = RoundedCornerShape(size / 2),
            )
            .background(color, RoundedCornerShape(size / 2))
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = indication,
                onClick = onClick,
            ),
    ) {
        Image(
            modifier = Modifier
                .size(iconSize)
                .align(Alignment.Center),
            painter = painterResource(id = icon),
            contentDescription = contentDescription,
            colorFilter = ColorFilter.tint(iconColor),
        )
    }
}
