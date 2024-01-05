package org.kepocnhh.xfiles.provider

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import org.kepocnhh.xfiles.module.app.ColorsType
import org.kepocnhh.xfiles.module.app.Language
import org.kepocnhh.xfiles.module.app.ThemeState

internal class ThemeStateProvider : PreviewParameterProvider<ThemeState> {
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
