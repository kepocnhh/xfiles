package org.kepocnhh.xfiles.module.enter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import org.kepocnhh.xfiles.App
import org.kepocnhh.xfiles.module.app.ColorsType
import org.kepocnhh.xfiles.module.app.Language
import org.kepocnhh.xfiles.module.app.ThemeState
import org.kepocnhh.xfiles.provider.ThemeStateProvider

@Composable
private fun EnterScreenPreview(
    themeState: ThemeState,
    state: EnterViewModel.State,
    error: EnterScreen.Error?,
    pin: String,
    deleteDialog: Boolean,
    settings: Boolean,
) {
    val errorState = rememberSaveable { mutableStateOf(error) }
    val pinState = rememberSaveable { mutableStateOf(pin) }
    val deleteDialogState = remember { mutableStateOf(deleteDialog) }
    val settingsState = remember { mutableStateOf(settings) }
    App.Theme.Composition(themeState) {
        EnterScreen(
            state = state,
            errorState = errorState,
            pinState = pinState,
            deleteDialogState = deleteDialogState,
            settingsState = settingsState,
            onBiometric = {
                // noop
            },
        )
    }
}

@Preview(name = "ThemeState")
@Composable
private fun AddItemScreenThemeStatePreview(
    @PreviewParameter(ThemeStateProvider::class) themeState: ThemeState,
) {
    val state = EnterViewModel.State(
        loading = false,
        exists = false,
        hasBiometric = false,
    )
    EnterScreenPreview(
        themeState = themeState,
        state = state,
        error = null,
        pin = "",
        deleteDialog = false,
        settings = false,
    )
}

private class StateProvider : PreviewParameterProvider<EnterViewModel.State?> {
    override val values = sequenceOf(
        EnterViewModel.State(
            loading = false,
            exists = false,
            hasBiometric = false,
        ),
        EnterViewModel.State(
            loading = true,
            exists = false,
            hasBiometric = false,
        ),
        EnterViewModel.State(
            loading = false,
            exists = true,
            hasBiometric = false,
        ),
        EnterViewModel.State(
            loading = false,
            exists = true,
            hasBiometric = true,
        ),
    )
}

@Preview(name = "State")
@Composable
private fun AddItemScreenStatePreview(
    @PreviewParameter(StateProvider::class) state: EnterViewModel.State,
) {
    val themeState = ThemeState(
        colorsType = ColorsType.DARK,
        language = Language.ENGLISH,
    )
    EnterScreenPreview(
        themeState = themeState,
        state = state,
        error = null,
        pin = "",
        deleteDialog = false,
        settings = false,
    )
}

// @Preview(name = "Error.UNLOCK") // todo
// @Preview(name = "Delete") // todo

@Preview(name = "Pin")
@Composable
private fun AddItemScreenPinPreview() {
    val themeState = ThemeState(
        colorsType = ColorsType.DARK,
        language = Language.ENGLISH,
    )
    val state = EnterViewModel.State(
        loading = false,
        exists = false,
        hasBiometric = false,
    )
    EnterScreenPreview(
        themeState = themeState,
        state = state,
        error = null,
        pin = "123",
        deleteDialog = false,
        settings = false,
    )
}
