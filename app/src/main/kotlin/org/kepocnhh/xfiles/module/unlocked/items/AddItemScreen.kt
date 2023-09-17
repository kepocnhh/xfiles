package org.kepocnhh.xfiles.module.unlocked.items

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.kepocnhh.xfiles.App
import org.kepocnhh.xfiles.util.compose.Keyboard
import org.kepocnhh.xfiles.util.compose.RoundedButton
import org.kepocnhh.xfiles.util.compose.TextFocused
import org.kepocnhh.xfiles.util.compose.toPaddings

private enum class Focused {
    TITLE, SECRET,
}

@Composable
private fun HintTextFocused(
    values: Map<Focused, String>,
    focusedState: MutableState<Focused?>,
    focused: Focused,
) {
    val text = when (focused) {
        Focused.TITLE -> values[focused].orEmpty()
        Focused.SECRET -> "*".repeat(values[focused].orEmpty().length)
    }
    val hint = when (focused) {
        Focused.TITLE -> "Title" // todo
        Focused.SECRET -> "Secret" // todo
    }
    val textStyle = when (focused) {
        Focused.TITLE -> TextStyle(
            color = App.Theme.colors.text,
            fontFamily = FontFamily.Default,
            fontSize = 14.sp, // todo
        )
        Focused.SECRET -> TextStyle(
            color = App.Theme.colors.text,
            fontFamily = FontFamily.Monospace,
            fontSize = 14.sp, // todo
        )
    }
    val hintStyle = TextStyle(
        color = App.Theme.colors.secondary,
        fontSize = 14.sp, // todo
    )
    TextFocused(
        margin = PaddingValues(
            start = App.Theme.sizes.small,
            end = App.Theme.sizes.small,
        ),
        padding = PaddingValues(
            start = App.Theme.sizes.large,
            end = App.Theme.sizes.large,
        ),
        height = App.Theme.sizes.xxxl,
        color = App.Theme.colors.basement,
        corners = App.Theme.sizes.large,
        text = if (text.isEmpty() && focusedState.value != focused) hint else text,
        textStyle = if (text.isEmpty() && focusedState.value != focused) hintStyle else textStyle,
        onClick = {
            focusedState.value = focused
        },
        onLongClick = {
                      // todo
        },
        focused = focusedState.value == focused,
    )
}

@Composable
private fun AddItemScreenPortrait(
    focusedState: MutableState<Focused?>,
    values: MutableMap<Focused, String>,
    onAdd: (String, String) -> Unit,
) {
    val insets = LocalView.current.rootWindowInsets.toPaddings()
    Box(
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {},
            ) // todo clickable?
            .fillMaxSize()
            .background(App.Theme.colors.background)
            .padding(insets),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = App.Theme.sizes.small),
        ) {
            HintTextFocused(
                values = values,
                focusedState = focusedState,
                focused = Focused.TITLE,
            )
            Spacer(Modifier.height(App.Theme.sizes.small))
            HintTextFocused(
                values = values,
                focusedState = focusedState,
                focused = Focused.SECRET,
            )
            Spacer(Modifier.height(App.Theme.sizes.small))
            RoundedButton(
                margin = PaddingValues(
                    start = App.Theme.sizes.small,
                    end = App.Theme.sizes.small,
                ),
                text = "ok", // todo
                onClick = {
                    val title = values[Focused.TITLE]
                    val secret = values[Focused.SECRET]
                    if (title.isNullOrEmpty() || secret.isNullOrEmpty()) {
                        // todo
                    } else {
                        onAdd(title, secret)
                    }
                },
            )
            val focused = focusedState.value
            // todo animation
            if (focused != null) {
                val rowsState = remember { mutableStateOf(Keyboard.letters) }
                Spacer(Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .height(App.Theme.sizes.xxl)
                        .fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxHeight()
                            .align(Alignment.CenterStart),
                    ) {
                        BasicText(
                            modifier = Modifier
                                .fillMaxHeight()
                                .clickable {
                                    rowsState.value = Keyboard.letters
                                }
                                .wrapContentSize()
                                .padding(
                                    start = App.Theme.sizes.small,
                                    end = App.Theme.sizes.small,
                                ),
                            text = "abc",
                            style = TextStyle(
                                // todo
                            ),
                        )
                        BasicText(
                            modifier = Modifier
                                .fillMaxHeight()
                                .clickable {
                                    rowsState.value = Keyboard.special
                                }
                                .wrapContentSize()
                                .padding(
                                    start = App.Theme.sizes.small,
                                    end = App.Theme.sizes.small,
                                ),
                            text = "!@#",
                            style = TextStyle(
                                // todo
                            ),
                        )
                    }
                    BasicText(
                        modifier = Modifier
                            .fillMaxHeight()
                            .widthIn(min = 128.dp)
                            .align(Alignment.CenterEnd)
                            .clickable {
                                // todo
                            }
                            .wrapContentSize()
                            .padding(
                                start = App.Theme.sizes.small,
                                end = App.Theme.sizes.small,
                            ),
                        text = "foo",
                        style = TextStyle(
                            // todo
                        ),
                    )
                }
                Keyboard(
                    modifier = Modifier
                        .fillMaxWidth(),
                    enabled = true, // todo
                    rows = rowsState.value,
                    onClick = {
                        values[focused] = values[focused].orEmpty() + it
                    },
                    onClickFun = {
                        when (it) {
                            Keyboard.Fun.SPACE_BAR -> {
                                values[focused] = values[focused].orEmpty() + ' '
                            }
                            Keyboard.Fun.BACKSPACE -> {
                                val oldValue = values[focused].orEmpty()
                                if (oldValue.isNotEmpty()) {
                                    values[focused] = oldValue.take(oldValue.lastIndex)
                                }
                            }
                        }
                    },
                    onLongClickFun = {
                        when (it) {
                            Keyboard.Fun.SPACE_BAR -> TODO("Keyboard:onLongClickFun:SPACE_BAR")
                            Keyboard.Fun.BACKSPACE -> {
                                val oldValue = values[focused].orEmpty()
                                if (oldValue.isNotEmpty()) {
                                    values[focused] = ""
                                }
                            }
                        }
                    },
                )
            }
        }
    }
}

@Composable
internal fun AddItemScreen(
    onAdd: (String, String) -> Unit,
    onCancel: () -> Unit,
) {
    BackHandler {
        onCancel()
    }
    val focusedState = remember { mutableStateOf<Focused?>(null) }
    val valuesState = remember { mutableStateMapOf<Focused, String>() }
    when (val orientation = LocalConfiguration.current.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            TODO("AddItemScreen:ORIENTATION_LANDSCAPE")
        }
        Configuration.ORIENTATION_PORTRAIT -> {
            AddItemScreenPortrait(
                focusedState = focusedState,
                values = valuesState,
                onAdd = onAdd,
            )
        }
        else -> error("Orientation $orientation is not supported!")
    }
}
