package org.kepocnhh.xfiles.module.unlocked.items

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.kepocnhh.xfiles.App
import org.kepocnhh.xfiles.R
import org.kepocnhh.xfiles.util.compose.AnimatedFadeVisibility
import org.kepocnhh.xfiles.util.compose.ExpandVertically
import org.kepocnhh.xfiles.util.compose.Keyboard
import org.kepocnhh.xfiles.util.compose.KeyboardRows
import org.kepocnhh.xfiles.util.compose.TextFocused
import org.kepocnhh.xfiles.util.compose.horizontalPaddings
import org.kepocnhh.xfiles.util.compose.px
import org.kepocnhh.xfiles.util.compose.toPaddings
import sp.ax.jc.clicks.clicks
import sp.ax.jc.clicks.onClick

internal enum class Focused {
    TITLE, SECRET,
}

@Composable
private fun HintTextFocused(
    values: Map<Focused, String>,
    focusedState: MutableState<Focused?>,
    focused: Focused,
) {
    // todo paste
    val text = when (focused) {
        Focused.TITLE -> values[focused].orEmpty()
        Focused.SECRET -> "*".repeat(values[focused].orEmpty().length)
    }
    val hint = when (focused) {
        Focused.TITLE -> App.Theme.strings.addItem.hintTitle
        Focused.SECRET -> App.Theme.strings.addItem.hintSecret
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
        color = App.Theme.colors.textHint,
        fontSize = 14.sp, // todo
    )
    TextFocused(
        margin = PaddingValues(
            horizontal = App.Theme.sizes.small,
        ),
        padding = PaddingValues(
            horizontal = App.Theme.sizes.large,
        ),
        enabled = focusedState.value != focused,
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
private fun KeyboardSwitch(
    modifier: Modifier,
    rowsState: MutableState<List<CharArray>>,
) {
    Row(
        modifier = modifier,
    ) {
        BasicText(
            modifier = Modifier
                .fillMaxHeight()
                .clickable {
                    rowsState.value = Keyboard.letters
                }
                .wrapContentSize()
                .padding(horizontal = App.Theme.sizes.small),
            text = "abc",
            style = App.Theme.textStyle,
        )
        BasicText(
            modifier = Modifier
                .fillMaxHeight()
                .clickable {
                    rowsState.value = Keyboard.special
                }
                .wrapContentSize()
                .padding(horizontal = App.Theme.sizes.small),
            text = "!@#",
            style = App.Theme.textStyle,
        )
    }
}

private fun MutableMap<Focused, String>.putChar(
    maxLength: Int,
    focused: Focused,
    char: Char,
) {
    if (this[focused].orEmpty().length < maxLength) {
        this[focused] = this[focused].orEmpty() + char
    }
}

@Composable
private fun Keyboard(
    margin: PaddingValues,
    focused: Focused,
    values: MutableMap<Focused, String>,
    onAction: () -> Unit,
) {
    val rowsState = remember { mutableStateOf(Keyboard.letters) }
    LaunchedEffect(focused) {
        if (focused != Focused.SECRET) {
            rowsState.value = Keyboard.letters
        }
    }
    Column(
        modifier = Modifier
            .padding(margin)
            .fillMaxWidth(),
    ) {
        Box(
            modifier = Modifier
                .height(App.Theme.sizes.xxl)
                .fillMaxWidth(),
        ) {
            AnimatedFadeVisibility(
                visible = focused == Focused.SECRET,
                duration = App.Theme.durations.animation,
            ) {
                KeyboardSwitch(
                    modifier = Modifier
                        .fillMaxHeight()
                        .align(Alignment.CenterStart),
                    rowsState = rowsState,
                )
            }
            val enabled = !values[focused].isNullOrEmpty()
            val color = if (enabled) App.Theme.colors.primary else App.Theme.colors.secondary
            val textStyle = TextStyle(
                color = color,
                textAlign = TextAlign.Center,
                fontSize = 14.sp, // todo
            ) // todo
            val text = when (focused) {
                Focused.TITLE -> App.Theme.strings.addItem.next
                Focused.SECRET -> App.Theme.strings.addItem.done
            }
            BasicText(
                modifier = Modifier
                    .fillMaxHeight()
                    .align(Alignment.CenterEnd)
                    .onClick(enabled = enabled, block = onAction)
                    .padding(horizontal = App.Theme.sizes.xl)
                    .wrapContentHeight(),
                text = text,
                style = textStyle,
            )
        }
//        val maxLength = 4 // todo
        val maxLength = 24
        val enabled = values[focused].orEmpty().length < maxLength
        KeyboardRows(
            modifier = Modifier
                .fillMaxWidth(),
            enabled = enabled,
            rows = rowsState.value,
            onClick = { char ->
                values.putChar(
                    maxLength = maxLength,
                    focused = focused,
                    char = char,
                )
            },
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(App.Theme.sizes.xxl),
        ) {
            val textStyle = TextStyle(
                color = App.Theme.colors.text,
                textAlign = TextAlign.Center,
                fontSize = 14.sp, // todo
            ) // todo
            Spacer(modifier = Modifier.width(64.dp))
            BasicText(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .clickable(enabled = enabled) {
                        values.putChar(
                            maxLength = maxLength,
                            focused = focused,
                            char = ' ',
                        )
                    }
                    .wrapContentHeight(),
                text = App.Theme.strings.keyboard.space,
                style = textStyle,
            )
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(App.Theme.sizes.xxxl)
                    .clicks(
                        onClick = {
                            val oldValue = values[focused].orEmpty()
                            if (oldValue.isNotEmpty()) {
                                values[focused] = oldValue.take(oldValue.lastIndex)
                            }
                        },
                        onLongClick = {
                            val oldValue = values[focused].orEmpty()
                            if (oldValue.isNotEmpty()) {
                                values[focused] = ""
                            }
                        },
                    ),
            ) {
                Image(
                    modifier = Modifier
                        .align(Alignment.Center),
                    painter = painterResource(id = R.drawable.backspace),
                    contentDescription = "item:value:backspace",
                    colorFilter = ColorFilter.tint(App.Theme.colors.foreground),
                )
            }
        }
    }
}

internal data class SecretFieldState(
    val expand: Boolean,
    val size: IntSize?,
    val x: Float,
)

@Composable
internal fun AddItemScreen(
    focusedState: MutableState<Focused?>,
    valuesState: MutableMap<Focused, String>,
    secretFieldState: SecretFieldState,
    onSecretFieldSize: (IntSize) -> Unit,
    onShowSecretField: suspend () -> Unit,
    onExpandSecretField: () -> Unit,
    onAdd: (String, String) -> Unit,
) {
    val insets = LocalView.current.rootWindowInsets.toPaddings()
    Column(
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {},
            ) // todo clickable?
            .fillMaxSize()
            .background(App.Theme.colors.background)
            .horizontalPaddings(insets),
        verticalArrangement = Arrangement.Bottom,
    ) {
        BasicText(
            modifier = Modifier.padding(horizontal = App.Theme.sizes.small),
            text = App.Theme.strings.addItem.promptTitle,
            style = App.Theme.textStyle,
        )
        Spacer(modifier = Modifier.height(App.Theme.sizes.small))
        HintTextFocused(
            values = valuesState,
            focusedState = focusedState,
            focused = Focused.TITLE,
        )
        LaunchedEffect(secretFieldState.expand, secretFieldState.size) {
            if (secretFieldState.expand && secretFieldState.size != null) {
                onShowSecretField()
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize(
                    animationSpec = tween(App.Theme.durations.animation.inWholeMilliseconds.toInt()),
                    finishedListener = { _, size ->
                        onSecretFieldSize(size)
                    },
                )
                .offset(x = secretFieldState.x.toInt().px())
                .heightIn(max = if (secretFieldState.expand) Dp.Unspecified else 0.dp),
        ) {
            Spacer(modifier = Modifier.height(App.Theme.sizes.small))
            BasicText(
                modifier = Modifier.padding(horizontal = App.Theme.sizes.small),
                text = App.Theme.strings.addItem.promptSecret,
                style = App.Theme.textStyle,
            )
            Spacer(modifier = Modifier.height(App.Theme.sizes.small))
            HintTextFocused(
                values = valuesState,
                focusedState = focusedState,
                focused = Focused.SECRET,
            )
        }
        Spacer(
            modifier = Modifier
                .animateContentSize(tween(App.Theme.durations.animation.inWholeMilliseconds.toInt()))
                .height(if (focusedState.value != null) App.Theme.sizes.small else App.Theme.sizes.small + insets.calculateBottomPadding()),
        )
        ExpandVertically(
            visible = focusedState.value != null,
            duration = App.Theme.durations.animation,
        ) {
            val focused = focusedState.value!! // todo
            Keyboard(
                margin = PaddingValues(bottom = insets.calculateBottomPadding()),
                focused = focused,
                values = valuesState,
                onAction = {
                    when (focused) {
                        Focused.TITLE -> {
                            focusedState.value = Focused.SECRET
                            onExpandSecretField()
                        }
                        Focused.SECRET -> {
                            val title = valuesState[Focused.TITLE]
                            val secret = valuesState[Focused.SECRET]
                            if (title.isNullOrBlank() || secret.isNullOrEmpty()) {
                                // todo
                            } else {
                                onAdd(title, secret)
                            }
                        }
                    }
                },
            )
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
    val secretFieldExpandState = remember { mutableStateOf(false) }
    val secretFieldSizeState = remember { mutableStateOf<IntSize?>(null) }
    val width = LocalView.current.width
    val secretFieldXState = remember { Animatable(width.toFloat()) }
    AddItemScreen(
        focusedState = focusedState,
        valuesState = valuesState,
        secretFieldState = SecretFieldState(
            expand = secretFieldExpandState.value,
            size = secretFieldSizeState.value,
            x = secretFieldXState.value,
        ),
        onSecretFieldSize = {
            secretFieldSizeState.value = it
        },
        onShowSecretField = {
            secretFieldXState.animateTo(0f)
        },
        onExpandSecretField = {
            secretFieldExpandState.value = true
        },
        onAdd = onAdd,
    )
}
