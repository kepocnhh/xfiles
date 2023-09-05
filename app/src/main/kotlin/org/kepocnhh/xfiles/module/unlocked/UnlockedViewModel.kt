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
import org.kepocnhh.xfiles.module.app.Injection
import org.kepocnhh.xfiles.provider.readBytes
import org.kepocnhh.xfiles.provider.readText
import org.kepocnhh.xfiles.util.base64
import org.kepocnhh.xfiles.util.lifecycle.AbstractViewModel
import org.kepocnhh.xfiles.util.security.generateKeyPair
import java.security.KeyFactory
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

internal class UnlockedViewModel(private val injection: Injection) : AbstractViewModel() {
    sealed interface Broadcast {
        class OnCopy(val secret: String) : Broadcast
        class OnShow(val secret: String) : Broadcast
    }

    private val _data = MutableStateFlow<Map<String, String>?>(null)
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

    private fun decrypt(key: SecretKey): ByteArray {
        val jsonObject = JSONObject(injection.files.readText("sym.json"))
        val services = injection.local.services ?: TODO()
        val cipher = injection.security(services).getCipher()
        val params = IvParameterSpec(jsonObject.getString("iv").base64())
        val encrypted = injection.files.readBytes("db.json.enc")
        return cipher.decrypt(key, params, encrypted)
    }

    private fun encrypt(
        key: SecretKey,
        decrypted: ByteArray,
    ) {
        val jsonObject = JSONObject(injection.files.readText("sym.json"))
        val services = injection.local.services ?: TODO()
        val cipher = injection.security(services).getCipher()
        val params = IvParameterSpec(jsonObject.getString("iv").base64())
        val pair = JSONObject(injection.files.readText("asym.json")).let { json ->
            KeyFactory.getInstance("DSA").generateKeyPair(
                public = json.getString("public").base64(),
                private = cipher.decrypt(key, params, json.getString("private").base64())
            )
        }
        injection.files.writeBytes("db.json.enc", cipher.encrypt(key, params, decrypted))
        val random = injection.security(services).getSecureRandom()
        injection.security(services).getSignature().also { signature ->
            injection.files.writeBytes("db.json.sig", signature.sign(pair.private, random, decrypted = decrypted))
        }
    }

    fun requestData(key: SecretKey) {
        injection.launch {
            _data.value = withContext(injection.contexts.default) {
                JSONObject(decrypt(key).toString(Charsets.UTF_8)).toMap()
            }
        }
    }

    fun requestToCopy(key: SecretKey, name: String) {
        viewModelScope.launch {
            val value = withContext(Dispatchers.IO) {
                val jsonObject = JSONObject(decrypt(key).toString(Charsets.UTF_8))
                jsonObject.getString(name)
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
