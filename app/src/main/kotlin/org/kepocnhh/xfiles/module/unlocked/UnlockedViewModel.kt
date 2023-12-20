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
            .readBytes(injection.pathNames.symmetric)
            .let(injection.serializer::toKeyMeta)
            .ivDB
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
        val symmetric = injection
            .encrypted
            .files
            .readBytes(injection.pathNames.symmetric)
            .let(injection.serializer::toKeyMeta)
        val services = injection.local.requireServices()
        val cipher = injection.security(services).getCipher()
        injection.encrypted.files.writeBytes(
            pathname = injection.pathNames.dataBase,
            bytes = cipher.encrypt(
                key = key,
                params = IvParameterSpec(symmetric.ivDB),
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
                            params = IvParameterSpec(symmetric.ivPrivate),
                            encrypted = injection
                                .encrypted
                                .files
                                .readBytes(injection.pathNames.asymmetric)
                                .let(injection.serializer::toAsymmetricKey)
                                .privateEncrypted,
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
            try {
                block()
            } finally {
                _operations.value -= 1
            }
        }
    }

    private fun decrypted(key: SecretKey): JSONObject {
        return JSONObject(decrypt(key).toString(Charsets.UTF_8))
    }

    fun requestValues(key: SecretKey) {
        logger.debug("request values...")
        loading {
            _encrypteds.value = withContext(injection.contexts.default) {
                injection.serializer.toDataBase(decrypt(key)).secrets.mapValues { (_, pair) ->
                    val (title, _) = pair
                    title
                }
            }
        }
    }

    private fun requireSecret(key: SecretKey, id: String): String {
        val (_, secret) = injection
            .serializer
            .toDataBase(decrypt(key))
            .secrets[id] ?: error("No secret by \"$id\"!")
        return secret
    }

    fun requestToCopy(key: SecretKey, id: String) {
        logger.debug("request to copy: $id")
        loading {
            val secret = withContext(injection.contexts.default) {
                requireSecret(key = key, id = id)
            }
            _broadcast.emit(Broadcast.OnCopy(secret = secret))
        }
    }

    fun requestToShow(key: SecretKey, id: String) {
        logger.debug("request to show: $id")
        loading {
            val secret = withContext(injection.contexts.default) {
                requireSecret(key = key, id = id)
            }
            _broadcast.emit(Broadcast.OnShow(secret = secret))
        }
    }

    fun addValue(key: SecretKey, title: String, secret: String) {
        logger.debug("add: $title")
        check(title.isNotBlank())
        check(secret.isNotBlank())
        loading {
            _encrypteds.value = withContext(injection.contexts.default) {
                val dataBase = injection.serializer.toDataBase(decrypt(key))
                val secrets = dataBase.secrets.toMutableMap()
                val services = injection.local.requireServices()
                val uuids = injection.security(services).uuids()
                val id = generateSequence {
                    uuids.generate().toString()
                }.firstOrNull {
                    !secrets.containsKey(it)
                } ?: error("Failed to generate ID!")
                logger.debug("generate id: $id")
                secrets[id] = title to secret
                encrypt(
                    key = key,
                    decrypted = dataBase.copy(
                        updated = injection.time.now(),
                        secrets = secrets,
                    ).let(injection.serializer::serialize),
                )
                secrets.mapValues { (_, pair) ->
                    val (t, _) = pair
                    t
                }
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
                decrypted.put("updated", injection.time.now().inWholeMilliseconds)
                encrypt(
                    key = key,
                    decrypted = decrypted.toString().toByteArray(),
                )
                secrets.toMap()
            }
        }
    }
}
