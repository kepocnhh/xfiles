package org.kepocnhh.xfiles.module.checks

import android.os.Build
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.kepocnhh.xfiles.entity.SecurityServices
import org.kepocnhh.xfiles.module.app.Injection
import org.kepocnhh.xfiles.util.lifecycle.AbstractViewModel
import org.kepocnhh.xfiles.util.security.SecurityUtil
import org.kepocnhh.xfiles.util.security.getServiceOrNull
import org.kepocnhh.xfiles.util.security.requireService
import org.kepocnhh.xfiles.util.security.toSecurityService
import java.security.NoSuchAlgorithmException
import java.util.Arrays
import java.util.UUID
import kotlin.time.Duration.Companion.seconds

internal class ChecksViewModel(private val injection: Injection) : AbstractViewModel() {
    sealed interface Broadcast {
        data object OnComplete : Broadcast
    }

    sealed interface State {
        data class OnChecks(val type: ChecksType?) : State
        data class OnError(val type: ChecksType, val error: Throwable) : State
    }

    enum class ChecksType {
        SECURITY_SERVICES,
        IDS,
    }

    private val logger = injection.loggers.newLogger("[Checks|VM]")
    private val _state = MutableStateFlow<State?>(null)
    val state = _state.asStateFlow()
    private val _broadcast = MutableSharedFlow<Broadcast>()
    val broadcast = _broadcast.asSharedFlow()

    private fun getSecurityServices(): SecurityServices {
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
        return SecurityServices(
            cipher = cipher,
            symmetric = provider.requireService(type = "SecretKeyFactory", algorithm = cipher.algorithm).toSecurityService(),
            asymmetric = provider.requireService(type = "KeyPairGenerator", algorithm = "DSA").toSecurityService(),
            signature = provider.requireService(type = "Signature", algorithm = "SHA256WithDSA").toSecurityService(),
            hash = platform.requireService(type = "MessageDigest", algorithm = "SHA-512").toSecurityService(),
            random = platform.requireService(type = "SecureRandom", algorithm = "SHA1PRNG").toSecurityService(),
        )
    }

    private suspend fun runChecksInternal() {
        for (type in ChecksType.entries) {
            when (type) {
                ChecksType.SECURITY_SERVICES -> {
//                    delay(2.seconds)
                    if (injection.local.services != null) continue
                    _state.value = State.OnChecks(type)
//                    delay(2.seconds)
                    val services = try {
                        getSecurityServices()
                    } catch (e: Throwable) {
                        _state.value = State.OnError(type, e)
                        return
                    }
                    logger.debug("services: $services")
                    injection.local.services = services
                }
                ChecksType.IDS -> {
                    if (injection.encrypted.local.deviceId == null) {
                        val deviceId = mapOf(
                            "manufacturer" to Build.MANUFACTURER,
                            "brand" to Build.BRAND,
                            "model" to Build.MODEL,
                            "device" to Build.DEVICE,
                            "supported_abis" to Build.SUPPORTED_ABIS.joinToString(separator = "/"),
                        ).entries.joinToString(separator = "-") { (key, value) ->
                            "$key:$value"
                        }
                        injection.encrypted.local.deviceId = deviceId
                        logger.debug("deviceId: $deviceId")
                    }
                    if (injection.encrypted.local.appId == null) {
                        val appId = UUID.randomUUID()
                        injection.encrypted.local.appId = appId
                        logger.debug("appId: $appId")
                    }
                }
            }
            _state.value = State.OnChecks(null)
        }
        _broadcast.emit(Broadcast.OnComplete)
    }

    fun runChecks() {
        injection.launch {
//            injection.local.services = null // todo
            withContext(injection.contexts.default) {
                runChecksInternal()
            }
        }
    }
}
