package org.kepocnhh.xfiles.entity

import org.kepocnhh.xfiles.mockBytes
import javax.crypto.SecretKey

internal class MockSecretKey(
    private val encoded: ByteArray = "MockSecretKey:encoded".toByteArray(),
) : SecretKey {
    constructor(issuer: String) : this(encoded = mockBytes(issuer))

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
