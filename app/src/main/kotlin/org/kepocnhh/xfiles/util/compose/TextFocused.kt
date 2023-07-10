package org.kepocnhh.xfiles.util.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle

@Composable
internal fun TextFocused(
    modifier: Modifier = Modifier,
    text: String,
    textStyle: TextStyle,
    focused: Boolean,
) {
    Box(modifier = modifier) {
        val value = if (focused) {
            text + "_"
        } else {
            text
        }
        BasicText(
            modifier = Modifier
                .align(Alignment.CenterStart),
            text = value,
            style = textStyle,
        )
//        val durationMillis = 250
//        AnimatedVisibility(
//            visible = focused,
//            enter = fadeIn(tween(durationMillis)),
//            exit = fadeOut(tween(durationMillis)),
//        ) {
//            Spacer(modifier = Modifier)
//        }
    }
}
