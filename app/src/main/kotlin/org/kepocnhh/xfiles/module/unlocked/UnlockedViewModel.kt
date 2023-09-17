package org.kepocnhh.xfiles.module.unlocked

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.kepocnhh.xfiles.module.app.Injection
import org.kepocnhh.xfiles.provider.readBytes
import org.kepocnhh.xfiles.provider.readText
import org.kepocnhh.xfiles.util.base64
import org.kepocnhh.xfiles.util.lifecycle.AbstractViewModel
import java.util.UUID
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

internal class UnlockedViewModel(private val injection: Injection) : AbstractViewModel() {
    sealed interface Broadcast {
        class OnCopy(val secret: String) : Broadcast
        class OnShow(val secret: String) : Broadcast
    }

    private val _operations = MutableStateFlow(0)
    val loading = _operations
        .map {
            encrypteds.value == null || it > 0
        }
        .stateIn(true)

    private val _encrypteds = MutableStateFlow<Map<String, String>?>(null)
    val encrypteds = _encrypteds.asStateFlow()

    private val _broadcast = MutableSharedFlow<Broadcast>()
    val broadcast = _broadcast.asSharedFlow()

    private fun JSONObject.toMap(): Map<String, String> {
        return keys().asSequence().associateWith { id ->
            getJSONObject(id).getString("title")
        }
    }

    private fun decrypt(key: SecretKey): ByteArray {
        val iv = injection
            .files
            .readText(injection.pathNames.symmetric)
            .let(::JSONObject)
            .getString("ivDB")
            .base64()
        val services = injection.local.services ?: TODO()
        return injection.security(services)
            .getCipher()
            .decrypt(
                key = key,
                params = IvParameterSpec(iv),
                encrypted = injection.files.readBytes(injection.pathNames.dataBase),
            )
    }

    private fun encrypt(
        key: SecretKey,
        decrypted: ByteArray,
    ) {
        val jsonSym = JSONObject(injection.files.readText(injection.pathNames.symmetric))
        val services = injection.local.services ?: TODO()
        val cipher = injection.security(services).getCipher()
        injection.files.writeBytes(
            pathname = injection.pathNames.dataBase,
            bytes = cipher.encrypt(
                key = key,
                params = IvParameterSpec(jsonSym.getString("ivDB").base64()),
                decrypted = decrypted,
            ),
        )
        injection.files.writeBytes(
            pathname = injection.pathNames.dataBaseSignature,
            bytes = injection.security(services)
                .getSignature()
                .sign(
                    key = injection.security(services).getKeyFactory().generatePrivate(
                        bytes = cipher.decrypt(
                            key = key,
                            params = IvParameterSpec(jsonSym.getString("ivPrivate").base64()),
                            encrypted = injection
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

    fun requestValues(key: SecretKey) {
        loading {
            _encrypteds.value = withContext(injection.contexts.default) {
                JSONObject(decrypt(key).toString(Charsets.UTF_8)).toMap()
            }
        }
    }

    fun requestToCopy(key: SecretKey, id: String) {
        loading {
            val secret = withContext(injection.contexts.default) {
                JSONObject(decrypt(key).toString(Charsets.UTF_8))
                    .getJSONObject(id)
                    .getString("secret")
            }
            _broadcast.emit(Broadcast.OnCopy(secret = secret))
        }
    }

    fun requestToShow(key: SecretKey, id: String) {
        loading {
            val secret = withContext(injection.contexts.default) {
                JSONObject(decrypt(key).toString(Charsets.UTF_8))
                    .getJSONObject(id)
                    .getString("secret")
            }
            _broadcast.emit(Broadcast.OnShow(secret = secret))
        }
    }

    fun addValue(key: SecretKey, title: String, secret: String) {
        check(title.isNotBlank())
        check(secret.isNotBlank())
        loading {
            _encrypteds.value = withContext(injection.contexts.default) {
                val jsonObject = JSONObject(decrypt(key).toString(Charsets.UTF_8))
                jsonObject.put(
                    generateSequence(UUID.randomUUID()::toString)
                        .firstOrNull { !jsonObject.has(it) }
                        ?: TODO(),
                    JSONObject().put("title", title).put("secret", secret),
                )
                encrypt(
                    key = key,
                    decrypted = jsonObject.toString().toByteArray(),
                )
                jsonObject.toMap()
            }
        }
    }

    fun deleteValue(key: SecretKey, id: String) {
        loading {
            _encrypteds.value = withContext(injection.contexts.default) {
                val jsonObject = JSONObject(decrypt(key).toString(Charsets.UTF_8))
                if (!jsonObject.has(id)) TODO()
                jsonObject.remove(id)
                encrypt(
                    key = key,
                    decrypted = jsonObject.toString().toByteArray(),
                )
                jsonObject.toMap()
            }
        }
    }
}
