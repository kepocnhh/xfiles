package org.kepocnhh.xfiles.provider.security

internal interface SecurityProvider {
    fun getMessageDigest(algorithm: String): MessageDigestProvider
    fun getCipher(transformation: String): CipherProvider
}
