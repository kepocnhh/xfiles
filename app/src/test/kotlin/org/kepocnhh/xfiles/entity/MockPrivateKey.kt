package org.kepocnhh.xfiles.entity

import org.kepocnhh.xfiles.mockBytes
import java.security.PrivateKey

internal class MockPrivateKey(
    private val encoded: ByteArray = mockBytes(prefix = "MockPrivateKey:encoded"),
) : PrivateKey {
    constructor(issuer: String) : this(encoded = mockBytes("$issuer:private:key:encoded"))

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
