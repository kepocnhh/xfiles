package org.kepocnhh.xfiles.module.enter.settings

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.kepocnhh.xfiles.entity.SecurityService
import org.kepocnhh.xfiles.entity.SecuritySettings
import org.kepocnhh.xfiles.module.app.Injection
import org.kepocnhh.xfiles.provider.data.requireServices
import org.kepocnhh.xfiles.util.lifecycle.AbstractViewModel

internal class SettingsViewModel(private val injection: Injection) : AbstractViewModel() {
    private val _cipher = MutableStateFlow<SecurityService?>(null)
    val cipher = _cipher.asStateFlow()

    private val _settings = MutableStateFlow<SecuritySettings?>(null)
    val settings = _settings.asStateFlow()

    private val _databaseExists = MutableStateFlow<Boolean?>(null)
    val databaseExists = _databaseExists.asStateFlow()

    fun requestCipher() {
        injection.launch {
            _cipher.value = withContext(injection.contexts.default) {
                val services = injection.local.requireServices()
                services.cipher
            }
        }
    }

    fun requestSettings() {
        injection.launch {
            _settings.value = withContext(injection.contexts.default) {
                injection.local.securitySettings
            }
        }
    }

    fun setSettings(value: SecuritySettings) {
        injection.launch {
            _settings.value = withContext(injection.contexts.default) {
                value.also {
                    injection.local.securitySettings = it
                }
            }
        }
    }

    fun requestDatabase() {
        injection.launch {
            _databaseExists.value = withContext(injection.contexts.default) {
                injection.encrypted.files.exists(injection.pathNames.dataBase)
            }
        }
    }
}
