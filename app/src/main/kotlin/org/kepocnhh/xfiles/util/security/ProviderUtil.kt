package org.kepocnhh.xfiles.util.security

import java.security.NoSuchAlgorithmException
import java.security.Provider

internal fun Provider.getServiceOrNull(type: String, algorithm: String): Provider.Service? {
    return services.firstOrNull {
        it.type.equals(type, ignoreCase = true) && it.algorithm.equals(algorithm, ignoreCase = true)
    }
}

internal fun Provider.requireService(type: String, algorithm: String): Provider.Service {
    return getServiceOrNull(type = type, algorithm = algorithm)
        ?: throw NoSuchAlgorithmException("No such algorithm $name:$type:$algorithm!")
}
