package org.kepocnhh.xfiles.provider.security

internal interface MessageDigestProvider {
    fun digest(bytes: ByteArray): ByteArray
}
