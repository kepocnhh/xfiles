package org.kepocnhh.xfiles.module.router

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
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
import kotlin.time.Duration.Companion.seconds

internal class RouterViewModel(private val injection: Injection): AbstractViewModel() {
    enum class State {
        CHECKED,
        ERROR,
    }

    private val logger = injection.loggers.newLogger("[Router|VM]")
    private val _state = MutableStateFlow<State?>(null)
    val state = _state.asStateFlow()

    fun checkSecurityServices() {
        injection.launch {
            withContext(injection.contexts.default) {
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
            }.fold(
                onSuccess = {
                    _state.value = State.CHECKED
                },
                onFailure = {
                    _state.value = State.ERROR
                },
            )
        }
    }
}
