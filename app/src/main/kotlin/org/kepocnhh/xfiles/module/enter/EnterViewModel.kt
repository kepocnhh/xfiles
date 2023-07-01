package org.kepocnhh.xfiles.module.enter

import android.os.Build
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
import java.io.File
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.SecureRandom
import java.security.Security
import java.security.spec.AlgorithmParameterSpec
import java.security.spec.PKCS8EncodedKeySpec
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.PBEParameterSpec

internal class EnterViewModel : ViewModel() {
    sealed interface Broadcast {
        object OnCreate : Broadcast
        object OnUnlock : Broadcast
        object OnUnlockError : Broadcast
    }

    private val _broadcast = MutableSharedFlow<Broadcast>()
    val broadcast = _broadcast.asSharedFlow()

    private val _exists = MutableStateFlow<Boolean?>(null)
    val exists = _exists.asStateFlow()

    private val size = 256 // 32 bytes
//    private val algorithm = "PBEWITHHMACSHA256ANDAES_128" // https://datatracker.ietf.org/doc/html/rfc8018
    private val algorithm = "PBEWITHHMACSHA256ANDAES_$size"
//    private val algorithm = "PBEWITHHMACSHA512ANDAES_256"
    private val iterations = 1_048_576
//    private var salt: ByteArray = ByteArray(0)
//    private var iv: ByteArray = ByteArray(0)
//    private var key: SecretKey? = null

    fun requestFile(parent: File) {
        viewModelScope.launch {
            _exists.value = withContext(Dispatchers.IO) {
                parent.resolve("db.json.enc").exists()
            }
        }
    }

    private fun getSecureRandom(): SecureRandom {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            SecureRandom.getInstanceStrong()
        } else {
            SecureRandom.getInstance("SHA1PRNG")
        }
    }

    private data class KeyMeta(val salt: ByteArray, val iv: ByteArray)

    fun createNewFile(parent: File, pin: String) {
        viewModelScope.launch {
            _exists.value = null
            withContext(Dispatchers.IO) {
                val random = getSecureRandom()
                val factory = SecretKeyFactory.getInstance(algorithm)
                val sym1 = KeyMeta(
                    salt = ByteArray(32).also(random::nextBytes),
                    iv = ByteArray(16).also(random::nextBytes)
                )
                JSONObject()
                    .put("salt", Base64.encodeToString(sym1.salt, Base64.DEFAULT))
                    .put("iv", Base64.encodeToString(sym1.iv, Base64.DEFAULT))
                    .also { json ->
                        parent.resolve("sym1.json").writeText(json.toString())
                    }
                val pair = KeyPairGenerator.getInstance("RSA").let { generator ->
                    generator.initialize(2048, random)
                    generator.generateKeyPair()
                }
                Cipher.getInstance(algorithm).also { cipher ->
                    val spec = PBEKeySpec(pin.toCharArray(), sym1.salt, iterations, size)
                    val key = factory.generateSecret(spec)
                    val params = IvParameterSpec(sym1.iv)
                    cipher.init(Cipher.ENCRYPT_MODE, key, params)
                    JSONObject()
                        .put("public", Base64.encodeToString(pair.public.encoded, Base64.DEFAULT))
                        .put("private", Base64.encodeToString(cipher.doFinal(pair.private.encoded), Base64.DEFAULT))
                        .also { json ->
                            parent.resolve("asym.json").writeText(json.toString())
                        }
                }
                val sym2 = KeyMeta(
                    salt = ByteArray(32).also(random::nextBytes),
                    iv = ByteArray(16).also(random::nextBytes)
                )
                JSONObject()
                    .put("salt", Base64.encodeToString(sym2.salt, Base64.DEFAULT))
                    .put("iv", Base64.encodeToString(sym2.iv, Base64.DEFAULT))
                    .also { json ->
                        parent.resolve("sym2.json").writeText(json.toString())
                    }
                val password = ByteArray(32).also(random::nextBytes)
                Cipher.getInstance("RSA/ECB/PKCS1Padding").also { cipher ->
                    cipher.init(Cipher.ENCRYPT_MODE, pair.public)
                    parent.resolve("sym2.enc")
                        .writeText(Base64.encodeToString(cipher.doFinal(password), Base64.DEFAULT))
                }
                Cipher.getInstance(algorithm).also { cipher ->
                    val chars = Base64.encodeToString(password, Base64.DEFAULT).toCharArray()
                    val spec = PBEKeySpec(chars, sym2.salt, iterations, size)
                    val key = factory.generateSecret(spec)
                    cipher.init(Cipher.ENCRYPT_MODE, key, IvParameterSpec(sym2.iv))
                    val decrypted = "{}"
                    val encoded = Base64.encode(decrypted.toByteArray(), Base64.DEFAULT)
                    val encrypted = cipher.doFinal(encoded)
                    parent.resolve("db.json.enc")
                        .writeText(Base64.encodeToString(encrypted, Base64.DEFAULT))
                }
            }
            _exists.value = true // todo
            _broadcast.emit(Broadcast.OnCreate)
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

    fun unlockFile(parent: File, pin: String) {
        println("unlock: $pin")
        viewModelScope.launch {
            _exists.value = null
            val value: Broadcast = withContext(Dispatchers.IO) {
                val factory = SecretKeyFactory.getInstance(algorithm)
                val sym1 = JSONObject(parent.resolve("sym1.json").readText()).let { json ->
                    KeyMeta(
                        salt = json.getString("salt").let { Base64.decode(it, Base64.DEFAULT) },
                        iv = json.getString("iv").let { Base64.decode(it, Base64.DEFAULT) },
                    )
                }
                val private = Cipher.getInstance(algorithm).let { cipher ->
                    val encrypted = JSONObject(parent.resolve("asym.json").readText()).let { json ->
                        json.getString("private").let { Base64.decode(it, Base64.DEFAULT) }
                    }
                    val spec = PBEKeySpec(pin.toCharArray(), sym1.salt, iterations, size)
                    val key = factory.generateSecret(spec)
                    val params = IvParameterSpec(sym1.iv)
                    cipher.init(Cipher.DECRYPT_MODE, key, params)
                    KeyFactory.getInstance("RSA")
                        .generatePrivate(PKCS8EncodedKeySpec(cipher.doFinal(encrypted)))
                }
//                val sym2 = JSONObject(parent.resolve("sym2.json").readText()).let { json ->
//                    KeyMeta(
//                        salt = json.getString("salt").let { Base64.decode(it, Base64.DEFAULT) },
//                        iv = json.getString("iv").let { Base64.decode(it, Base64.DEFAULT) },
//                    )
//                }
                val password = Cipher.getInstance("RSA/ECB/PKCS1Padding").let { cipher ->
                    cipher.init(Cipher.DECRYPT_MODE, private)
                    val encoded = parent.resolve("sym2.enc").readBytes()
                    val encrypted = Base64.decode(encoded, Base64.DEFAULT)
                    cipher.doFinal(encrypted)
                }
                Broadcast.OnUnlock
            }
            if (value == Broadcast.OnUnlockError) {
                _exists.value = true
            }
            _broadcast.emit(value)
        }
    }
}
