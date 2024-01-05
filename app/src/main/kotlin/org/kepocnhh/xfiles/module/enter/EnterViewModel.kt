package org.kepocnhh.xfiles.module.enter

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.kepocnhh.xfiles.entity.AsymmetricKey
import org.kepocnhh.xfiles.entity.BiometricMeta
import org.kepocnhh.xfiles.entity.DataBase
import org.kepocnhh.xfiles.entity.KeyMeta
import org.kepocnhh.xfiles.entity.SecuritySettings
import org.kepocnhh.xfiles.module.app.Injection
import org.kepocnhh.xfiles.provider.Decrypt
import org.kepocnhh.xfiles.provider.Encrypt
import org.kepocnhh.xfiles.provider.data.requireAppId
import org.kepocnhh.xfiles.provider.data.requireDatabaseId
import org.kepocnhh.xfiles.provider.data.requireDevice
import org.kepocnhh.xfiles.provider.data.requireServices
import org.kepocnhh.xfiles.provider.security.HashAlgorithm
import org.kepocnhh.xfiles.util.lifecycle.AbstractViewModel
import org.kepocnhh.xfiles.util.security.SecurityUtil
import java.security.PrivateKey
import java.security.interfaces.DSAParams
import java.security.interfaces.DSAPrivateKey
import java.security.spec.DSAParameterSpec
import java.util.UUID
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec

private fun check(key: PrivateKey) {
    check(key is DSAPrivateKey) { "The private key is not DSA!" }
    check(key.params)
}

@Suppress("VariableNaming")
private fun check(params: DSAParams) {
    val L = params.p.bitLength()
    val N = params.q.bitLength()
    when (L) {
        1024 -> check(N == 160)
        2048 -> check(N == 224 || N == 256) { "N($N) is not 224 or 256!" }
        3072 -> check(N == 256)
        else -> error("L($L) is not 1024, 2048 or 3072!")
    }
}

internal class EnterViewModel(private val injection: Injection) : AbstractViewModel() {
    sealed interface Broadcast {
        class OnUnlock(val key: SecretKey) : Broadcast
        data class OnUnlockError(val error: Throwable) : Broadcast
        class OnBiometric(val iv: ByteArray) : Broadcast
    }

