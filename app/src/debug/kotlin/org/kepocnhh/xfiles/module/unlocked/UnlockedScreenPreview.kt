package org.kepocnhh.xfiles.module.unlocked

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import org.kepocnhh.xfiles.App
import org.kepocnhh.xfiles.module.app.ColorsType
import org.kepocnhh.xfiles.module.app.Language
import org.kepocnhh.xfiles.module.app.ThemeState
import org.kepocnhh.xfiles.provider.ThemeStateProvider

@Composable
private fun UnlockedScreenPreview(
    themeState: ThemeState,
    loading: Boolean,
    encrypteds: Map<String, String>,
) {
    App.Theme.Composition(themeState) {
        UnlockedScreen(
            loading = loading,
            encrypteds = encrypteds,
            onShow = {
                // noop
            },
            onCopy = {
                // noop
            },
            onAdd = {
                // noop
            },
            onDelete = {
                // noop
            },
            onLock = {
                // noop
            },
        )
    }
}

@Suppress("FunctionMaxLength")
@Preview(name = "ThemeState")
@Composable
private fun UnlockedScreenThemeStatePreview(
    @PreviewParameter(ThemeStateProvider::class) themeState: ThemeState,
) {
    UnlockedScreenPreview(
        themeState = themeState,
        loading = false,
        encrypteds = emptyMap(),
    )
}

@Preview(name = "Loading")
@Composable
private fun UnlockedScreenLoadingPreview() {
    val themeState = ThemeState(
        colorsType = ColorsType.DARK,
        language = Language.ENGLISH,
    )
    UnlockedScreenPreview(
        themeState = themeState,
        loading = true,
        encrypteds = emptyMap(),
    )
}

@Suppress("FunctionMaxLength")
@Preview(name = "Encrypteds")
@Composable
private fun UnlockedScreenEncryptedsPreview() {
    val themeState = ThemeState(
        colorsType = ColorsType.DARK,
        language = Language.ENGLISH,
    )
    UnlockedScreenPreview(
        themeState = themeState,
        loading = false,
        encrypteds = mapOf(
            "id:foo" to "foo",
            "id:numbers" to "numbers",
            "id:letters" to "letters",
        ),
    )
}
