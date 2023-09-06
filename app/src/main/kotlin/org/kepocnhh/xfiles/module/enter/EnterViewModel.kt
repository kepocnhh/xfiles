package org.kepocnhh.xfiles.module.enter

import android.util.Base64
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.kepocnhh.xfiles.entity.KeyMeta
import org.kepocnhh.xfiles.entity.SecurityService
import org.kepocnhh.xfiles.entity.SecurityServices
import org.kepocnhh.xfiles.module.app.Injection
import org.kepocnhh.xfiles.provider.readBytes
import org.kepocnhh.xfiles.provider.readText
import org.kepocnhh.xfiles.util.base64
import org.kepocnhh.xfiles.util.lifecycle.AbstractViewModel
import org.kepocnhh.xfiles.util.security.generateKeyPair
import java.security.KeyFactory
import java.security.NoSuchAlgorithmException
import java.security.NoSuchProviderException
import java.security.Provider
import java.security.Security
import java.security.interfaces.DSAPrivateKey
import java.security.spec.DSAParameterSpec
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import kotlin.math.pow
import kotlin.time.Duration.Companion.milliseconds

internal class EnterViewModel(private val injection: Injection) : AbstractViewModel() {
    sealed interface Broadcast {
        class OnUnlock(val key: SecretKey) : Broadcast
        object OnUnlockError : Broadcast
        object OnSecurityError : Broadcast
    }

    private val logger = injection.loggers.newLogger("[Enter|VM]")
    private val _broadcast = MutableSharedFlow<Broadcast>()
    val broadcast = _broadcast.asSharedFlow()

    private val _exists = MutableStateFlow<Boolean?>(null)
    val exists = _exists.asStateFlow()

    private fun getProvider(name: String): Provider {
        return Security.getProviders().firstOrNull { it.name == name } ?: throw NoSuchProviderException("No such provider \"$name\"!")
    }

    private fun Provider.getAlgorithm(type: String, algorithm: String): Provider.Service {
        return services.firstOrNull {
            it.type.equals(type, ignoreCase = true) && it.algorithm.equals(algorithm, ignoreCase = true)
        } ?: throw NoSuchAlgorithmException("No such algorithm $name:$type:$algorithm!")
    }

    private fun Provider.getAlgorithm(type: String, algorithms: Set<String>): Provider.Service {
        return services.firstOrNull {
            it.type == type && algorithms.any { algorithm -> it.algorithm.equals(algorithm, ignoreCase = true) }
        } ?: throw NoSuchAlgorithmException("No such algorithm $name:$type:$algorithms!")
    }

    private fun Provider.getSecurityService(type: String, algorithms: Set<String>): SecurityService {
        return SecurityService(
            provider = name,
            algorithm = getAlgorithm(
                type = type,
                algorithms = algorithms,
            ).algorithm,
        )
    }

    private fun Provider.getSecurityService(type: String, algorithm: String): SecurityService {
        return SecurityService(
            provider = name,
            algorithm = getAlgorithm(
                type = type,
                algorithm = algorithm,
            ).algorithm,
        )
    }

    fun requestFile() {
        injection.launch {
            val result = withContext(injection.contexts.default) {
                runCatching {
                    if (injection.local.services == null) {
                        val provider = getProvider("BC")
                        val cipher = provider.getSecurityService(
                            type = "Cipher",
                            algorithms = setOf(
                                "PBEWITHHMACSHA256ANDAES_256",
                                "PBEWITHSHA256AND256BITAES-CBC-BC",
                            ),
                        )
                        val platform = getProvider("AndroidOpenSSL")
                        injection.local.services = SecurityServices(
                            cipher = cipher,
                            symmetric = provider.getSecurityService(type = "SecretKeyFactory", algorithm = cipher.algorithm),
                            asymmetric = provider.getSecurityService(type = "KeyPairGenerator", algorithm = "DSA"),
                            signature = provider.getSecurityService(type = "Signature", algorithm = "SHA256WithDSA"),
                            hash = platform.getSecurityService(type = "MessageDigest", algorithm = "SHA-512"),
                            random = platform.getSecurityService(type = "SecureRandom", algorithm = "SHA1PRNG"),
                        )
                        logger.debug("services: " + injection.local.services)
                    }
                }
            }
            result.fold(
                onFailure = {
                    logger.warning("Check security services error: $it")
                    _broadcast.emit(Broadcast.OnSecurityError)
                },
                onSuccess = {
                    _exists.value = withContext(injection.contexts.default) {
                        injection.files.exists("db.json.enc")
                    }
                },
            )
        }
    }

    private fun KeyMeta.toJson(): JSONObject {
        return JSONObject()
            .put("salt", salt.base64())
            .put("iv", iv.base64())
            .put("iterations", iterations)
            .put("bits", bits)
    }

