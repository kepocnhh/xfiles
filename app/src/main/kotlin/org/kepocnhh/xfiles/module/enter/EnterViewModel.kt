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
import java.lang.StringBuilder
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.security.SecureRandom
import java.security.Security
import java.security.Signature
import java.security.spec.AlgorithmParameterSpec
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
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

    private val bits = 256 // 32 bytes
//    private val algorithm = StringBuilder()
//        .append("PBE")
//        .append("WITH")
//        .append("HMACSHA256")
//        .append("AND")
//        .append("AES_$size")
//    private val algorithm = "PBEWITHHMACSHA256ANDAES_128" // https://datatracker.ietf.org/doc/html/rfc8018
    private val algorithm = "PBEWITHHMACSHA256ANDAES_$bits"
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

    private fun KeyMeta.toJson(): JSONObject {
        return JSONObject()
            .put("salt", salt.base64())
            .put("iv", iv.base64())
    }

    fun createNewFile(parent: File, pin: String) {
        viewModelScope.launch {
            _exists.value = null
            withContext(Dispatchers.IO) {
//                error(Security.getAlgorithms("Signature").toList())
                // [NONEWITHDSA, SHA384WITHECDSA, SHA224WITHDSA, SHA384WITHRSA/PSS, SHA256WITHRSA, MD5WITHRSA, ED25519, SHA1WITHRSA, SHA256WITHRSA/PSS, SHA512WITHRSA, SHA512WITHRSA/PSS, SHA256WITHDSA, SHA1WITHECDSA, NONEWITHECDSA, SHA224WITHRSA, NONEWITHRSA, SHA256WITHECDSA, SHA224WITHECDSA, SHA384WITHRSA, SHA512WITHECDSA, SHA1WITHRSA/PSS, SHA224WITHRSA/PSS, SHA1WITHDSA]
                val random = getSecureRandom()
                val sym1 = KeyMeta(
                    salt = ByteArray(32).also(random::nextBytes),
                    iv = ByteArray(16).also(random::nextBytes)
                )
                parent.resolve("sym1.json").writeText(sym1.toJson().toString())
                val pair = KeyPairGenerator.getInstance("RSA").let { generator ->
                    generator.initialize(2048, random)
                    generator.generateKeyPair()
                }
                Cipher.getInstance(algorithm).also { cipher ->
                    val key = generateSecret(cipher.algorithm, pin.toCharArray(), sym1.salt)
                    val params = IvParameterSpec(sym1.iv)
                    val encrypted = cipher.encrypt(key, params, pair.private.encoded)
                    JSONObject()
                        .put("public", pair.public.encoded.base64())
                        .put("private", encrypted.base64())
                        .also { json ->
                            parent.resolve("asym.json").writeText(json.toString())
                        }
                }
                val sym2 = KeyMeta(
                    salt = ByteArray(32).also(random::nextBytes),
                    iv = ByteArray(16).also(random::nextBytes)
                )
                parent.resolve("sym2.json").writeText(sym2.toJson().toString())
                val password = ByteArray(32).also(random::nextBytes)
                Cipher.getInstance("RSA/ECB/PKCS1Padding").also { cipher ->
                    val encrypted = cipher.encrypt(pair.public, password)
                    parent.resolve("sym2.enc").writeBytes(encrypted)
                }
                val decrypted = "{}".toByteArray()
                Cipher.getInstance(algorithm).also { cipher ->
                    val chars = password.base64().toCharArray()
                    val key = generateSecret(cipher.algorithm, chars, sym2.salt)
                    val params = IvParameterSpec(sym2.iv)
                    val encrypted = cipher.encrypt(key, params, decrypted)
                    parent.resolve("db.json.enc").writeBytes(encrypted)
                }
//                Signature.getInstance("SHA256WithDSA").also { signature ->
                Signature.getInstance("SHA256WITHRSA").also { signature ->
                    signature.initSign(pair.private, random)
                    signature.update(decrypted)
                    parent.resolve("db.json.sig").writeBytes(signature.sign())
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

    private fun generateSecret(algorithm: String, chars: CharArray, salt: ByteArray): SecretKey {
        val factory = SecretKeyFactory.getInstance(algorithm)
        val spec = PBEKeySpec(chars, salt, iterations, bits)
        return factory.generateSecret(spec)
    }

    private fun Cipher.encrypt(key: PublicKey, decrypted: ByteArray): ByteArray {
        init(Cipher.ENCRYPT_MODE, key)
        return doFinal(decrypted)
    }

    private fun Cipher.encrypt(key: SecretKey, params: AlgorithmParameterSpec, decrypted: ByteArray): ByteArray {
        init(Cipher.ENCRYPT_MODE, key, params)
        return doFinal(decrypted)
    }

    private fun ByteArray.base64(flags: Int = Base64.DEFAULT): String {
        return Base64.encodeToString(this, flags)
    }

    private fun Cipher.decrypt(key: SecretKey, params: AlgorithmParameterSpec, decrypted: ByteArray): ByteArray {
        init(Cipher.DECRYPT_MODE, key, params)
        return doFinal(decrypted)
    }

    private fun Cipher.decrypt(key: PrivateKey, decrypted: ByteArray): ByteArray {
        init(Cipher.DECRYPT_MODE, key)
        return doFinal(decrypted)
    }

    fun unlockFile(parent: File, pin: String) {
        println("unlock: $pin")
        viewModelScope.launch {
            _exists.value = null
            val value: Broadcast = withContext(Dispatchers.IO) {
                val sym1 = JSONObject(parent.resolve("sym1.json").readText()).let { json ->
                    KeyMeta(
                        salt = json.getString("salt").let { Base64.decode(it, Base64.DEFAULT) },
                        iv = json.getString("iv").let { Base64.decode(it, Base64.DEFAULT) },
                    )
                }
                val (public, private) = Cipher.getInstance(algorithm).let { cipher ->
                    val (public, encrypted) = JSONObject(parent.resolve("asym.json").readText()).let { json ->
                        json.getString("public").let {
                            Base64.decode(it, Base64.DEFAULT)
                        } to json.getString("private").let {
                            Base64.decode(it, Base64.DEFAULT)
                        }
                    }
                    val key = generateSecret(algorithm = cipher.algorithm, chars = pin.toCharArray(), salt = sym1.salt)
                    val params = IvParameterSpec(sym1.iv)
                    val decrypted = cipher.decrypt(key, params, encrypted)
                    KeyFactory.getInstance("RSA").let { factory ->
                        factory.generatePublic(
                            X509EncodedKeySpec(public)
                        ) to factory.generatePrivate(
                            PKCS8EncodedKeySpec(decrypted)
                        )
                    }
                }
                val sym2 = JSONObject(parent.resolve("sym2.json").readText()).let { json ->
                    KeyMeta(
                        salt = json.getString("salt").let { Base64.decode(it, Base64.DEFAULT) },
                        iv = json.getString("iv").let { Base64.decode(it, Base64.DEFAULT) },
                    )
                }
                val password = Cipher.getInstance("RSA/ECB/PKCS1Padding").let { cipher ->
                    val encrypted = parent.resolve("sym2.enc").readBytes()
                    cipher.decrypt(private, encrypted)
                }
                val decrypted = Cipher.getInstance(algorithm).let { cipher ->
                    val chars = password.base64().toCharArray()
                    val key = generateSecret(algorithm = cipher.algorithm, chars = chars, salt = sym2.salt)
                    val params = IvParameterSpec(sym2.iv)
                    val encrypted = parent.resolve("db.json.enc").readBytes()
                    cipher.decrypt(key, params, encrypted)
                }
//                Signature.getInstance("SHA256WithDSA").also { signature ->
                Signature.getInstance("SHA256WITHRSA").also { signature ->
                    signature.initVerify(public)
                    signature.update(decrypted)
                    signature.verify(parent.resolve("db.json.sig").readBytes())
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
