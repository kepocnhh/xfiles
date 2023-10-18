package org.kepocnhh.xfiles.module.unlocked.items

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.kepocnhh.xfiles.App
import org.kepocnhh.xfiles.R
import org.kepocnhh.xfiles.module.app.Colors
import org.kepocnhh.xfiles.util.compose.AnimatedFadeVisibility
import org.kepocnhh.xfiles.util.compose.ColorIndication
import org.kepocnhh.xfiles.util.compose.FloatingActionButton
import org.kepocnhh.xfiles.util.compose.Keyboard
import org.kepocnhh.xfiles.util.compose.KeyboardRows
import org.kepocnhh.xfiles.util.compose.SlideInVFadeExpand
import org.kepocnhh.xfiles.util.compose.TextFocused
import org.kepocnhh.xfiles.util.compose.horizontalPaddings
import org.kepocnhh.xfiles.util.compose.toPaddings
import sp.ax.jc.clicks.clicks
import sp.ax.jc.clicks.onClick

private enum class Focused {
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
                .padding(
                    start = App.Theme.sizes.small,
                    end = App.Theme.sizes.small,
                ),
            text = "abc",
            style = TextStyle(
                color = App.Theme.colors.text,
                fontSize = 14.sp, // todo
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
                color = App.Theme.colors.text,
                fontSize = 14.sp, // todo
            ),
        )
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
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(App.Theme.sizes.xxxl)
                    .align(Alignment.CenterEnd)
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
        KeyboardRows(
            modifier = Modifier
                .fillMaxWidth(),
            enabled = true, // todo
            rows = rowsState.value,
            onClick = {
                values[focused] = values[focused].orEmpty() + it
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
                    .clickable(enabled = true) {
                        values[focused] = values[focused].orEmpty() + ' '
                    }
                    .wrapContentHeight(),
                text = "space",
                style = textStyle,
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(App.Theme.sizes.small),
        ) {
            Row(
                modifier = Modifier
                    .align(Alignment.CenterStart),
            ) {

            }
            Row(
                modifier = Modifier
                    .align(Alignment.CenterEnd),
            ) {
                val icon = when (focused) {
                    Focused.TITLE -> R.drawable.arrow_right
                    Focused.SECRET -> R.drawable.check
                }
                val enabled = !values[focused].isNullOrEmpty()
                FloatingActionButton(
                    color = if (enabled) App.Theme.colors.primary else App.Theme.colors.secondary,
                    enabled = enabled,
                    indication = ColorIndication.create(Colors.white),
                    icon = icon,
                    iconColor = if (enabled) Color.White else App.Theme.colors.background,
                    contentDescription = "add:item:${focused.name}",
                    onClick = onAction,
                )
            }
        }
    }
}

@Composable
private fun AddItemScreenPortrait(
    focusedState: MutableState<Focused?>,
    values: MutableMap<Focused, String>,
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
            modifier = Modifier
                .padding(horizontal = App.Theme.sizes.small),
            text = "Come up with a name for your secret:", // todo
            style = TextStyle(
                color = App.Theme.colors.text,
                fontSize = 14.sp,
            ),
        )
        Spacer(modifier = Modifier.height(App.Theme.sizes.small))
        HintTextFocused(
            values = values,
            focusedState = focusedState,
            focused = Focused.TITLE,
        )
        Spacer(modifier = Modifier.height(App.Theme.sizes.small))
        BasicText(
            modifier = Modifier
                .padding(horizontal = App.Theme.sizes.small),
            text = "Enter your secret here:", // todo
            style = TextStyle(
                color = App.Theme.colors.text,
                fontSize = 14.sp,
            ),
        )
        Spacer(modifier = Modifier.height(App.Theme.sizes.small))
        HintTextFocused(
            values = values,
            focusedState = focusedState,
            focused = Focused.SECRET,
        )
        Spacer(
            modifier = Modifier
                .animateContentSize(tween(App.Theme.durations.animation.inWholeMilliseconds.toInt()))
                .height(if (focusedState.value != null) App.Theme.sizes.small else App.Theme.sizes.small + insets.calculateBottomPadding()),
        )
        SlideInVFadeExpand(
            visible = focusedState.value != null,
            duration = App.Theme.durations.animation,
        ) {
            val focused = focusedState.value!! // todo
            Keyboard(
                margin = PaddingValues(bottom = insets.calculateBottomPadding()),
                focused = focused,
                values = values,
                onAction = {
                    when (focused) {
                        Focused.TITLE -> {
                            focusedState.value = Focused.SECRET
                        }
                        Focused.SECRET -> {
                            val title = values[Focused.TITLE]
                            val secret = values[Focused.SECRET]
                            if (title.isNullOrBlank() || secret.isNullOrEmpty()) {
                                // todo
                            } else {
                                onAdd(title, secret)
                            }
                        }
                    }
                }
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
