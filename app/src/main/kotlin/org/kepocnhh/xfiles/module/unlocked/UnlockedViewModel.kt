package org.kepocnhh.xfiles.module.unlocked

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.kepocnhh.xfiles.entity.EncryptedValue
import org.kepocnhh.xfiles.module.app.Injection
import org.kepocnhh.xfiles.provider.readBytes
import org.kepocnhh.xfiles.provider.readText
import org.kepocnhh.xfiles.util.base64
import org.kepocnhh.xfiles.util.lifecycle.AbstractViewModel
import org.kepocnhh.xfiles.util.security.generateKeyPair
import java.security.KeyFactory
import java.util.UUID
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import kotlin.time.Duration.Companion.seconds

internal class UnlockedViewModel(private val injection: Injection) : AbstractViewModel() {
    sealed interface Broadcast {
        class OnCopy(val secret: String) : Broadcast
        class OnShow(val secret: String) : Broadcast
    }

    private val logger = injection.loggers.newLogger("[Unlocked]")

    init {
        // todo
        logger.debug("init")
    }

    override fun onCleared() {
        // todo
        logger.debug("on cleared")
    }

//    private val _loading = MutableStateFlow(true)
//    val loading = _loading.asStateFlow()

    private val _operations = MutableStateFlow(0)
    val loading = _operations
        .map {
            encrypteds.value == null || it > 0
        }
        .stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            true,
        )

    private val _encrypteds = MutableStateFlow<List<EncryptedValue>?>(null)
    val encrypteds = _encrypteds.asStateFlow()

    @Deprecated(message = "_encrypteds")
    private val _data = MutableStateFlow<Map<String, String>?>(null)
    @Deprecated(message = "encrypteds")
    val data = _data.asStateFlow()

    private val _broadcast = MutableSharedFlow<Broadcast>()
    val broadcast = _broadcast.asSharedFlow()

    private fun JSONObject.toMap(): Map<String, String> {
        val result = mutableMapOf<String, String>()
        keys().forEach { key ->
            result[key] = getString(key)
        }
        return result
    }

    private fun JSONObject.toList(): List<EncryptedValue> {
        return keys().asSequence().map { id ->
            EncryptedValue(
                id = id,
                title = getJSONObject(id).getString("title"),
            )
        }.toList()
    }

    private fun decrypt(key: SecretKey): ByteArray {
        val jsonObject = JSONObject(injection.files.readText(injection.pathNames.symmetric))
        val services = injection.local.services ?: TODO()
        return injection.security(services)
            .getCipher()
            .decrypt(
                key = key,
                params = IvParameterSpec(jsonObject.getString("ivDB").base64()),
                encrypted = injection.files.readBytes(injection.pathNames.dataBase),
            )
    }

    private fun encrypt(
        key: SecretKey,
        decrypted: ByteArray,
    ) {
        val jsonSym = JSONObject(injection.files.readText(injection.pathNames.symmetric))
        val jsonAsym = JSONObject(injection.files.readText(injection.pathNames.asymmetric))
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
                            encrypted = jsonAsym.getString("private").base64(),
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
            delay(2.seconds)
            block()
            _operations.value -= 1
        }
    }

    fun requestValues(key: SecretKey) {
        loading {
            _encrypteds.value = withContext(injection.contexts.default) {
                JSONObject(decrypt(key).toString(Charsets.UTF_8)).toList()
            }
        }
    }

    @Deprecated(message = "requestValues")
    fun requestData(key: SecretKey) {
        injection.launch {
            _data.value = withContext(injection.contexts.default) {
                JSONObject(decrypt(key).toString(Charsets.UTF_8)).toMap()
            }
        }
    }

    fun requestToCopy(key: SecretKey, id: String) {
        loading {
            val value = withContext(injection.contexts.default) {
                JSONObject(decrypt(key).toString(Charsets.UTF_8))
                    .getJSONObject(id)
                    .getString("value")
            }
            _broadcast.emit(Broadcast.OnCopy(value))
        }
    }

    fun requestToShow(key: SecretKey, name: String) {
        viewModelScope.launch {
            val value = withContext(Dispatchers.IO) {
                val jsonObject = JSONObject(decrypt(key).toString(Charsets.UTF_8))
                jsonObject.getString(name)
            }
            _broadcast.emit(Broadcast.OnShow(value))
        }
    }

    fun addValue(key: SecretKey, title: String, value: String) {
        check(title.isNotBlank())
        check(value.isNotBlank())
        loading {
            _encrypteds.value = withContext(injection.contexts.default) {
                val jsonObject = JSONObject(decrypt(key).toString(Charsets.UTF_8))
                jsonObject.put(
                    generateSequence { UUID.randomUUID().toString() }
                        .firstOrNull { !jsonObject.has(it) }
                        ?: TODO(),
                    JSONObject().put("title", title).put("value", value),
                )
                encrypt(
                    key = key,
                    decrypted = jsonObject.toString().toByteArray(),
                )
                jsonObject.toList()
            }
        }
    }

    @Deprecated(message = "addValue")
    fun addData(key: SecretKey, name: String, value: String) {
        if (name.trim().isEmpty()) TODO()
        if (value.trim().isEmpty()) TODO()
        viewModelScope.launch {
            val map = withContext(Dispatchers.IO) {
                val decrypted = decrypt(key)
                val jsonObject = JSONObject(decrypted.toString(Charsets.UTF_8))
                if (jsonObject.has(name)) TODO()
                jsonObject.put(name, value)
                encrypt(
                    key = key,
                    decrypted = jsonObject.toString().toByteArray(),
                )
                jsonObject.toMap()
            }
            _data.value = map
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
                jsonObject.toList()
            }
        }
    }

    @Deprecated(message = "deleteValue")
    fun deleteData(key: SecretKey, name: String) {
        println("delete: \"$name\"")
        if (name.trim().isEmpty()) TODO()
        viewModelScope.launch {
            val map = withContext(Dispatchers.IO) {
                val decrypted = decrypt(key)
                val decoded = decrypted.toString(Charsets.UTF_8)
                println("decoded: $decoded")
                val jsonObject = JSONObject(decoded)
                if (!jsonObject.has(name)) TODO("$decoded has no \"$name\"")
                jsonObject.remove(name)
                println("decrypt: $jsonObject")
                encrypt(
                    key = key,
                    decrypted = jsonObject.toString().toByteArray(),
                )
                jsonObject.toMap()
            }
            _data.value = map
        }
    }
}
