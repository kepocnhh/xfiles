package org.kepocnhh.xfiles.entity

import java.security.PublicKey

internal class MockPublicKey(
    private val encoded: ByteArray = "MockPublicKey:encoded".toByteArray()
) : PublicKey {
    constructor(issuer: String) : this(
        encoded = "$issuer:mock:public:key:encoded".toByteArray(),
    )

    override fun getAlgorithm(): String {
        TODO("Not yet implemented: getAlgorithm")
    }

    override fun getFormat(): String {
        TODO("Not yet implemented: getFormat")
    }

    override fun getEncoded(): ByteArray {
        return encoded
    }

    override fun toString(): String {
        return "Mock:Public:Key(${encoded.slice(0..5).joinToString()}...)"
    }
}
