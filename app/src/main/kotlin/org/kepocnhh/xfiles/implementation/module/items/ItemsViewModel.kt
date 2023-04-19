package org.kepocnhh.xfiles.implementation.module.items

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.kepocnhh.xfiles.foundation.provider.Injection
import org.kepocnhh.xfiles.implementation.util.androidx.lifecycle.AbstractViewModel

internal class ItemsViewModel(private val injection: Injection) : AbstractViewModel() {
    private val _broadcast = MutableSharedFlow<Boolean>()
    val broadcast = _broadcast.asSharedFlow()

    fun deleteFile() {
        injection.launch {
            val exists = withContext(injection.contexts.io) {
                injection.file.delete()
                injection.file.exists()
            }
            _broadcast.emit(exists)
        }
    }
}
