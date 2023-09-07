package org.kepocnhh.xfiles.util.security

import java.security.NoSuchProviderException
import java.security.Provider
import java.security.Security

internal object SecurityUtil {
    fun requireProvider(name: String): Provider {
        return Security.getProviders().firstOrNull { it.name == name } ?: throw NoSuchProviderException("No such provider \"$name\"!")
    }
}
