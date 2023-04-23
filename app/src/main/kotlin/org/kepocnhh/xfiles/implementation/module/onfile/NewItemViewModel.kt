package org.kepocnhh.xfiles.implementation.module.onfile

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.kepocnhh.xfiles.foundation.provider.Injection
import org.kepocnhh.xfiles.implementation.util.androidx.lifecycle.AbstractViewModel

internal class NewItemViewModel(private val injection: Injection) : AbstractViewModel() {
    sealed interface Broadcast {
        object Error : Broadcast
        object Success : Broadcast
    }

    private val _broadcast = MutableSharedFlow<Broadcast?>()
    val broadcast = _broadcast.asSharedFlow()

    fun add(key: String, value: String) {
        injection.launch {
            if (key.isEmpty()) {
                _broadcast.emit(Broadcast.Error)
            } else if (value.isEmpty()) {
                _broadcast.emit(Broadcast.Error)
            } else {
                val contains = withContext(injection.contexts.io) {
                    JSONObject(injection.file.readText()).has(key)
                }
                if (contains) {
                    _broadcast.emit(Broadcast.Error)
                } else {
                    withContext(injection.contexts.io) {
                        val text = JSONObject(injection.file.readText()).also {
                            it.put(key, value)
                        }.toString()
                        injection.file.writeText(text)
                    }
                    _broadcast.emit(Broadcast.Success)
                }
            }
        }
    }
}
