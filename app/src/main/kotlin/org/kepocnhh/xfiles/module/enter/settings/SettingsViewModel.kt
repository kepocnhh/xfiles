package org.kepocnhh.xfiles.module.enter.settings

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.kepocnhh.xfiles.entity.SecurityService
import org.kepocnhh.xfiles.module.app.Injection
import org.kepocnhh.xfiles.util.lifecycle.AbstractViewModel

internal class SettingsViewModel(private val injection: Injection) : AbstractViewModel() {
    private val _cipher = MutableStateFlow<SecurityService?>(null)
    val cipher = _cipher.asStateFlow()

    fun requestCipher() {
        injection.launch {
            _cipher.value = withContext(injection.contexts.default) {
                injection.local.services!!.cipher
            }
        }
    }
}
