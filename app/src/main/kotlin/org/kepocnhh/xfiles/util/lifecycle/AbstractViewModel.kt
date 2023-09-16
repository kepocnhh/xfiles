package org.kepocnhh.xfiles.util.lifecycle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.kepocnhh.xfiles.module.app.Injection

internal open class AbstractViewModel : ViewModel() {
    protected fun Injection.launch(block: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch(contexts.main, block = block)
    }

    protected fun <T : Any> Flow<T>.stateIn(
        initialValue: T,
        started: SharingStarted = SharingStarted.Lazily,
    ): StateFlow<T> {
        return stateIn(
            scope = viewModelScope,
            started = started,
            initialValue = initialValue,
        )
    }
}
