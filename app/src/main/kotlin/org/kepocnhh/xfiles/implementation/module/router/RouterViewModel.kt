package org.kepocnhh.xfiles.implementation.module.router

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.kepocnhh.xfiles.foundation.provider.Injection
import org.kepocnhh.xfiles.implementation.util.androidx.lifecycle.AbstractViewModel

internal class RouterViewModel(private val injection: Injection) : AbstractViewModel() {
    private val _state = MutableStateFlow<Boolean?>(null)
    val state = _state.asStateFlow()

    fun requestFile() {
        injection.launch {
            val result = withContext(injection.contexts.io) {
                injection.file.exists()
            }
            _state.value = result
        }
    }
}
