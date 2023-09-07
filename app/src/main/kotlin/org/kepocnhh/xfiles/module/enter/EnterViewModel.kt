package org.kepocnhh.xfiles.module.enter

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.kepocnhh.xfiles.entity.KeyMeta
import org.kepocnhh.xfiles.entity.SecurityService
import org.kepocnhh.xfiles.entity.SecurityServices
import org.kepocnhh.xfiles.entity.SecuritySettings
import org.kepocnhh.xfiles.module.app.Injection
import org.kepocnhh.xfiles.provider.readBytes
import org.kepocnhh.xfiles.provider.readText
import org.kepocnhh.xfiles.util.base64
import org.kepocnhh.xfiles.util.lifecycle.AbstractViewModel
import org.kepocnhh.xfiles.util.security.SecurityUtil
import org.kepocnhh.xfiles.util.security.getServiceOrNull
import org.kepocnhh.xfiles.util.security.requireService
import java.security.NoSuchAlgorithmException
import java.security.Provider
import java.security.interfaces.DSAParams
import java.security.interfaces.DSAPrivateKey
import java.security.spec.DSAParameterSpec
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec

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

    private fun Provider.Service.toSecurityService(): SecurityService {
        return SecurityService(
            provider = provider.name,
            algorithm = algorithm,
        )
    }

    fun requestFile() {
        injection.launch {
            val result = withContext(injection.contexts.default) {
                runCatching {
                    if (injection.local.services == null) {
                        val provider = SecurityUtil.requireProvider("BC")
                        val ciphers = setOf(
                            "PBEWITHHMACSHA256ANDAES_256",
                            "PBEWITHSHA256AND256BITAES-CBC-BC",
                        )
                        val cipher = ciphers.firstNotNullOfOrNull {
                            provider.getServiceOrNull(
                                type = "Cipher",
                                algorithm = it,
                            )
                        }?.toSecurityService() ?: throw NoSuchAlgorithmException("No such algorithms ${provider.name}:Cipher:$ciphers!")
                        val platform = SecurityUtil.requireProvider("AndroidOpenSSL")
                        injection.local.services = SecurityServices(
                            cipher = cipher,
                            symmetric = provider.requireService(type = "SecretKeyFactory", algorithm = cipher.algorithm).toSecurityService(),
                            asymmetric = provider.requireService(type = "KeyPairGenerator", algorithm = "DSA").toSecurityService(),
                            signature = provider.requireService(type = "Signature", algorithm = "SHA256WithDSA").toSecurityService(),
                            hash = platform.requireService(type = "MessageDigest", algorithm = "SHA-512").toSecurityService(),
                            random = platform.requireService(type = "SecureRandom", algorithm = "SHA1PRNG").toSecurityService(),
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
                        injection.files.exists(injection.pathNames.dataBase)
                    }
                },
            )
        }
    }

    private fun KeyMeta.toJson(): JSONObject {
        return JSONObject()
            .put("salt", salt.base64())
            .put("ivDB", ivDB.base64())
            .put("ivPrivate", ivPrivate.base64())
    }

    private fun check(params: DSAParams) {
        val L = params.p.bitLength()
        val N = params.q.bitLength()
        when (L) {
            1024 -> check(N == 160)
            2048 -> check(N == 224 || N == 256)
            3072 -> check(N == 256)
            else -> error("L is not 1024, 2048 or 3072!")
        }
    }

    private fun create(pin: String, securitySettings: SecuritySettings): SecretKey {
        val services = injection.local.services ?: TODO()
        val md = injection.security(services).getMessageDigest()
        val hash = md.digest(pin.toByteArray()).base64()
        val random = injection.security(services).getSecureRandom()
        val aesKeyLength = SecurityUtil.getValue(securitySettings.aesKeyLength)
        val blockSize = SecurityUtil.getBlockSize(securitySettings.aesKeyLength)
        val pbeIterations = SecurityUtil.getValue(securitySettings.pbeIterations)
        val meta = KeyMeta(
            salt = ByteArray(aesKeyLength / 8).also(random::nextBytes),
            ivDB = ByteArray(blockSize).also(random::nextBytes),
            ivPrivate = ByteArray(blockSize).also(random::nextBytes),
        )
        injection.files.writeBytes(injection.pathNames.symmetric, meta.toJson().toString().toByteArray())
        val pair = injection.security(services).getKeyPairGenerator().let { generator ->
            val params = injection.security(services).getAlgorithmParameterGenerator()
                .generate(
                    size = SecurityUtil.getValue(securitySettings.dsaKeyLength),
                    random = random,
                )
            generator.generate(params.getParameterSpec(DSAParameterSpec::class.java)).also {
                val private = it.private
                check(private is DSAPrivateKey)
                check(private.params)
            }
        }
        val decrypted = "{}".toByteArray()
        val cipher = injection.security(services).getCipher()
        val key = injection.security(services)
            .getSecretKeyFactory()
            .generate(PBEKeySpec(hash.toCharArray(), meta.salt, pbeIterations, aesKeyLength))
        injection.files.writeBytes(injection.pathNames.dataBase, cipher.encrypt(key, IvParameterSpec(meta.ivDB), decrypted))
        val private = cipher.encrypt(key, IvParameterSpec(meta.ivPrivate), pair.private.encoded)
        JSONObject()
            .put("public", pair.public.encoded.base64())
            .put("private", private.base64())
            .also { json ->
                injection.files.writeBytes(injection.pathNames.asymmetric, json.toString().toByteArray())
            }
        val sign = injection.security(services)
            .getSignature()
            .sign(pair.private, random, decrypted = decrypted)
        injection.files.writeBytes(injection.pathNames.dataBaseSignature, sign)
        return key
    }

    fun createNewFile(pin: String) {
        injection.launch {
            _exists.value = null
            val key = withContext(injection.contexts.default) {
                create(pin, injection.local.securitySettings)
            }
            _broadcast.emit(Broadcast.OnUnlock(key))
        }
    }

    fun deleteFile() {
        injection.launch {
            _exists.value = null
            withContext(injection.contexts.default) {
                injection.files.delete(injection.pathNames.dataBase)
            }
            _exists.value = false
        }
    }

    private fun JSONObject.toKeyMeta(): KeyMeta {
        return KeyMeta(
            salt = getString("salt").base64(),
            ivDB = getString("ivDB").base64(),
            ivPrivate = getString("ivPrivate").base64(),
        )
    }

    private fun unlock(pin: String, securitySettings: SecuritySettings): SecretKey {
        val services = injection.local.services ?: TODO()
        val md = injection.security(services).getMessageDigest()
        val hash = md.digest(pin.toByteArray()).base64()
        val aesKeyLength = SecurityUtil.getValue(securitySettings.aesKeyLength)
        val pbeIterations = SecurityUtil.getValue(securitySettings.pbeIterations)
        val meta = JSONObject(injection.files.readText(injection.pathNames.symmetric)).toKeyMeta()
        val cipher = injection.security(services).getCipher()
        val key = injection.security(services)
            .getSecretKeyFactory()
            .generate(PBEKeySpec(hash.toCharArray(), meta.salt, pbeIterations, aesKeyLength))
        val pair = JSONObject(injection.files.readText(injection.pathNames.asymmetric)).let { json ->
            injection.security(services).getKeyFactory().generate(
                public = json.getString("public").base64(),
                private = cipher.decrypt(
                    key = key,
                    params = IvParameterSpec(meta.ivPrivate),
                    encrypted = json.getString("private").base64(),
                ),
            )
        }
        val decrypted = cipher.decrypt(
            key = key,
            params = IvParameterSpec(meta.ivDB),
            encrypted = injection.files.readBytes(injection.pathNames.dataBase),
        )
        val verified = injection.security(services)
            .getSignature()
            .verify(
                key = pair.public,
                decrypted = decrypted,
                sig = injection.files.readBytes(injection.pathNames.dataBaseSignature),
            )
        check(verified)
        return key
    }

    fun unlockFile(pin: String) {
        injection.launch {
            _exists.value = null
            val result = withContext(injection.contexts.default) {
                runCatching {
                    unlock(pin = pin, injection.local.securitySettings)
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
