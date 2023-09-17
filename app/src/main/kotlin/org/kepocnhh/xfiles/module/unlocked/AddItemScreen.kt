package org.kepocnhh.xfiles.module.unlocked

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.res.Configuration
import android.view.View
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.kepocnhh.xfiles.App
import org.kepocnhh.xfiles.module.app.Colors
import org.kepocnhh.xfiles.util.compose.Keyboard
import org.kepocnhh.xfiles.util.compose.TextFocused
import sp.ax.jc.clicks.onClick
import sp.ax.jc.dialogs.Dialog

private enum class Focused {
    KEY, VALUE,
}

private fun ClipData.getFirstTextOrNull(): String? {
    for (i in 0 until itemCount) {
        val text = getItemAt(i).text ?: continue
        return text.toString()
    }
    return null
}

@Composable
private fun TitledFocused(
    title: String,
    text: String,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    focused: Boolean,
) {
    BasicText(
        modifier = Modifier.padding(start = 32.dp, bottom = 8.dp),
        text = title,
    )
    TextFocused(
        margin = PaddingValues(start = 16.dp, end = 16.dp),
        padding = PaddingValues(start = 32.dp, end = 32.dp),
        height = 56.dp,
        color = Color.White,
        corners = 32.dp,
        text = text,
        textStyle = TextStyle(
            color = Color.Black,
        ),
        onClick = onClick,
        onLongClick = onLongClick,
        focused = focused,
    )
}

@Composable
private fun RoundedButton(
    margin: PaddingValues,
    padding: PaddingValues,
    backgroundColor: Color,
    corners: Dp,
    text: String,
    textColor: Color,
    onClick: () -> Unit,
) {
    BasicText(
        modifier = Modifier
            .padding(margin)
            .widthIn(min = 128.dp)
            .background(backgroundColor, RoundedCornerShape(corners))
            .clip(RoundedCornerShape(corners))
            .onClick(enabled = true, onClick)
            .wrapContentHeight()
            .padding(padding),
        text = text,
        style = TextStyle(
            color = textColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        ),
    )
}

@Deprecated(message = "AddItemScreen")
@Composable
internal fun AddItemScreenOld(
    keys: Set<String>,
    onAdd: (String, String) -> Unit,
    onCancel: () -> Unit,
) {
    BackHandler {
        onCancel()
    }
    val keyState = remember { mutableStateOf("") }
    val valueState = remember { mutableStateOf("") }
    val focusedState = remember { mutableStateOf(Focused.KEY) }
    val textDialogState = remember { mutableStateOf(false) }
    val context = LocalContext.current
    if (textDialogState.value) {
        Dialog(
            "paste" to {
                val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = clipboardManager.primaryClip
                val text = clip?.getFirstTextOrNull()
                if (text != null) {
                    when (focusedState.value) {
                        Focused.KEY -> {
                            keyState.value = text
                        }
                        Focused.VALUE -> {
                            valueState.value = text
                        }
                    }
                }
                textDialogState.value = false
            },
            "clear" to {
                when (focusedState.value) {
                    Focused.KEY -> {
                        keyState.value = ""
                    }
                    Focused.VALUE -> {
                        valueState.value = ""
                    }
                }
                textDialogState.value = false
            },
            message = "?",
            onDismissRequest = { textDialogState.value = false },
        )
    }
    when (val orientation = LocalConfiguration.current.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            AddItemScreenLandscape(
                keys = keys,
                keyState = keyState,
                valueState = valueState,
                focusedState = focusedState,
                textDialogState = textDialogState,
                onAdd = onAdd,
            )
        }
        Configuration.ORIENTATION_PORTRAIT -> {
            AddItemScreenPortraitOld(
                keys = keys,
                keyState = keyState,
                valueState = valueState,
                focusedState = focusedState,
                textDialogState = textDialogState,
                onAdd = onAdd,
            )
        }
        else -> error("Orientation $orientation is not supported!")
    }
}

