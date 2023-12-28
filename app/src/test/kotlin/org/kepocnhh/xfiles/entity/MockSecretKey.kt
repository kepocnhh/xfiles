package org.kepocnhh.xfiles.entity

import javax.crypto.SecretKey

internal class MockSecretKey(
    private val encoded: ByteArray = "foo:encoded".toByteArray(),
) : SecretKey {
    constructor(issuer: String) : this(encoded = "$issuer:secret:key:encoded".toByteArray())

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
