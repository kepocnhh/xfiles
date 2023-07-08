package org.kepocnhh.xfiles.util.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
private fun DrawChars(actual: CharArray, chars: CharArray) {
    if (actual.size > 32) TODO()
    if (chars.size > 32) TODO()
    println("actual: ${actual.toList()}")
    println("chars: ${chars.toList()}")
//    if (actual.contentEquals(chars)) return
    var index = 0
    val durationMillis = 250
    for (i in 0 until 12) {
        val before = actual.getOrNull(i)
        val after = chars.getOrNull(i)
        println("before: $before")
//        if (before == null && after == null) break
        println("after: $after")
        val visible: Boolean
        val state = remember { mutableStateOf(after) }
        if (before == null && after != null) {
            visible = true
            state.value = after
        } else {
            visible = before?.equals(after) ?: false
            state.value = after ?: before
        }
        AnimatedVisibility(
            visible = visible,
            enter = slideInVertically(tween(durationMillis), initialOffsetY = { it })
                    + fadeIn(tween(durationMillis)),
            exit = slideOutVertically(tween(durationMillis), targetOffsetY = { it })
                    + fadeOut(tween(durationMillis)),
        ) {
            val char = state.value
            BasicText(
                modifier = Modifier,
                text = char.toString(),
            )
        }
    }
}

@Composable
internal fun AnimatedText(
    state: State<Pair<String, String>>,
) {
    val (actual, chars) = state.value
    Row(modifier = Modifier) {
        DrawChars(actual = actual.toCharArray(), chars = chars.toCharArray())
    }
}
