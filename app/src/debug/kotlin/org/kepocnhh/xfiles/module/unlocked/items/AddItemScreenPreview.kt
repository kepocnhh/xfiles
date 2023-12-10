package org.kepocnhh.xfiles.module.unlocked.items

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateMap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import org.kepocnhh.xfiles.App
import org.kepocnhh.xfiles.module.app.ColorsType
import org.kepocnhh.xfiles.module.app.Language
import org.kepocnhh.xfiles.module.app.ThemeState

@Composable
private fun AddItemScreenPreview(
    themeState: ThemeState,
    focusedState: MutableState<Focused?>,
    valuesState: MutableMap<Focused, String>,
    secretFieldState: SecretFieldState,
) {
    App.Theme.Composition(themeState) {
        AddItemScreen(
            focusedState = focusedState,
            valuesState = valuesState,
            secretFieldState = secretFieldState,
            onSecretFieldSize = {
                // noop
            },
            onShowSecretField = {
                // noop
            },
            onExpandSecretField = {
                // noop
            },
            onAdd = { _, _ ->
                // noop
            },
        )
    }
}

private class ThemeStateProvider : PreviewParameterProvider<ThemeState> {
    override val values = sequenceOf(
        ThemeState(
            colorsType = ColorsType.DARK,
            language = Language.ENGLISH,
        ),
        ThemeState(
            colorsType = ColorsType.LIGHT,
            language = Language.RUSSIAN,
        ),
    )
}

private class FocusedProvider : PreviewParameterProvider<Focused?> {
    override val values = sequenceOf<Focused?>(null) + Focused.entries.asSequence()
}

@Preview(name = "ThemeState")
@Composable
private fun AddItemScreenThemeStatePreview(
    @PreviewParameter(ThemeStateProvider::class) themeState: ThemeState,
) {
    val focusedState = remember { mutableStateOf<Focused?>(null) }
    val valuesState = remember { mutableStateMapOf<Focused, String>() }
    val secretFieldState = SecretFieldState(
        expand = false,
        size = null,
        x = 0f,
    )
    AddItemScreenPreview(
        themeState = themeState,
        focusedState = focusedState,
        valuesState = valuesState,
        secretFieldState = secretFieldState,
    )
}

@Preview(name = "Focused")
@Composable
private fun AddItemScreenFocusedPreview(
    @PreviewParameter(FocusedProvider::class) focused: Focused?,
) {
    val themeState = ThemeState(
        colorsType = ColorsType.DARK,
        language = Language.ENGLISH,
    )
    val focusedState = remember { mutableStateOf(focused) }
    val valuesState = remember { mutableStateMapOf<Focused, String>() }
    val secretFieldState = SecretFieldState(
        expand = true,
        size = null,
        x = 0f,
    )
    AddItemScreenPreview(
        themeState = themeState,
        focusedState = focusedState,
        valuesState = valuesState,
        secretFieldState = secretFieldState,
    )
}

private class ValuesProvider : PreviewParameterProvider<Iterable<Pair<Focused, String>>> {
    override val values = sequenceOf(
        setOf(
            Focused.TITLE to "foo",
        ),
        setOf(
            Focused.SECRET to "bar",
        ),
        setOf(
            Focused.TITLE to "foo",
            Focused.SECRET to "bar",
        ),
    )
}

@Preview(name = "Values")
@Composable
private fun AddItemScreenValuesPreview(
    @PreviewParameter(ValuesProvider::class) values: Iterable<Pair<Focused, String>>,
) {
    val themeState = ThemeState(
        colorsType = ColorsType.DARK,
        language = Language.ENGLISH,
    )
    val focusedState = remember { mutableStateOf<Focused?>(null) }
    val valuesState = remember { values.toMutableStateMap() }
    val secretFieldState = SecretFieldState(
        expand = true,
        size = null,
        x = 0f,
    )
    AddItemScreenPreview(
        themeState = themeState,
        focusedState = focusedState,
        valuesState = valuesState,
        secretFieldState = secretFieldState,
    )
}