@Composable
private fun AddItemScreenLandscape(
    keys: Set<String>,
    keyState: MutableState<String>,
    valueState: MutableState<String>,
    focusedState: MutableState<Focused>,
    textDialogState: MutableState<Boolean>,
    onAdd: (String, String) -> Unit,
) {
    val layoutDirection = when (val i = LocalConfiguration.current.layoutDirection) {
        View.LAYOUT_DIRECTION_LTR -> LayoutDirection.Ltr
        View.LAYOUT_DIRECTION_RTL -> LayoutDirection.Rtl
        else -> error("Layout direction $i is not supported!")
    }
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val parent = this
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(App.Theme.colors.background)
                .padding(
                    top = App.Theme.dimensions.insets.calculateTopPadding(),
                    end = App.Theme.dimensions.insets.calculateEndPadding(layoutDirection),
                ),
        ) {
            Column(
                modifier = Modifier
                    .weight(1f),
            ) {
                TitledFocused(
                    title = "key:",
                    text = keyState.value,
                    onClick = {
                        focusedState.value = Focused.KEY
                    },
                    onLongClick = {
                        when (focusedState.value) {
                            Focused.KEY -> {
                                textDialogState.value = true
                            }
                            Focused.VALUE -> {
                                focusedState.value = Focused.KEY
                            }
                        }
                    },
                    focused = focusedState.value == Focused.KEY,
                )
                Spacer(modifier = Modifier.height(16.dp))
                TitledFocused(
                    title = "value:",
                    text = valueState.value,
                    onClick = {
                        focusedState.value = Focused.VALUE
                    },
                    onLongClick = {
                        when (focusedState.value) {
                            Focused.KEY -> {
                                focusedState.value = Focused.VALUE
                            }
                            Focused.VALUE -> {
                                textDialogState.value = true
                            }
                        }
                    },
                    focused = focusedState.value == Focused.VALUE,
                )
                RoundedButton(
                    margin = PaddingValues(16.dp),
                    padding = PaddingValues(8.dp),
                    backgroundColor = App.Theme.colors.primary,
                    corners = 16.dp,
                    text = "ok",
                    textColor = Colors.white,
                    onClick = {
                        if (keyState.value.isEmpty()) {
                            // todo
                        } else if (valueState.value.isEmpty()) {
                            // todo
                        } else if (keys.contains(keyState.value)) {
                            // todo
                        } else {
                            onAdd(keyState.value, valueState.value)
                        }
                    },
                )
            }
            Keyboard(
                modifier = Modifier
                    .align(Alignment.Bottom)
                    .width(parent.maxHeight),
                enabled = true,
                rows = TODO(),
                onClick = {
                    when (focusedState.value) {
                        Focused.KEY -> keyState.value += it
                        Focused.VALUE -> valueState.value += it
                    }
                },
                onClickFun = {
                    when (it) {
                        Keyboard.Fun.SPACE_BAR -> TODO()
                        Keyboard.Fun.BACKSPACE -> {
                            when (focusedState.value) {
                                Focused.KEY -> {
                                    if (keyState.value.isNotEmpty()) {
                                        keyState.value = keyState.value.take(keyState.value.lastIndex)
                                    }
                                }
                                Focused.VALUE -> {
                                    if (valueState.value.isNotEmpty()) {
                                        valueState.value = valueState.value.take(valueState.value.lastIndex)
                                    }
                                }
                            }
                        }
                    }
                },
                onLongClickFun = {
                    TODO()
                },
            )
        }
    }
}

@Deprecated(message = "AddItemScreenPortrait")
@Composable
private fun AddItemScreenPortraitOld(
    keys: Set<String>,
    keyState: MutableState<String>,
    valueState: MutableState<String>,
    focusedState: MutableState<Focused>,
    textDialogState: MutableState<Boolean>,
    onAdd: (String, String) -> Unit,
) {
    Box(
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {},
            ) // todo clickable?
            .fillMaxSize()
            .background(App.Theme.colors.background)
            .padding(
                top = App.Theme.dimensions.insets.calculateTopPadding(),
                bottom = App.Theme.dimensions.insets.calculateBottomPadding(),
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            TitledFocused(
                title = "key:",
                text = keyState.value,
                onClick = {
                    focusedState.value = Focused.KEY
                },
                onLongClick = {
                    when (focusedState.value) {
                        Focused.KEY -> {
                            textDialogState.value = true
                        }
                        Focused.VALUE -> {
                            focusedState.value = Focused.KEY
                        }
                    }
                },
                focused = focusedState.value == Focused.KEY,
            )
            Spacer(modifier = Modifier.height(16.dp))
            TitledFocused(
                title = "value:",
                text = valueState.value,
                onClick = {
                    focusedState.value = Focused.VALUE
                },
                onLongClick = {
                    when (focusedState.value) {
                        Focused.KEY -> {
                            focusedState.value = Focused.VALUE
                        }
                        Focused.VALUE -> {
                            textDialogState.value = true
                        }
                    }
                },
                focused = focusedState.value == Focused.VALUE,
            )
            RoundedButton(
                margin = PaddingValues(16.dp),
                padding = PaddingValues(16.dp),
                backgroundColor = App.Theme.colors.primary,
                corners = 32.dp,
                text = "OK",
                textColor = Colors.white,
                onClick = {
                    if (keyState.value.isEmpty()) {
                        // todo
                    } else if (valueState.value.isEmpty()) {
                        // todo
                    } else if (keys.contains(keyState.value)) {
                        // todo
                    } else {
                        onAdd(keyState.value, valueState.value)
                    }
                },
            )
        }
        Keyboard(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            enabled = true,
            rows = TODO(),
            onClick = {
                when (focusedState.value) {
                    Focused.KEY -> keyState.value += it
                    Focused.VALUE -> valueState.value += it
                }
            },
            onClickFun = {
                when (it) {
                    Keyboard.Fun.SPACE_BAR -> TODO()
                    Keyboard.Fun.BACKSPACE -> {
                        when (focusedState.value) {
                            Focused.KEY -> {
                                if (keyState.value.isNotEmpty()) {
                                    keyState.value = keyState.value.take(keyState.value.lastIndex)
                                }
                            }
                            Focused.VALUE -> {
                                if (valueState.value.isNotEmpty()) {
                                    valueState.value = valueState.value.take(valueState.value.lastIndex)
                                }
                            }
                        }
                    }
                }
            },
            onLongClickFun = {
                TODO()
            },
        )
    }
}
