package org.kepocnhh.xfiles.module.unlocked

import android.util.Base64
import androidx.lifecycle.ViewModel
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
import org.kepocnhh.xfiles.util.security.decrypt
import org.kepocnhh.xfiles.util.security.encrypt
import org.kepocnhh.xfiles.util.security.generateKeyPair
import org.kepocnhh.xfiles.util.security.getSecureRandom
import java.io.File
import java.security.KeyFactory
import java.security.KeyPair
import java.security.Signature
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec

internal class UnlockedViewModel(private val injection: Injection) : AbstractViewModel() {
    sealed interface Broadcast {
        class OnCopy(val secret: String) : Broadcast
        class OnShow(val secret: String) : Broadcast
    }

    private val algorithm = "PBEWITHHMACSHA256ANDAES_256" // todo

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
        val iv = jsonObject.getString("iv").let { Base64.decode(it, Base64.DEFAULT) }
        val cipher = Cipher.getInstance(algorithm)
        val params = IvParameterSpec(iv)
        val encrypted = injection.files.readBytes("db.json.enc")
        return cipher.decrypt(key, params, encrypted)
    }

    private fun encrypt(
        key: SecretKey,
        decrypted: ByteArray,
    ) {
        val mata = JSONObject(injection.files.readText("sym.json"))
        val cipher = Cipher.getInstance(algorithm)
        val params = IvParameterSpec(mata.getString("iv").base64())
        val pair = JSONObject(injection.files.readText("asym.json")).let { json ->
            KeyFactory.getInstance("DSA").generateKeyPair(
                public = json.getString("public").base64(),
                private = cipher.decrypt(key, params, json.getString("private").base64())
            )
        }
        injection.files.writeBytes("db.json.enc", cipher.encrypt(key, params, decrypted))
        val random = getSecureRandom()
        Signature.getInstance("SHA256WithDSA").also { signature ->
            signature.initSign(pair.private, random)
            signature.update(decrypted)
            injection.files.writeBytes("db.json.sig", signature.sign())
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
