package org.kepocnhh.xfiles.provider.security

import java.security.SecureRandom

internal class MockSecureRandom(
    private val values: Map<Int, ByteArray> = emptyMap(),
) : SecureRandom() {
    @Suppress("IgnoredReturnValue")
    override fun nextBytes(bytes: ByteArray) {
        val size = bytes.size
        val value = values[size] ?: error("No bytes by size $size!")
        check(value.size == size)
        value.copyInto(destination = bytes)
    }
}
