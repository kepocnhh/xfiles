package org.kepocnhh.xfiles.module.enter

import android.util.Base64
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
import org.kepocnhh.xfiles.provider.readBytes
import org.kepocnhh.xfiles.provider.readText
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
import java.security.spec.KeySpec
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

    private fun create(pin: String): SecretKey {
        val chars = hash(pin = pin).base64().toCharArray()
        val random = getSecureRandom()
        val meta = KeyMeta(
            salt = ByteArray(32).also(random::nextBytes),
            iv = ByteArray(16).also(random::nextBytes),
            iterations = 1_048_576,
            bits = 256,
        )
        injection.files.writeBytes("sym.json", meta.toJson().toString().toByteArray())
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
        injection.files.writeBytes("db.json.enc", cipher.encrypt(key, params, decrypted))
        val private = cipher.encrypt(key, params, pair.private.encoded)
        JSONObject()
            .put("public", pair.public.encoded.base64())
            .put("private", private.base64())
            .also { json ->
                injection.files.writeBytes("asym.json", json.toString().toByteArray())
            }
        Signature.getInstance("SHA256WithDSA").also { signature ->
            signature.initSign(pair.private, random)
            signature.update(decrypted)
            injection.files.writeBytes("db.json.sig", signature.sign())
        }
        return key
    }

    fun createNewFile(pin: String) {
        injection.launch {
            _exists.value = null
            val key = withContext(injection.contexts.default) {
                create(pin)
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

    private fun JSONObject.toKeyMeta(): KeyMeta {
        return KeyMeta(
            salt = getString("salt").let { Base64.decode(it, Base64.DEFAULT) },
            iv = getString("iv").let { Base64.decode(it, Base64.DEFAULT) },
            iterations = getInt("iterations"),
            bits = getInt("bits"),
        )
    }

    private fun KeyFactory.generateKeyPair(
        publicKeySpec: KeySpec,
        privateKeySpec: KeySpec,
    ): KeyPair {
        return KeyPair(
            generatePublic(publicKeySpec),
            generatePrivate(privateKeySpec),
        )
    }

    private fun KeyFactory.generateKeyPair(
        public: ByteArray,
        private: ByteArray,
    ): KeyPair {
        return generateKeyPair(
            publicKeySpec = X509EncodedKeySpec(public),
            privateKeySpec = PKCS8EncodedKeySpec(private),
        )
    }

    private fun unlock(pin: String): SecretKey {
        val chars = hash(pin = pin).base64().toCharArray()
        val meta = JSONObject(injection.files.readText("sym.json")).toKeyMeta()
        val cipher = Cipher.getInstance(algorithm)
        val key = SecretKeyFactory.getInstance(cipher.algorithm).let { factory ->
            val spec = PBEKeySpec(chars, meta.salt, meta.iterations, meta.bits)
            factory.generateSecret(spec)
        }
        val params = IvParameterSpec(meta.iv)
        val pair = JSONObject(injection.files.readText("asym.json")).let { json ->
            val public = json.getString("public").let { Base64.decode(it, Base64.DEFAULT) }
            val encrypted = json.getString("private").let { Base64.decode(it, Base64.DEFAULT) }
            KeyFactory.getInstance("DSA").generateKeyPair(
                public = public,
                private = cipher.decrypt(key, params, encrypted)
            )
        }
        val decrypted = cipher.decrypt(key, params, injection.files.readBytes("db.json.enc"))
        Signature.getInstance("SHA256WithDSA").also { signature ->
            signature.initVerify(pair.public)
            signature.update(decrypted)
            val verified = signature.verify(injection.files.readBytes("db.json.sig"))
            check(verified)
        }
        return key
    }

    fun unlockFile(pin: String) {
        injection.launch {
            _exists.value = null
            val result = withContext(injection.contexts.default) {
                runCatching {
                    unlock(pin = pin)
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
