package org.kepocnhh.xfiles.entity

import org.kepocnhh.xfiles.mockBytes
import javax.crypto.SecretKey

internal class MockSecretKey(
    private val encoded: ByteArray = mockBytes(prefix = "MockSecretKey:encoded"),
) : SecretKey {
    constructor(issuer: String) : this(encoded = mockBytes(issuer))

    override fun getAlgorithm(): String {
        error("Illegal state: getAlgorithm")
    }

    override fun getFormat(): String {
        error("Illegal state: getFormat")
    }

    override fun getEncoded(): ByteArray {
        return encoded
    }
}
