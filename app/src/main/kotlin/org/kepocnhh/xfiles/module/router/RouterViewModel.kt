package org.kepocnhh.xfiles.module.router

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.kepocnhh.xfiles.provider.Injection
import org.kepocnhh.xfiles.util.lifecycle.AbstractViewModel

internal class RouterViewModel(private val injection: Injection) : AbstractViewModel() {
    data class State(val exists: Boolean)

    private val _state = MutableStateFlow<State?>(null)
    val state = _state.asStateFlow()

    fun requestState() {
        injection.launch {
            val exists = withContext(injection.contexts.io) {
                injection.file.exists()
            }
            _state.value = State(exists = exists)
        }
    }
}
