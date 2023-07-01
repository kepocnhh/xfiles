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
import java.security.KeyPairGenerator
import java.security.SecureRandom
import java.security.Security
import java.security.spec.AlgorithmParameterSpec
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
                // https://github.com/doridori/Android-Security-Reference/blob/master/providers/platform_security_providers.md
//                error(Security.getProviders().joinToString { it.name }) // AndroidNSSP, AndroidOpenSSL, CertPathProvider, AndroidKeyStoreBCWorkaround, BC, HarmonyJSSE, AndroidKeyStore
//                error(Security.getAlgorithms("SecretKeyFactory")) // [PBEWITHHMACSHA384ANDAES_128, PBEWITHSHAAND128BITRC2-CBC, PBEWITHMD5AND128BITAES-CBC-OPENSSL, PBEWITHSHA256AND192BITAES-CBC-BC, HMACSHA256, PBEWITHHMACSHA512ANDAES_128, PBEWITHSHA256AND128BITAES-CBC-BC, PBEWITHHMACSHA224ANDAES_256, PBKDF2WITHHMACSHA384, HMACSHA512, PBEWITHSHAANDTWOFISH-CBC, PBEWITHMD5ANDDES, PBEWITHSHAAND40BITRC2-CBC, PBEWITHSHA1ANDDES, PBEWITHHMACSHA256ANDAES_256, PBEWITHSHA1ANDRC2, PBKDF2WITHHMACSHA224, PBEWITHHMACSHA1ANDAES_128, HMACSHA1, PBKDF2WITHHMACSHA1AND8BIT, PBEWITHMD5AND192BITAES-CBC-OPENSSL, PBEWITHHMACSHA512ANDAES_256, PBEWITHSHAAND128BITRC4, PBEWITHSHAAND3-KEYTRIPLEDES-CBC, PBEWITHSHAAND128BITAES-CBC-BC, DESEDE, HMACSHA384, PBEWITHSHAAND256BITAES-CBC-BC, HMACSHA224, PBKDF2WITHHMACSHA1, PBEWITHMD5AND256BITAES-CBC-OPENSSL, PBEWITHSHAAND40BITRC4, AES, PBEWITHSHAAND2-KEYTRIPLEDES-CBC, PBEWITHHMACSHA1, DES, PBEWITHHMACSHA256ANDAES_128, PBEWITHSHA256AND256BITAES-CBC-BC, PBEWITHMD5ANDRC2, PBKDF2WITHHMACSHA512, PBEWITHHMACSHA384ANDAES_256, PBEWITHSHAAND192BITAES-CBC-BC, PBEWITHHMACSHA1ANDAES_256, PBEWITHHMACSHA224ANDAES_128, PBKDF2WITHHMACSHA256]
                // AES
                // DES
                // DESEDE
                // HMACSHA1
                // HMACSHA224
                // HMACSHA256
                // HMACSHA384
                // HMACSHA512
                // PBEWITHHMACSHA1
                // PBEWITHHMACSHA1ANDAES_128
                // PBEWITHHMACSHA1ANDAES_256
                // PBEWITHHMACSHA224ANDAES_128
                // PBEWITHHMACSHA224ANDAES_256
                // PBEWITHHMACSHA256ANDAES_128
                // PBEWITHHMACSHA256ANDAES_256
                // PBEWITHHMACSHA384ANDAES_128
                // PBEWITHHMACSHA384ANDAES_256
                // PBEWITHHMACSHA512ANDAES_128
                // PBEWITHHMACSHA512ANDAES_256
                // PBEWITHMD5AND128BITAES-CBC-OPENSSL
                // PBEWITHMD5AND192BITAES-CBC-OPENSSL
                // PBEWITHMD5AND256BITAES-CBC-OPENSSL
                // PBEWITHMD5ANDDES
                // PBEWITHMD5ANDRC2
                // PBEWITHSHA1ANDDES
                // PBEWITHSHA1ANDRC2
                // PBEWITHSHA256AND128BITAES-CBC-BC
                // PBEWITHSHA256AND192BITAES-CBC-BC
                // PBEWITHSHA256AND256BITAES-CBC-BC
                // PBEWITHSHAAND128BITAES-CBC-BC
                // PBEWITHSHAAND128BITRC2-CBC
                // PBEWITHSHAAND128BITRC4
                // PBEWITHSHAAND192BITAES-CBC-BC
                // PBEWITHSHAAND2-KEYTRIPLEDES-CBC
                // PBEWITHSHAAND256BITAES-CBC-BC
                // PBEWITHSHAAND3-KEYTRIPLEDES-CBC
                // PBEWITHSHAAND40BITRC2-CBC
                // PBEWITHSHAAND40BITRC4
                // PBEWITHSHAANDTWOFISH-CBC
                // PBKDF2WITHHMACSHA1
                // PBKDF2WITHHMACSHA1AND8BIT
                // PBKDF2WITHHMACSHA224
                // PBKDF2WITHHMACSHA256
                // PBKDF2WITHHMACSHA384
                // PBKDF2WITHHMACSHA512
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
            val value = withContext(Dispatchers.IO) {
                val cipher = Cipher.getInstance(algorithm)
                val factory = SecretKeyFactory.getInstance(algorithm)
                delay(2_000)
                if (pin == "3454") {
                    Broadcast.OnUnlock
                } else {
                    Broadcast.OnUnlockError
                }
            }
            if (value == Broadcast.OnUnlockError) {
                _exists.value = true
            }
            _broadcast.emit(value)
        }
    }
}
