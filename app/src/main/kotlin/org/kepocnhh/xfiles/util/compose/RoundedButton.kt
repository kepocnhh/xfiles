package org.kepocnhh.xfiles.util.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import org.kepocnhh.xfiles.App
import sp.ax.jc.clicks.onClick

@Composable
internal fun RoundedButton(
    margin: PaddingValues,
    padding: PaddingValues = PaddingValues(App.Theme.sizes.small),
    height: Dp = App.Theme.sizes.xxxl,
    color: Color = App.Theme.colors.secondary,
    corners: Dp = App.Theme.sizes.large,
    text: String,
    textColor: Color = App.Theme.colors.icon,
    fontSize: TextUnit = 14.sp, // todo
    fontWeight: FontWeight = FontWeight.Bold,
    textAlign: TextAlign = TextAlign.Center,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    RoundedButton(
        margin = margin,
        padding = padding,
        height = height,
        color = color,
        corners = corners,
        text = text,
        textStyle = TextStyle(
            color = textColor,
            fontSize = fontSize,
            fontWeight = fontWeight,
            textAlign = textAlign,
        ),
        enabled = enabled,
        onClick = onClick,
    )
}

@Composable
private fun RoundedButton(
    margin: PaddingValues,
    padding: PaddingValues,
    height: Dp,
    color: Color,
    corners: Dp,
    text: String,
    textStyle: TextStyle,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    BasicText(
        modifier = Modifier
            .padding(margin)
            .height(height)
            .background(color, RoundedCornerShape(corners))
            .clip(RoundedCornerShape(corners))
            .onClick(enabled = enabled, onClick)
            .wrapContentHeight()
            .padding(padding),
        text = text,
        style = textStyle,
    )
}
