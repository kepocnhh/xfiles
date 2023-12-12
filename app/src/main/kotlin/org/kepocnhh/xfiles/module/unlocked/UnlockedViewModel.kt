package org.kepocnhh.xfiles.module.unlocked

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.kepocnhh.xfiles.module.app.Injection
import org.kepocnhh.xfiles.provider.data.requireServices
import org.kepocnhh.xfiles.provider.readBytes
import org.kepocnhh.xfiles.provider.readText
import org.kepocnhh.xfiles.util.base64
import org.kepocnhh.xfiles.util.lifecycle.AbstractViewModel
import java.util.UUID
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

private fun JSONObject.toMap(): Map<String, String> {
    return keys().asSequence().associateWith { id ->
        getJSONObject(id).getString("title")
    }
}

private fun JSONObject.getSecrets(): JSONObject {
    return getJSONObject("secrets")
}

private fun JSONObject.getSecret(): String {
    return getString("secret")
}

internal class UnlockedViewModel(private val injection: Injection) : AbstractViewModel() {
    sealed interface Broadcast {
        data class OnCopy(val secret: String) : Broadcast
        data class OnShow(val secret: String) : Broadcast
    }

    private val logger = injection.loggers.newLogger("[Unlocked|VM]")

    private val _encrypteds = MutableStateFlow<Map<String, String>?>(null)
    val encrypteds = _encrypteds.asStateFlow()

    private val _operations = MutableStateFlow(0)
    val loading = _operations
        .map {
            encrypteds.value == null || it > 0
        }
        .stateIn(true)

    private val _broadcast = MutableSharedFlow<Broadcast>()
    val broadcast = _broadcast.asSharedFlow()

    private fun decrypt(key: SecretKey): ByteArray {
        val iv = injection
            .encrypted
            .files
            .readText(injection.pathNames.symmetric)
            .let(::JSONObject)
            .getString("ivDB")
            .base64()
        val services = injection.local.requireServices()
        return injection.security(services)
            .getCipher()
            .decrypt(
                key = key,
                params = IvParameterSpec(iv),
                encrypted = injection.encrypted.files.readBytes(injection.pathNames.dataBase),
            )
    }

    private fun encrypt(
        key: SecretKey,
        decrypted: ByteArray,
    ) {
        val jsonSym = JSONObject(injection.encrypted.files.readText(injection.pathNames.symmetric))
        val services = injection.local.requireServices()
        val cipher = injection.security(services).getCipher()
        injection.encrypted.files.writeBytes(
            pathname = injection.pathNames.dataBase,
            bytes = cipher.encrypt(
                key = key,
                params = IvParameterSpec(jsonSym.getString("ivDB").base64()),
                decrypted = decrypted,
            ),
        )
        injection.encrypted.files.writeBytes(
            pathname = injection.pathNames.dataBaseSignature,
            bytes = injection.security(services)
                .getSignature()
                .sign(
                    key = injection.security(services).getKeyFactory().generatePrivate(
                        bytes = cipher.decrypt(
                            key = key,
                            params = IvParameterSpec(jsonSym.getString("ivPrivate").base64()),
                            encrypted = injection
                                .encrypted
                                .files
                                .readText(injection.pathNames.asymmetric)
                                .let(::JSONObject)
                                .getString("private")
                                .base64(),
                        ),
                    ),
                    random = injection.security(services).getSecureRandom(),
                    decrypted = decrypted,
                ),
        )
    }

    private fun loading(block: suspend () -> Unit) {
        injection.launch {
            _operations.value += 1
            block()
            _operations.value -= 1
        }
    }

    private fun decrypted(key: SecretKey): JSONObject {
        return JSONObject(decrypt(key).toString(Charsets.UTF_8))
    }

    fun requestValues(key: SecretKey) {
        logger.debug("request values...")
        loading {
            _encrypteds.value = withContext(injection.contexts.default) {
                decrypted(key)
                    .getSecrets()
                    .toMap()
            }
        }
    }

    fun requestToCopy(key: SecretKey, id: String) {
        logger.debug("request to copy: $id")
        loading {
            logger.debug("request to copy: $id")
            val secret = withContext(injection.contexts.default) {
                decrypted(key)
                    .getSecrets()
                    .getJSONObject(id)
                    .getSecret()
            }
            _broadcast.emit(Broadcast.OnCopy(secret = secret))
        }
    }

    fun requestToShow(key: SecretKey, id: String) {
        logger.debug("request to show: $id")
        loading {
            val secret = withContext(injection.contexts.default) {
                decrypted(key)
                    .getSecrets()
                    .getJSONObject(id)
                    .getSecret()
            }
            _broadcast.emit(Broadcast.OnShow(secret = secret))
        }
    }

    @Suppress("IgnoredReturnValue")
    fun addValue(key: SecretKey, title: String, secret: String) {
        logger.debug("add: $title")
        check(title.isNotBlank())
        check(secret.isNotBlank())
        loading {
            _encrypteds.value = withContext(injection.contexts.default) {
                val decrypted = decrypted(key)
                val secrets = decrypted.getSecrets()
                val id = generateSequence {
                    UUID.randomUUID().toString()
                }.firstOrNull {
                    !secrets.has(it)
                } ?: error("Failed to generate ID!")
                logger.debug("generate id: $id")
                secrets.put(
                    id,
                    JSONObject().put("title", title).put("secret", secret),
                )
                decrypted.put("updated", System.currentTimeMillis())
                encrypt(
                    key = key,
                    decrypted = decrypted.toString().toByteArray(),
                )
                secrets.toMap()
            }
        }
    }

    @Suppress("IgnoredReturnValue")
    fun deleteValue(key: SecretKey, id: String) {
        logger.debug("delete: $id")
        loading {
            _encrypteds.value = withContext(injection.contexts.default) {
                val decrypted = decrypted(key)
                val secrets = decrypted.getSecrets()
                if (!secrets.has(id)) TODO()
                secrets.remove(id)
                decrypted.put("updated", System.currentTimeMillis())
                encrypt(
                    key = key,
                    decrypted = decrypted.toString().toByteArray(),
                )
                secrets.toMap()
            }
        }
    }
}
