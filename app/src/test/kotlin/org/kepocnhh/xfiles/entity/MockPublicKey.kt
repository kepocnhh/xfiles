package org.kepocnhh.xfiles.entity

import java.security.PublicKey

internal class MockPublicKey(
    private val encoded: ByteArray = "MockPublicKey:encoded".toByteArray()
) : PublicKey {
    override fun getAlgorithm(): String {
        TODO("Not yet implemented: getAlgorithm")
    }

    override fun getFormat(): String {
        TODO("Not yet implemented: getFormat")
    }

    override fun getEncoded(): ByteArray {
        return encoded
    }
}
