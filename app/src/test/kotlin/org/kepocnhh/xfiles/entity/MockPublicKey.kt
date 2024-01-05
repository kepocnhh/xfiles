package org.kepocnhh.xfiles.entity

import org.kepocnhh.xfiles.mockBytes
import java.security.PublicKey

internal class MockPublicKey(
    private val encoded: ByteArray = mockBytes(prefix = "MockPublicKey:encoded"),
) : PublicKey {
    constructor(issuer: String) : this(encoded = mockBytes("$issuer:mock:public:key:encoded"))

    override fun getAlgorithm(): String {
        error("Illegal state: getAlgorithm")
    }

    override fun getFormat(): String {
        error("Illegal state: getFormat")
    }

    override fun getEncoded(): ByteArray {
        return encoded
    }

    override fun toString(): String {
        return "Mock:Public:Key(${encoded.slice(0..5).joinToString()}...)"
    }
}
