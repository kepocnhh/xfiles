package org.kepocnhh.xfiles.util.compose

import androidx.compose.foundation.Indication
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.kepocnhh.xfiles.App
import sp.ax.jc.clicks.clicks

internal object Keyboard {
    enum class Fun {
        SPACE_BAR,
        BACKSPACE,
    }

    val letters = listOf(
        charArrayOf('1', '2', '3', '4', '5', '6', '7', '8', '9', '0'),
        charArrayOf('q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p'),
        charArrayOf('a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l'),
        charArrayOf('z', 'x', 'c', 'v', 'b', 'n', 'm'),
    )

    val special = listOf(
        33..40,
        41..47,
        58..64,
        (91..96) + (123..126),
    ).map { ints ->
        ints.map { it.toChar() }.toCharArray()
    }
}

private fun Char.up(): Char {
    if (!Character.isLowerCase(this)) return this
    val uppercased = uppercaseChar()
    if (Character.isUpperCase(uppercased)) return uppercased
    return this
}

fun Modifier.foo(
    enabled: Boolean = true,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
): Modifier {
    return composed {
        Modifier.foo(
            enabled = enabled,
            interactionSource = remember { MutableInteractionSource() },
            indication = LocalIndication.current,
            onClick = onClick,
            onLongClick = onLongClick,
        )
    }
}

fun Modifier.foo(
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource,
    indication: Indication,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
): Modifier {
    return composed {
        val onClickState = rememberUpdatedState(onClick)
        val onLongClickState = rememberUpdatedState(onLongClick)
//        val currentPressInteractions = remember { mutableStateListOf<PressInteraction.Press>() }
        val lastPressState = remember { mutableStateOf<PressInteraction.Press?>(null) }
//        val currentPressState = remember { mutableStateOf<PressInteraction.Press?>(null) }
//        LaunchedEffect(currentPressState.value, lastPressState.value) {
//            val currentPress = currentPressState.value
//            val lastPress = lastPressState.value
//            if (currentPress == null) {
//                // noop
//            } else if (lastPress == null) {
//                lastPressState.value = currentPress
//            } else if (lastPress != currentPress) {
//                println("[Foo]: press: " + lastPress.hashCode() + " last is not current")
//                interactionSource.emit(PressInteraction.Cancel(lastPress))
//                lastPressState.value = currentPress
//            }
//        }
        LaunchedEffect(lastPressState.value, enabled) {
            val lastPress = lastPressState.value
            if (lastPress != null && !enabled) {
                println("[Foo]: press: " + lastPress.hashCode() + " cancel") // todo
                interactionSource.emit(PressInteraction.Cancel(lastPress))
                lastPressState.value = null
            }
        }
        Modifier
            .indication(interactionSource = interactionSource, indication = indication)
//            .hoverable(enabled = enabled, interactionSource = interactionSource)
//            .focusable(enabled = enabled, interactionSource = interactionSource)
            .pointerInput(enabled, interactionSource) {
                detectTapGestures(
                    onPress = { offset ->
                        if (enabled) {
                            val press = PressInteraction.Press(offset)
                            println("[Foo]: press: " + press.hashCode()) // todo
                            lastPressState.value = press
                            interactionSource.emit(press)
                            if (tryAwaitRelease()) {
                                interactionSource.emit(PressInteraction.Release(press))
                            } else {
                                interactionSource.emit(PressInteraction.Cancel(press))
                            }
                            lastPressState.value = null
                        }
                    },
                    onLongPress = {
                        if (enabled) onLongClickState.value()
                    },
                    onTap = {
                        if (enabled) onClickState.value()
                    },
                )
            }
    }
}

@Composable
private fun KeyboardRow(
    height: Dp,
    enabled: Boolean,
    chars: CharArray,
    onClick: (Char) -> Unit,
    textStyle: TextStyle,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(height),
    ) {
        chars.forEach {
            BasicText(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
//                    .combinedClickable() // todo
                    .foo(
                        enabled = enabled,
                        onClick = {
                            onClick(it)
                        },
                        onLongClick = {
                            onClick(it.up())
                        },
                    ) // todo
                    .wrapContentHeight(),
                text = it.toString(),
                style = textStyle,
            )
        }
    }
}

@Composable
internal fun KeyboardRows(
    modifier: Modifier,
    enabled: Boolean,
    rowHeight: Dp = App.Theme.sizes.xl,
    rows: List<CharArray>,
    onClick: (Char) -> Unit,
) {
    Column(modifier = modifier) {
        rows.forEach { chars ->
            KeyboardRow(
                height = rowHeight,
                enabled = enabled,
                chars = chars,
                onClick = onClick,
                textStyle = TextStyle(
                    color = App.Theme.colors.text,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp, // todo
                ),
            )
        }
    }
}

@Composable
internal fun Keyboard(
    modifier: Modifier,
    enabled: Boolean,
    rowHeight: Dp = App.Theme.sizes.xl,
    rows: List<CharArray>,
    keyTextStyle: TextStyle = TextStyle(
        color = App.Theme.colors.text,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace,
        fontSize = 14.sp, // todo
    ),
    onClick: (Char) -> Unit,
    onClickFun: (Keyboard.Fun) -> Unit,
    onLongClickFun: (Keyboard.Fun) -> Unit,
) {
    Column(modifier = modifier) {
        rows.forEach { chars ->
            KeyboardRow(
                height = rowHeight,
                enabled = enabled,
                chars = chars,
                onClick = onClick,
                textStyle = keyTextStyle,
            )
        }
        val textStyle = TextStyle(
            color = App.Theme.colors.text,
            textAlign = TextAlign.Center,
            fontSize = 14.sp, // todo
        ) // todo
        Row(modifier = Modifier.height(rowHeight)) {
            Spacer(modifier = Modifier.width(64.dp))
            BasicText(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .clickable(enabled = enabled) {
                        onClickFun(Keyboard.Fun.SPACE_BAR)
                    }
                    .wrapContentHeight(),
                text = "space",
                style = textStyle,
            )
            BasicText(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(64.dp)
                    .clicks(
                        enabled = enabled,
                        onClick = {
                            onClickFun(Keyboard.Fun.BACKSPACE)
                        },
                        onLongClick = {
                            onLongClickFun(Keyboard.Fun.BACKSPACE)
                        },
                    )
                    .wrapContentHeight(),
                text = "<",
                style = textStyle,
            ) // todo icon
        }
    }
}
