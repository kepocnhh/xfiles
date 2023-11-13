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
import org.kepocnhh.xfiles.util.security.toSecurityService
import java.security.NoSuchAlgorithmException
import java.security.Provider
import java.security.interfaces.DSAParams
import java.security.interfaces.DSAPrivateKey
import java.security.spec.DSAParameterSpec
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec

internal class EnterViewModel(private val injection: Injection) : AbstractViewModel() {
    sealed interface Broadcast {
        class OnUnlock(val key: SecretKey) : Broadcast
        object OnUnlockError : Broadcast
        object OnSecurityError : Broadcast
        class OnBiometric(val iv: ByteArray) : Broadcast
    }

    data class State(
        val loading: Boolean = false,
        val exists: Boolean,
        val hasBiometric: Boolean,
    )

    private val logger = injection.loggers.newLogger("[Enter|VM]")
    private val _broadcast = MutableSharedFlow<Broadcast>()
    val broadcast = _broadcast.asSharedFlow()

    private val _state = MutableStateFlow<State?>(null)
    val state = _state.asStateFlow()

    fun requestState() {
        injection.launch {
            _state.value = withContext(injection.contexts.default) {
                State(
                    exists = injection.files.exists(injection.pathNames.dataBase),
                    hasBiometric = injection.local.securitySettings.hasBiometric,
                )
            }
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

    private fun create(password: String, securitySettings: SecuritySettings): SecretKey {
        val services = injection.local.services ?: TODO()
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
            .generate(PBEKeySpec(password.toCharArray(), meta.salt, pbeIterations, aesKeyLength))
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

    fun createNewFile(pin: String, cipher: Cipher?) {
        injection.launch {
            _state.value = state.value!!.copy(loading = true)
            val key = withContext(injection.contexts.default) {
                val password = getPassword(pin = pin)
                if (cipher != null) {
                    val encrypted = cipher.doFinal(password.toByteArray())
                    val biometric = JSONObject()
                        .put("password", encrypted.base64())
                        .put("iv", cipher.iv.base64())
                        .toString()
                        .toByteArray()
                    injection.files.writeBytes(injection.pathNames.biometric, biometric)
                }
                create(password = password, securitySettings = injection.local.securitySettings)
            }
            _broadcast.emit(Broadcast.OnUnlock(key))
        }
    }

    fun deleteFile() {
        injection.launch {
            _state.value = state.value!!.copy(loading = true)
            withContext(injection.contexts.default) {
                injection.files.delete(injection.pathNames.dataBase)
            }
            _state.value = withContext(injection.contexts.default) {
                State(exists = false, hasBiometric = injection.local.securitySettings.hasBiometric)
            }
        }
    }

    private fun JSONObject.toKeyMeta(): KeyMeta {
        return KeyMeta(
            salt = getString("salt").base64(),
            ivDB = getString("ivDB").base64(),
            ivPrivate = getString("ivPrivate").base64(),
        )
    }

    private fun getPassword(pin: String): String {
        val services = injection.local.services ?: TODO()
        val md = injection.security(services).getMessageDigest()
        return md.digest(pin.toByteArray()).base64()
    }

    private fun unlock(password: String, securitySettings: SecuritySettings): SecretKey {
        val services = injection.local.services ?: TODO()
        val aesKeyLength = SecurityUtil.getValue(securitySettings.aesKeyLength)
        val pbeIterations = SecurityUtil.getValue(securitySettings.pbeIterations)
        val meta = JSONObject(injection.files.readText(injection.pathNames.symmetric)).toKeyMeta()
        val cipher = injection.security(services).getCipher()
        val key = injection.security(services)
            .getSecretKeyFactory()
            .generate(PBEKeySpec(password.toCharArray(), meta.salt, pbeIterations, aesKeyLength))
        val decrypted = cipher.decrypt(
            key = key,
            params = IvParameterSpec(meta.ivDB),
            encrypted = injection.files.readBytes(injection.pathNames.dataBase),
        )
        val verified = injection.security(services)
            .getSignature()
            .verify(
                key = injection.security(services)
                    .getKeyFactory()
                    .generatePublic(
                        bytes = JSONObject(injection.files.readText(injection.pathNames.asymmetric))
                            .getString("public")
                            .base64(),
                    ),
                decrypted = decrypted,
                sig = injection.files.readBytes(injection.pathNames.dataBaseSignature),
            )
        check(verified)
        return key
    }

    fun unlockFile(pin: String) {
        injection.launch {
            _state.value = state.value!!.copy(loading = true)
            val result = withContext(injection.contexts.default) {
                runCatching {
                    val securitySettings = injection.local.securitySettings
                    val password = getPassword(pin = pin)
                    unlock(password = password, securitySettings = securitySettings)
                }
            }
            result.fold(
                onFailure = {
                    _state.value = withContext(injection.contexts.default) {
                        State(exists = true, hasBiometric = injection.local.securitySettings.hasBiometric)
                    }
                    _broadcast.emit(Broadcast.OnUnlockError)
                },
                onSuccess = {
                    _broadcast.emit(Broadcast.OnUnlock(it))
                },
            )
        }
    }

    fun unlockFile(cipher: Cipher) {
        injection.launch {
            _state.value = state.value!!.copy(loading = true)
            val key = withContext(injection.contexts.default) {
                val securitySettings = injection.local.securitySettings
                val password = injection.files.readText(injection.pathNames.biometric)
                    .let(::JSONObject)
                    .getString("password")
                    .base64()
                    .let(cipher::doFinal)
                    .let(::String)
                unlock(password = password, securitySettings = securitySettings)
            }
            _broadcast.emit(Broadcast.OnUnlock(key))
        }
    }

    fun requestBiometric() {
        injection.launch {
            _state.value = state.value!!.copy(loading = true)
            val iv = withContext(injection.contexts.default) {
                injection.files.readText(injection.pathNames.biometric)
                    .let(::JSONObject)
                    .getString("iv")
                    .base64()
            }
            _broadcast.emit(Broadcast.OnBiometric(iv))
        }
    }
}
