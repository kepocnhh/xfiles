package org.kepocnhh.xfiles.implementation.module.onfile

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.kepocnhh.xfiles.foundation.provider.Injection
import org.kepocnhh.xfiles.implementation.util.androidx.lifecycle.AbstractViewModel

internal class OnFileViewModel(private val injection: Injection) : AbstractViewModel() {
    sealed interface Broadcast {
        object Delete : Broadcast
    }

    private val _broadcast = MutableSharedFlow<Broadcast?>()
    val broadcast = _broadcast.asSharedFlow()

    private val _state = MutableStateFlow<Map<String, String>?>(null)
    val state = _state.asStateFlow()

    private val logger = injection.loggers.newLogger("[OnFile|VM]")

    fun deleteFile() {
        injection.launch {
            withContext(injection.contexts.io) {
                injection.file.delete()
            }
            _broadcast.emit(Broadcast.Delete)
        }
    }

    fun requestItems() {
        injection.launch {
            _state.value = withContext(injection.contexts.io) {
                val text = injection.file.readText()
                logger.debug("text: $text")
                JSONObject(text).let { json ->
                    json.keys().asSequence().map { key ->
                        key to json.getString(key)
                    }
                }.toMap()
            }
        }
    }
}
