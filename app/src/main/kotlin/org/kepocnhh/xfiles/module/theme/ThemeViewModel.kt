package org.kepocnhh.xfiles.module.theme

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.kepocnhh.xfiles.module.app.ColorsType
import org.kepocnhh.xfiles.module.app.Injection
import org.kepocnhh.xfiles.module.app.ThemeState
import org.kepocnhh.xfiles.util.lifecycle.AbstractViewModel

internal class ThemeViewModel(private val injection: Injection) : AbstractViewModel() {
    private val _state = MutableStateFlow<ThemeState?>(null)
    val state = _state.asStateFlow()

    fun requestThemeState() {
        injection.launch {
            _state.value = withContext(injection.contexts.default) {
                injection.local.themeState
            }
        }
    }

    fun setColorsType(value: ColorsType) {
        injection.launch {
            _state.value = withContext(injection.contexts.default) {
                injection.local.themeState.copy(colorsType = value).also {
                    injection.local.themeState = it
                }
            }
        }
    }
}
