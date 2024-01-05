package org.kepocnhh.xfiles.util.compose

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import org.kepocnhh.xfiles.App
import org.kepocnhh.xfiles.R
import sp.ax.jc.animations.tween.fade.FadeVisibility
import sp.ax.jc.clicks.onClick

@Composable
private fun RowScope.PinButton(
    char: Char,
    enabled: Boolean,
    textStyle: TextStyle,
    onClick: (Char) -> Unit,
) {
    BasicText(
        modifier = Modifier
            .semantics {
                role = Role.Button
                contentDescription = "pin:pad:button:$char"
            }
            .fillMaxHeight()
            .weight(1f)
            .clickable(enabled = enabled) {
                onClick(char)
            }
            .wrapContentHeight(),
        text = "$char",
        style = textStyle,
    )
}

@Suppress("LongParameterList")
@Composable
private fun PinRow(
    modifier: Modifier,
    enabled: Boolean,
    first: Char,
    second: Char,
    third: Char,
    textStyle: TextStyle,
    onClick: (Char) -> Unit,
) {
    Row(modifier = modifier) {
        PinButton(
            char = first,
            enabled = enabled,
            textStyle = textStyle,
            onClick = onClick,
        )
        PinButton(
            char = second,
            enabled = enabled,
            textStyle = textStyle,
            onClick = onClick,
        )
        PinButton(
            char = third,
            enabled = enabled,
            textStyle = textStyle,
            onClick = onClick,
        )
    }
}

internal class PinPad {
    data class Listeners(
        val onSettings: () -> Unit,
        val onClick: (Char) -> Unit,
        val onDelete: () -> Unit,
        val onBiometric: () -> Unit,
    )
}

@Suppress("LongParameterList")
@Composable
private fun RowScope.PinPadImages(
    enabled: Boolean,
    visibleDelete: Boolean,
    hasBiometric: Boolean,
    exists: Boolean,
    color: Color,
    onDelete: () -> Unit,
    onBiometric: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .weight(1f)
            .clickable(enabled = enabled) {
                if (visibleDelete) {
                    onDelete()
                } else if (hasBiometric && exists) {
                    onBiometric()
                }
            },
    ) {
        FadeVisibility(
            modifier = Modifier.align(Alignment.Center),
            visible = visibleDelete,
        ) {
            PinPadImage(
                id = R.drawable.cross,
                contentDescription = "delete",
                color = color,
            )
        }
        FadeVisibility(
            modifier = Modifier.align(Alignment.Center),
            visible = !visibleDelete && hasBiometric && exists,
        ) {
            PinPadImage(
                id = R.drawable.biometric,
                contentDescription = "biometric",
                color = color,
            )
        }
    }
}

@Composable
private fun PinPadImage(
    @DrawableRes id: Int,
    contentDescription: String,
    color: Color,
) {
    Image(
        modifier = Modifier
            .size(App.Theme.sizes.medium),
        painter = painterResource(id = id),
        contentDescription = contentDescription,
        colorFilter = ColorFilter.tint(color),
        contentScale = ContentScale.Fit,
    )
}

@Suppress("LongParameterList")
@Composable
private fun PinPadBottom(
    rowHeight: Dp,
    textStyle: TextStyle,
    enabled: Boolean,
    listeners: PinPad.Listeners,
    hasBiometric: Boolean,
    exists: Boolean,
    visibleDelete: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(rowHeight),
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .onClick(enabled = enabled, block = listeners.onSettings),
        ) {
            Image(
                modifier = Modifier
                    .size(App.Theme.sizes.medium)
                    .align(Alignment.Center),
                painter = painterResource(id = R.drawable.gear),
                contentDescription = "settings",
                colorFilter = ColorFilter.tint(textStyle.color),
            )
        }
        val char = '0'
        PinButton(
            char = char,
            enabled = enabled,
            textStyle = textStyle,
            onClick = listeners.onClick,
        )
        PinPadImages(
            enabled = enabled,
            visibleDelete = visibleDelete,
            hasBiometric = hasBiometric,
            exists = exists,
            color = textStyle.color,
            onDelete = listeners.onDelete,
            onBiometric = listeners.onBiometric,
        )
    }
}

@Suppress("LongParameterList")
@Composable
internal fun PinPad(
    modifier: Modifier = Modifier,
    rowHeight: Dp,
    textStyle: TextStyle,
    enabled: Boolean,
    visibleDelete: Boolean,
    listeners: PinPad.Listeners,
    hasBiometric: Boolean,
    exists: Boolean,
) {
    Column(modifier = modifier) {
        PinRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(rowHeight),
            enabled = enabled,
            first = '1',
            second = '2',
            third = '3',
            textStyle = textStyle,
            onClick = listeners.onClick,
        )
        PinRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(rowHeight),
            enabled = enabled,
            first = '4',
            second = '5',
            third = '6',
            textStyle = textStyle,
            onClick = listeners.onClick,
        )
        PinRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(rowHeight),
            enabled = enabled,
            first = '7',
            second = '8',
            third = '9',
            textStyle = textStyle,
            onClick = listeners.onClick,
        )
        PinPadBottom(
            rowHeight = rowHeight,
            textStyle = textStyle,
            enabled = enabled,
            listeners = listeners,
            hasBiometric = hasBiometric,
            exists = exists,
            visibleDelete = visibleDelete,
        )
    }
}
