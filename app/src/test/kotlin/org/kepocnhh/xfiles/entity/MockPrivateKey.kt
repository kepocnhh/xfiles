package org.kepocnhh.xfiles.entity

import java.security.PrivateKey

internal class MockPrivateKey(
    private val encoded: ByteArray = "MockPrivateKey:encoded".toByteArray()
) : PrivateKey {
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