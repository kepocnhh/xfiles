package org.kepocnhh.xfiles.provider.data

import android.content.Context
import org.kepocnhh.xfiles.BuildConfig
import org.kepocnhh.xfiles.entity.Defaults
import org.kepocnhh.xfiles.module.app.ColorsType
import org.kepocnhh.xfiles.module.app.Language
import org.kepocnhh.xfiles.module.app.ThemeState

internal class FinalLocalDataProvider(
    context: Context,
    private val defaults: Defaults,
) : LocalDataProvider {
    private val preferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)

    override var themeState: ThemeState
        get() {
            return ThemeState(
                colorsType = preferences
                    .getString("colorsType", null)
                    ?.let(ColorsType::valueOf)
                    ?: defaults.themeState.colorsType,
                language = preferences
                    .getString("language", null)
                    ?.let(Language::valueOf)
                    ?: defaults.themeState.language,
            )
        }
        set(value) {
            preferences.edit()
                .putString("colorsType", value.colorsType.name)
                .putString("language", value.language.name)
                .commit()
        }
}
