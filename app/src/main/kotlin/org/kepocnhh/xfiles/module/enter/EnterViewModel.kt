package org.kepocnhh.xfiles.module.enter

import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.kepocnhh.xfiles.entity.KeyMeta
import org.kepocnhh.xfiles.module.app.Injection
import org.kepocnhh.xfiles.util.base64
import org.kepocnhh.xfiles.util.lifecycle.AbstractViewModel
import org.kepocnhh.xfiles.util.security.decrypt
import org.kepocnhh.xfiles.util.security.encrypt
import org.kepocnhh.xfiles.util.security.getSecureRandom
import java.io.File
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.MessageDigest
import java.security.Signature
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec

internal class EnterViewModel(private val injection: Injection) : AbstractViewModel() {
    sealed interface Broadcast {
        class OnUnlock(val key: SecretKey) : Broadcast
        object OnUnlockError : Broadcast
    }

    private val _broadcast = MutableSharedFlow<Broadcast>()
    val broadcast = _broadcast.asSharedFlow()

    private val _exists = MutableStateFlow<Boolean?>(null)
    val exists = _exists.asStateFlow()

    private val algorithm = "PBEWITHHMACSHA256ANDAES_256" // todo

    fun requestFile() {
        injection.launch {
            _exists.value = withContext(injection.contexts.default) {
                injection.files.exists("db.json.enc")
            }
        }
    }

    private fun KeyMeta.toJson(): JSONObject {
        return JSONObject()
            .put("salt", salt.base64())
            .put("iv", iv.base64())
            .put("iterations", iterations)
            .put("bits", bits)
    }

    private fun hash(pin: String): ByteArray {
        val md = MessageDigest.getInstance("SHA-512")
        return md.digest(pin.toByteArray())
    }

    private fun create(parent: File, pin: String): SecretKey {
        val chars = hash(pin = pin).base64().toCharArray()
        val random = getSecureRandom()
        val meta = KeyMeta(
            salt = ByteArray(32).also(random::nextBytes),
            iv = ByteArray(16).also(random::nextBytes),
            iterations = 1_048_576,
            bits = 256,
        )
        parent.resolve("sym.json").writeText(meta.toJson().toString())
        val pair = KeyPairGenerator.getInstance("DSA").let { generator ->
            generator.initialize(2048, random)
            generator.generateKeyPair()
        }
        val decrypted = "{}".toByteArray()
        val cipher = Cipher.getInstance(algorithm)
        val key = SecretKeyFactory.getInstance(cipher.algorithm).let { factory ->
            val spec = PBEKeySpec(chars, meta.salt, meta.iterations, meta.bits)
            factory.generateSecret(spec)
        }
        val params = IvParameterSpec(meta.iv)
        parent.resolve("db.json.enc").writeBytes(cipher.encrypt(key, params, decrypted))
        val private = cipher.encrypt(key, params, pair.private.encoded)
        JSONObject()
            .put("public", pair.public.encoded.base64())
            .put("private", private.base64())
            .also { json ->
                parent.resolve("asym.json").writeText(json.toString())
            }
        Signature.getInstance("SHA256WithDSA").also { signature ->
            signature.initSign(pair.private, random)
            signature.update(decrypted)
            parent.resolve("db.json.sig").writeBytes(signature.sign())
        }
        return key
    }

    fun createNewFile(parent: File, pin: String) {
        viewModelScope.launch {
            _exists.value = null
            val key = withContext(Dispatchers.IO) {
                create(parent, pin)
            }
            _broadcast.emit(Broadcast.OnUnlock(key))
        }
    }

    fun deleteFile(parent: File) {
        viewModelScope.launch {
            _exists.value = null
            withContext(Dispatchers.IO) {
                parent.resolve("db.json.enc").delete()
                delay(2_000)
            }
            _exists.value = false
        }
    }

    private fun unlock(parent: File, pin: String): SecretKey {
        val chars = hash(pin = pin).base64().toCharArray()
        val meta = JSONObject(parent.resolve("sym.json").readText()).let { json ->
            KeyMeta(
                salt = json.getString("salt").let { Base64.decode(it, Base64.DEFAULT) },
                iv = json.getString("iv").let { Base64.decode(it, Base64.DEFAULT) },
                iterations = json.getInt("iterations"),
                bits = json.getInt("bits"),
            )
        }
        val cipher = Cipher.getInstance(algorithm)
        val (public, encrypted) = JSONObject(parent.resolve("asym.json").readText()).let { json ->
            json.getString("public").let {
                Base64.decode(it, Base64.DEFAULT)
            } to json.getString("private").let {
                Base64.decode(it, Base64.DEFAULT)
            }
        }
        val key = SecretKeyFactory.getInstance(cipher.algorithm).let { factory ->
            val spec = PBEKeySpec(chars, meta.salt, meta.iterations, meta.bits)
            factory.generateSecret(spec)
        }
        val params = IvParameterSpec(meta.iv)
        val decrypted = cipher.decrypt(key, params, parent.resolve("db.json.enc").readBytes())
        val private = cipher.decrypt(key, params, encrypted)
        val pair = KeyFactory.getInstance("DSA").let { factory ->
            KeyPair(
                factory.generatePublic(X509EncodedKeySpec(public)),
                factory.generatePrivate(PKCS8EncodedKeySpec(private)),
            )
        }
        Signature.getInstance("SHA256WithDSA").also { signature ->
            signature.initVerify(pair.public)
            signature.update(decrypted)
            val verified = signature.verify(parent.resolve("db.json.sig").readBytes())
            check(verified)
        }
        return key
    }

    fun unlockFile(parent: File, pin: String) {
        println("unlock: $pin") // todo
        viewModelScope.launch {
            _exists.value = null
            val result = withContext(Dispatchers.IO) {
                runCatching {
                    unlock(parent, pin)
                }
            }
            result.fold(
                onFailure = {
                    _exists.value = true
                    _broadcast.emit(Broadcast.OnUnlockError)
                },
                onSuccess = {
                    _broadcast.emit(Broadcast.OnUnlock(it))
                }
            )
        }
    }
}
