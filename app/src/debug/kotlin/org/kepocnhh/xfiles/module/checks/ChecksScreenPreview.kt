package org.kepocnhh.xfiles.module.checks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.kepocnhh.xfiles.App
import org.kepocnhh.xfiles.module.app.ColorsType
import org.kepocnhh.xfiles.module.app.Language
import org.kepocnhh.xfiles.module.app.ThemeState

@Composable
private fun ChecksScreenPreview(
    themeState: ThemeState,
    state: ChecksViewModel.State,
) {
    App.Theme.Composition(themeState) {
        ChecksScreen(
            state = state,
            onExit = {
                // noop
            },
        )
    }
}

@Preview(name = "DARK/ENGLISH")
@Composable
private fun ChecksScreenDarkEnPreview() {
    val themeState = ThemeState(
        colorsType = ColorsType.DARK,
        language = Language.ENGLISH,
    )
    ChecksScreenPreview(
        themeState = themeState,
        state = ChecksViewModel.State.OnChecks(type = ChecksViewModel.ChecksType.SECURITY_SERVICES),
    )
}

@Preview(name = "LIGHT/RUSSIAN")
@Composable
private fun ChecksScreenLightRuPreview() {
    val themeState = ThemeState(
        colorsType = ColorsType.LIGHT,
        language = Language.RUSSIAN,
    )
    ChecksScreenPreview(
        themeState = themeState,
        state = ChecksViewModel.State.OnChecks(type = ChecksViewModel.ChecksType.IDS),
    )
}

@Preview(name = "on error")
@Composable
private fun ChecksScreenOnErrorPreview() {
    val themeState = ThemeState(
        colorsType = ColorsType.DARK,
        language = Language.ENGLISH,
    )
    ChecksScreenPreview(
        themeState = themeState,
        state = ChecksViewModel.State.OnError(
            type = ChecksViewModel.ChecksType.SECURITY_SERVICES,
            error = IllegalStateException("foo"),
        ),
    )
}
