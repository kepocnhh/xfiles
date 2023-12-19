package org.kepocnhh.xfiles.entity

import javax.crypto.SecretKey

internal class MockSecretKey(
    private val encoded: ByteArray = "foo".toByteArray(),
) : SecretKey {
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
