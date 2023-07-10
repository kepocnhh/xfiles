package org.kepocnhh.xfiles.module.unlocked

import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.kepocnhh.xfiles.util.security.decrypt
import org.kepocnhh.xfiles.util.security.encrypt
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

internal class UnlockedViewModel : ViewModel() {
    private val algorithm = "PBEWITHHMACSHA256ANDAES_256" // todo

    private val _data = MutableStateFlow<Map<String, String>?>(null)
    val data = _data.asStateFlow()

    private fun JSONObject.toMap(): Map<String, String> {
        val result = mutableMapOf<String, String>()
        keys().forEach { key ->
            result[key] = getString(key)
        }
        return result
    }

    private fun decrypt(parent: File, key: SecretKey): ByteArray {
        val jsonObject = JSONObject(parent.resolve("sym.json").readText())
        val iv = jsonObject.getString("iv").let { Base64.decode(it, Base64.DEFAULT) }
        val cipher = Cipher.getInstance(algorithm)
        val params = IvParameterSpec(iv)
        val encrypted = parent.resolve("db.json.enc").readBytes()
        return cipher.decrypt(key, params, encrypted)
    }

    private fun encrypt(
        sym: File,
        asym: File,
        target: File,
        sig: File,
        key: SecretKey,
        decrypted: ByteArray,
    ) {
        val mata = JSONObject(sym.readText())
        val iv = mata.getString("iv").let { Base64.decode(it, Base64.DEFAULT) }
        val cipher = Cipher.getInstance(algorithm)
        val params = IvParameterSpec(iv)
        val pair = KeyFactory.getInstance("DSA").let { factory ->
            val jsonObject = JSONObject(asym.readText())
            val public = jsonObject.getString("public").let {
                Base64.decode(it, Base64.DEFAULT)
            }
            val encrypted = jsonObject.getString("private").let {
                Base64.decode(it, Base64.DEFAULT)
            }
            val private = cipher.decrypt(key, params, encrypted)
            KeyPair(
                factory.generatePublic(X509EncodedKeySpec(public)),
                factory.generatePrivate(PKCS8EncodedKeySpec(private)),
            )
        }
        target.writeBytes(cipher.encrypt(key, params, decrypted))
        val random = getSecureRandom()
        Signature.getInstance("SHA256WithDSA").also { signature ->
            signature.initSign(pair.private, random)
            signature.update(decrypted)
            sig.writeBytes(signature.sign())
        }
    }

    fun requestData(parent: File, key: SecretKey) {
        viewModelScope.launch {
            val map = withContext(Dispatchers.IO) {
                JSONObject(decrypt(parent, key).toString(Charsets.UTF_8)).toMap()
            }
            _data.value = map
        }
    }

    fun addData(parent: File, key: SecretKey, name: String, value: String) {
        if (name.trim().isEmpty()) TODO()
        if (value.trim().isEmpty()) TODO()
        viewModelScope.launch {
            val map = withContext(Dispatchers.IO) {
                val decrypted = decrypt(parent, key)
                val jsonObject = JSONObject(decrypted.toString(Charsets.UTF_8))
                if (jsonObject.has(name)) TODO()
                jsonObject.put(name, value)
                encrypt(
                    sym = parent.resolve("sym.json"),
                    asym = parent.resolve("asym.json"),
                    target = parent.resolve("db.json.enc"),
                    sig = parent.resolve("db.json.sig"),
                    key = key,
                    decrypted = jsonObject.toString().toByteArray(),
                )
                jsonObject.toMap()
            }
            _data.value = map
        }
    }
}
