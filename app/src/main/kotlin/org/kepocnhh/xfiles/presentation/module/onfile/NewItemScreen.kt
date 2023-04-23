package org.kepocnhh.xfiles.presentation.module.onfile

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.onLongClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.center
import androidx.compose.ui.unit.toOffset
import kotlinx.coroutines.launch
import org.kepocnhh.xfiles.App
import org.kepocnhh.xfiles.presentation.util.androidx.compose.foundation.catchClicks
import org.kepocnhh.xfiles.presentation.util.androidx.compose.foundation.combinedClickable

@Composable
private fun KeyboardRow(buttons: Set<Char>, onClick: (Char) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(App.Theme.dimensions.sizes.xl)) {
        for (char in buttons) {
//            val scope = rememberCoroutineScope()
            val source = remember { MutableInteractionSource() }
            val indication = LocalIndication.current
            val state = source.collectIsDraggedAsState()
//            val inputModeManager = LocalInputModeManager.current
//            val enabled = true
//            val offset = remember { mutableStateOf(Offset.Zero) }
//            val interactions = remember { mutableMapOf<Key, PressInteraction.Press>() }
            BasicText(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
//                    .clearAndSetSemantics {
//                        onClick {
//                            println("on click: $char")
//                            onClick(char)
//                            true
//                        }
//                        onLongClick {
//                            println("on long click: $char")
//                            char.takeIf { it.isLowerCase() }
//                                ?.uppercaseChar()
//                                ?.takeIf { it.isUpperCase() }
//                                ?.also(onClick) ?: onClick(char)
//                            true
//                        }
//                    }
//                    .pointerInput(source) {
//                        offset.value = size.center.toOffset()
//                    }
//                    .onKeyEvent { event ->
//                        when (event.type) {
//                            KeyEventType.KeyDown -> {
//                                val contains = interactions.containsKey(event.key)
//                                val it = PressInteraction.Press(offset.value).also {
//                                    interactions[event.key] = it
//                                }
//                                scope.launch { source.emit(it) }
//                                !contains
//                            }
//                            KeyEventType.KeyUp -> {
//                                interactions.remove(event.key)?.also {
//                                    scope.launch { source.emit(PressInteraction.Release(it)) }
//                                }
//                                true
//                            }
//                            else -> true
//                        }
//                    }
//                    .indication(interactionSource = source, indication)
//                    .hoverable(enabled = enabled, interactionSource = source)
//                    .focusProperties { canFocus = inputModeManager.inputMode != InputMode.Touch }
//                    .focusable(enabled = enabled, interactionSource = source)
//                    .pointerInput(null) {
//                        detectTapGestures(
//                            onLongPress = { _ ->
//                                println("on long press: $char")
//                                char.takeIf { it.isLowerCase() }
//                                    ?.uppercaseChar()
//                                    ?.takeIf { it.isUpperCase() }
//                                    ?.also(onClick) ?: onClick(char)
//                            },
//                            onTap = { _ ->
//                                onClick(char)
//                            },
//                        )
//                    }
                    .combinedClickable(
                        interactionSource = source,
                        indication = indication,
                        onClick = {
                            println("on click: $char")
                            onClick(char)
                        },
                        onLongClick = {
                            println("on long click: $char")
                            char
                                .takeIf { it.isLowerCase() }
                                ?.uppercaseChar()
                                ?.takeIf { it.isUpperCase() }
                                ?.also(onClick)
                        }
                    )
//                    .clickable {
//                        onClick(char)
//                    }
//                    .catchClicks()
                    .wrapContentHeight(),
                text = "$char",
                style = TextStyle(
                    textAlign = TextAlign.Center,
                    color = App.Theme.colors.text,
                ),
            )
        }
    }
}

@Composable
private fun Keyboard(onClick: (Char) -> Unit) {
    Column(Modifier.fillMaxWidth()) {
        listOf(
            "1234567890",
            "qwertyuiop",
            "asdfghjkl",
            "zxcvbnm",
        ).forEach { buttons ->
            KeyboardRow(
                buttons = buttons.toCharArray().toSet(),
                onClick = onClick
            )
        }
    }
}

@Composable
internal fun NewItemScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(App.Theme.colors.background),
    ) {
        var value by remember { mutableStateOf("") }
        Column(
            Modifier
                .fillMaxSize()
                .padding(bottom = App.Theme.dimensions.insets.bottom)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                BasicText(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(App.Theme.dimensions.sizes.xxl)
                        .wrapContentHeight()
                        .align(Alignment.Center),
                    text = value,
                    style = TextStyle(
                        textAlign = TextAlign.Center,
                        color = App.Theme.colors.text,
                    ),
                )
            }
            Keyboard(
                onClick = {
                    value += it
                }
            )
        }
    }
}
