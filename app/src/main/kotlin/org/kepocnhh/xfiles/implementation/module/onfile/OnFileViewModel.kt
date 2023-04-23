package org.kepocnhh.xfiles.implementation.module.onfile

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.withContext
import org.kepocnhh.xfiles.foundation.provider.Injection
import org.kepocnhh.xfiles.implementation.util.androidx.lifecycle.AbstractViewModel

internal class OnFileViewModel(private val injection: Injection) : AbstractViewModel() {
    sealed interface Broadcast {
        object Delete : Broadcast
    }

    private val _broadcast = MutableSharedFlow<Broadcast?>()
    val broadcast = _broadcast.asSharedFlow()

    fun deleteFile() {
        injection.launch {
            withContext(injection.contexts.io) {
                injection.file.delete()
            }
            _broadcast.emit(Broadcast.Delete)
        }
    }
}
