package org.kepocnhh.xfiles.provider.security

import java.security.PrivateKey
import java.security.PublicKey

internal interface KeyFactoryProvider {
    fun generatePublic(bytes: ByteArray): PublicKey
    fun generatePrivate(bytes: ByteArray): PrivateKey
}