    private fun create(pin: String): SecretKey {
        val startTime = System.currentTimeMillis().milliseconds // todo
        val services = injection.local.services ?: TODO()
        val md = injection.security(services).getMessageDigest()
        val hash = md.digest(pin.toByteArray()).base64()
        val random = injection.security(services).getSecureRandom()
//        val bits = 8 * 8 // 64
//        val bits = 8 * 16 // 128
        val bits = 8 * 32 // 256 bits (32 octets)
        val blockSize = 16 // AES-256
        val meta = KeyMeta(
            salt = ByteArray(bits / 8).also(random::nextBytes),
            iv = ByteArray(blockSize).also(random::nextBytes),
//            iterations = 2.0.pow(10).toInt(),
            iterations = 2.0.pow(16).toInt(), // 65_536
//            iterations = 2.0.pow(17).toInt(), // 131_072
//            iterations = 2.0.pow(20).toInt(), // 1_048_576
//            iterations = 1_048_576, // 2^20
            bits = bits,
        )
        println("create meta: ${System.currentTimeMillis().milliseconds - startTime}")
        injection.files.writeBytes("sym.json", meta.toJson().toString().toByteArray())
        val pair = injection.security(services).getKeyPairGenerator().let { generator ->
//            L = 1024, N = 160
//            L = 2048, N = 224
//            L = 2048, N = 256
//            L = 3072, N = 256
//            strength must be from 512 - 4096 and a multiple of 1024 above 1024
//            val primes = 1024 * 1
            val primes = 1024 * 2
            val params = injection.security(services).getAlgorithmParameterGenerator()
                .generate(primes, random)
            generator.generate(params.getParameterSpec(DSAParameterSpec::class.java)).also {
                val private = it.private
                check(private is DSAPrivateKey)
                println(
                    """
                        prime: [${private.params.p.bitLength()}] ${private.params.p}
                        subPrime: [${private.params.q.bitLength()}] ${private.params.q}
                        base: [${private.params.g.bitLength()}] ${private.params.g}
                    """.trimIndent()
                )
                val L = private.params.p.bitLength()
                val N = private.params.q.bitLength()
                when (L) {
                    1024 -> check(N == 160)
                    2048 -> check(N == 224 || N == 256)
                    3072 -> check(N == 256)
                    else -> error("L is not 1024, 2048 or 3072!")
                }
            }
        }
        println("generate key pair: ${System.currentTimeMillis().milliseconds - startTime}")
        val decrypted = "{}".toByteArray()
        val cipher = injection.security(services).getCipher()
        val key = injection.security(services).getSecretKeyFactory().let { factory ->
            val spec = PBEKeySpec(hash.toCharArray(), meta.salt, meta.iterations, meta.bits)
            factory.generate(spec)
        }
        println("generate secret key: ${System.currentTimeMillis().milliseconds - startTime}")
        val params = IvParameterSpec(meta.iv)
        injection.files.writeBytes("db.json.enc", cipher.encrypt(key, params, decrypted))
        val private = cipher.encrypt(key, params, pair.private.encoded)
        println("encrypt: ${System.currentTimeMillis().milliseconds - startTime}")
        JSONObject()
            .put("public", pair.public.encoded.base64())
            .put("private", private.base64())
            .also { json ->
                injection.files.writeBytes("asym.json", json.toString().toByteArray())
            }
        injection.security(services).getSignature().also { signature ->
            val sign = signature.sign(pair.private, random, decrypted = decrypted)
            injection.files.writeBytes("db.json.sig", sign)
            println("signature sign: ${System.currentTimeMillis().milliseconds - startTime}")
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

    fun deleteFile() {
        injection.launch {
            _exists.value = null
            withContext(injection.contexts.default) {
                injection.files.delete("db.json.enc")
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

    private fun unlock(pin: String): SecretKey {
        val startTime = System.currentTimeMillis().milliseconds // todo
        val services = injection.local.services ?: TODO()
        val md = injection.security(services).getMessageDigest()
        val hash = md.digest(pin.toByteArray()).base64()
        val meta = JSONObject(injection.files.readText("sym.json")).toKeyMeta()
        val cipher = injection.security(services).getCipher()
        val key = injection.security(services).getSecretKeyFactory().let { factory ->
            val spec = PBEKeySpec(hash.toCharArray(), meta.salt, meta.iterations, meta.bits)
            factory.generate(spec)
        }
        println("generate secret key: ${System.currentTimeMillis().milliseconds - startTime}")
        val params = IvParameterSpec(meta.iv)
        val pair = JSONObject(injection.files.readText("asym.json")).let { json ->
            val public = json.getString("public").let { Base64.decode(it, Base64.DEFAULT) }
            val encrypted = json.getString("private").let { Base64.decode(it, Base64.DEFAULT) }
            KeyFactory.getInstance("DSA").generateKeyPair(
                public = public,
                private = cipher.decrypt(key, params, encrypted),
            )
        }
        println("generate key pair: ${System.currentTimeMillis().milliseconds - startTime}")
        val decrypted = cipher.decrypt(key, params, injection.files.readBytes("db.json.enc"))
        println("decrypt: ${System.currentTimeMillis().milliseconds - startTime}")
        injection.security(services).getSignature().also { signature ->
            val verified = signature.verify(
                key = pair.public,
                decrypted = decrypted,
                sig = injection.files.readBytes("db.json.sig"),
            )
            println("signature verify: ${System.currentTimeMillis().milliseconds - startTime}")
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
                },
            )
        }
    }
}
