package org.kepocnhh.xfiles.module.unlocked

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
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

internal class UnlockedViewModel(private val injection: Injection) : AbstractViewModel() {
    sealed interface Broadcast {
        class OnCopy(val secret: String) : Broadcast
        class OnShow(val secret: String) : Broadcast
    }

    private val logger = injection.loggers.newLogger("[Unlocked]")

    private val _loading = MutableStateFlow(true)
    val loading = _loading.asStateFlow()

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
        val pair = injection.security(services).getKeyFactory().generate(
            public = jsonAsym.getString("public").base64(),
            private = cipher.decrypt(
                key = key,
                params = IvParameterSpec(jsonSym.getString("ivPrivate").base64()),
                encrypted = jsonAsym.getString("private").base64(),
            ),
        )
        injection.files.writeBytes(
            pathname = injection.pathNames.dataBase,
            bytes = cipher.encrypt(
                key = key,
                params = IvParameterSpec(jsonSym.getString("ivDB").base64()),
                decrypted = decrypted,
            ),
        )
        val random = injection.security(services).getSecureRandom()
        val sig = injection.security(services)
            .getSignature()
            .sign(pair.private, random, decrypted = decrypted)
        injection.files.writeBytes(injection.pathNames.dataBaseSignature, sig)
    }

    fun requestValues(key: SecretKey) {
        injection.launch {
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
        logger.debug("request to copy...")
        injection.launch {
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
        injection.launch {
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
        injection.launch {
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