    data class State(
        val loading: Boolean,
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
                    loading = false,
                    exists = injection.encrypted.files.exists(injection.pathNames.dataBase),
                    hasBiometric = injection.local.securitySettings.hasBiometric,
                )
            }
        }
    }

    private fun requireState(): State {
        return state.value ?: error("No state!")
    }

    private fun create(password: String, securitySettings: SecuritySettings): SecretKey {
        val services = injection.local.requireServices()
        val random = injection.security(services).getSecureRandom()
        val aesKeyLength = SecurityUtil.getValue(securitySettings.aesKeyLength)
        val blockSize = SecurityUtil.getBlockSize(securitySettings.aesKeyLength)
        val pbeIterations = SecurityUtil.getValue(securitySettings.pbeIterations)
        val meta = KeyMeta(
            salt = ByteArray(aesKeyLength / 8).also(random::nextBytes),
            ivDB = ByteArray(blockSize).also(random::nextBytes),
            ivPrivate = ByteArray(blockSize).also(random::nextBytes),
        )
        injection.encrypted.files.writeBytes(
            pathname = injection.pathNames.symmetric,
            bytes = injection.serializer.serialize(meta),
        )
        val pair = injection.security(services).getKeyPairGenerator().let { generator ->
            val params = injection.security(services).getAlgorithmParameterGenerator()
                .generate(
                    size = SecurityUtil.getValue(securitySettings.dsaKeyLength),
                    random = random,
                )
            generator.generate(params.getParameterSpec(DSAParameterSpec::class.java))
        }
        check(pair.private)
        val dataBase = DataBase(
            id = injection.encrypted.local.requireDatabaseId(),
            updated = injection.time.now(),
            secrets = emptyMap(),
        )
        val decrypted = injection.serializer.serialize(dataBase)
        val cipher = injection.security(services).getCipher()
        val secretKey = injection.security(services)
            .getSecretKeyFactory()
            .generate(PBEKeySpec(password.toCharArray(), meta.salt, pbeIterations, aesKeyLength))
        injection.encrypted.files.writeBytes(
            pathname = injection.pathNames.dataBase,
            bytes = cipher.encrypt(secretKey, IvParameterSpec(meta.ivDB), decrypted),
        )
        val privateEncrypted = cipher.encrypt(secretKey, IvParameterSpec(meta.ivPrivate), pair.private.encoded)
        val asymmetricKey = AsymmetricKey(
            publicKeyDecrypted = pair.public.encoded,
            privateKeyEncrypted = privateEncrypted,
        )
        injection.encrypted.files.writeBytes(
            pathname = injection.pathNames.asymmetric,
            bytes = injection.serializer.serialize(asymmetricKey),
        )
        val sign = injection.security(services)
            .getSignature()
            .sign(key = pair.private, random = random, decrypted = decrypted)
        injection.encrypted.files.writeBytes(injection.pathNames.dataBaseSignature, sign)
        return secretKey
    }

    fun createNewFile(pin: String, encrypt: Encrypt?) {
        logger.debug("create...")
        injection.launch {
            _state.value = requireState().copy(loading = true)
            val key = withContext(injection.contexts.default) {
                val services = injection.local.requireServices()
                val security = injection.security(services)
                val databaseId = security.uuids().generate()
                logger.debug("databaseId: $databaseId")
                injection.encrypted.local.databaseId = databaseId
                val password = getPassword(pin = pin)
                if (encrypt != null) {
                    logger.debug("cipher exists")
                    val data = encrypt.doFinal(password.toByteArray())
                    val biometric = BiometricMeta(
                        passwordEncrypted = data.encrypted,
                        iv = data.iv,
                    )
                    injection
                        .encrypted
                        .files
                        .writeBytes(injection.pathNames.biometric, injection.serializer.serialize(biometric))
                }
                create(password = password, securitySettings = injection.local.securitySettings)
            }
            _broadcast.emit(Broadcast.OnUnlock(key))
        }
    }

    fun deleteFile() {
        injection.launch {
            _state.value = requireState().copy(loading = true)
            withContext(injection.contexts.default) {
                injection.encrypted.local.databaseId = null
                setOf(
                    injection.pathNames.symmetric,
                    injection.pathNames.asymmetric,
                    injection.pathNames.dataBase,
                    injection.pathNames.dataBaseSignature,
                    injection.pathNames.biometric,
                ).forEach { pathName ->
                    injection.encrypted.files.delete(pathName)
                }
            }
            _state.value = withContext(injection.contexts.default) {
                State(
                    loading = false,
                    exists = false,
                    hasBiometric = injection.local.securitySettings.hasBiometric,
                )
            }
        }
    }

    private fun getPassword(pin: String): String {
        val services = injection.local.requireServices()
        val security = injection.security(services)
        val sha512 = security.getMessageDigest(HashAlgorithm.SHA512)
        val device = injection.local.requireDevice()
        val bytes = getBytes(
            pin = pin,
            deviceId = injection.devices.toUUID(device),
            appId = injection.encrypted.local.requireAppId(),
            databaseId = injection.encrypted.local.requireDatabaseId(),
        )
        return security.base64().encode(sha512.digest(bytes))
    }

    private fun unlock(password: String, securitySettings: SecuritySettings): SecretKey {
        val services = injection.local.requireServices()
        val aesKeyLength = SecurityUtil.getValue(securitySettings.aesKeyLength)
        val pbeIterations = SecurityUtil.getValue(securitySettings.pbeIterations)
        val meta = injection.encrypted.files.readBytes(injection.pathNames.symmetric)
            .let(injection.serializer::toKeyMeta)
        logger.debug("salt: " + meta.salt.contentToString())
        val cipher = injection.security(services).getCipher()
        val key = injection.security(services)
            .getSecretKeyFactory()
            .generate(PBEKeySpec(password.toCharArray(), meta.salt, pbeIterations, aesKeyLength))
        val decrypted = cipher.decrypt(
            key = key,
            params = IvParameterSpec(meta.ivDB),
            encrypted = injection.encrypted.files.readBytes(injection.pathNames.dataBase),
        )
        val verified = injection.security(services)
            .getSignature()
            .verify(
                key = injection.security(services)
                    .getKeyFactory()
                    .generatePublic(
                        bytes = injection.encrypted.files.readBytes(injection.pathNames.asymmetric)
                            .let(injection.serializer::toAsymmetricKey)
                            .publicKeyDecrypted,
                    ),
                decrypted = decrypted,
                sig = injection.encrypted.files.readBytes(injection.pathNames.dataBaseSignature),
            )
        check(verified)
        val dataBase = injection.serializer.toDataBase(decrypted)
        check(injection.encrypted.local.requireDatabaseId() == dataBase.id) {
            "Data base id is not expected!"
        }
        logger.debug("unlocked: ${dataBase.id}")
        return key
    }

    fun unlockFile(pin: String) {
        logger.debug("unlock...")
        injection.launch {
            _state.value = requireState().copy(loading = true)
            val result = withContext(injection.contexts.default) {
                runCatching {
                    val securitySettings = injection.local.securitySettings
                    val password = getPassword(pin = pin)
                    unlock(password = password, securitySettings = securitySettings)
                }
            }
            result.fold(
                onFailure = { error ->
                    logger.warning("unlock error: $error")
                    _state.value = withContext(injection.contexts.default) {
                        State(
                            loading = false,
                            exists = true,
                            hasBiometric = injection.local.securitySettings.hasBiometric,
                        )
                    }
                    _broadcast.emit(Broadcast.OnUnlockError(error))
                },
                onSuccess = {
                    _broadcast.emit(Broadcast.OnUnlock(it))
                },
            )
        }
    }

    fun unlockFile(decrypt: Decrypt) {
        logger.debug("unlock with cipher...")
        injection.launch {
            _state.value = requireState().copy(loading = true)
            val key = withContext(injection.contexts.default) {
                val securitySettings = injection.local.securitySettings
                val password = injection
                    .encrypted
                    .files
                    .readBytes(injection.pathNames.biometric)
                    .let(injection.serializer::toBiometricMeta)
                    .passwordEncrypted
                    .let(decrypt::doFinal)
                    .let(::String)
                unlock(password = password, securitySettings = securitySettings)
            }
            _broadcast.emit(Broadcast.OnUnlock(key))
        }
    }

    fun requestBiometric() {
        injection.launch {
            _state.value = requireState().copy(loading = true)
            val meta = withContext(injection.contexts.default) {
                injection
                    .encrypted
                    .files
                    .readBytes(injection.pathNames.biometric)
                    .let(injection.serializer::toBiometricMeta)
            }
            _broadcast.emit(Broadcast.OnBiometric(iv = meta.iv))
        }
    }

    companion object {
        internal fun getBytes(
            pin: String,
            deviceId: UUID,
            appId: UUID,
            databaseId: UUID,
        ): ByteArray {
            check(pin.isNotEmpty()) { "PIN is empty!" }
            val version = 1
            return listOf(
                "version:$version",
                pin,
                deviceId.toString(),
                appId.toString(),
                databaseId.toString(),
            ).joinToString(separator = "-").toByteArray()
        }
    }
}
