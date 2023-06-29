package org.kepocnhh.xfiles.module.enter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

internal class EnterViewModel : ViewModel() {
    sealed interface Broadcast {
        object OnCreate : Broadcast
    }

    private val _broadcast = MutableSharedFlow<Broadcast>()
    val broadcast = _broadcast.asSharedFlow()

    private val _exists = MutableStateFlow<Boolean?>(null)
    val exists = _exists.asStateFlow()

    fun requestFile(parent: File) {
        viewModelScope.launch {
            _exists.value = withContext(Dispatchers.IO) {
                parent.resolve("db.enc").exists()
            }
        }
    }

    fun createNewFile(parent: File, pin: String) {
        viewModelScope.launch {
            _exists.value = null
            withContext(Dispatchers.IO) {
                parent.resolve("db.enc").createNewFile()
                delay(2_000)
            }
            _broadcast.emit(Broadcast.OnCreate)
        }
    }

    fun deleteFile(parent: File) {
        viewModelScope.launch {
            _exists.value = null
            withContext(Dispatchers.IO) {
                parent.resolve("db.enc").delete()
                delay(2_000)
            }
            _exists.value = false
        }
    }
}
