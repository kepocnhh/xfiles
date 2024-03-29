package org.kepocnhh.xfiles.provider.security

internal interface Base64Provider {
    fun encode(bytes: ByteArray): String
    fun decode(encoded: String): ByteArray
}
