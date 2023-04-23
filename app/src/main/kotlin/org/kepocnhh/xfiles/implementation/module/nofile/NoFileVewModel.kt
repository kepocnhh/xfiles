package org.kepocnhh.xfiles.implementation.module.nofile

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.kepocnhh.xfiles.foundation.provider.Injection
import org.kepocnhh.xfiles.implementation.util.androidx.lifecycle.AbstractViewModel

internal class NoFileVewModel(private val injection: Injection) : AbstractViewModel() {
    sealed interface Broadcast {
        object Create : Broadcast
    }

    private val _broadcast = MutableSharedFlow<Broadcast?>()
    val broadcast = _broadcast.asSharedFlow()

    fun newFile() {
        injection.launch {
            withContext(injection.contexts.io) {
                injection.file.writeText("{}")
                injection.file.exists()
            }
            _broadcast.emit(Broadcast.Create)
        }
    }
}
