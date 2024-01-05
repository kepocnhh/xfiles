package org.kepocnhh.xfiles.entity

import org.kepocnhh.xfiles.mockBytes
import java.math.BigInteger
import java.security.interfaces.DSAParams
import java.security.interfaces.DSAPrivateKey

internal class MockDSAPrivateKey(
    private val encoded: ByteArray = mockBytes(prefix = "MockDSAPrivateKey:encoded"),
    private val params: DSAParams = MockDSAParameterSpec(),
) : DSAPrivateKey {
    constructor(issuer: String, params: DSAParams) : this(
        encoded = mockBytes("$issuer:mock:dsa:private:key:encoded"),
        params = params,
    )

    override fun getParams(): DSAParams {
        return params
    }

    override fun getAlgorithm(): String {
        error("Illegal state: getAlgorithm")
    }

    override fun getFormat(): String {
        error("Illegal state: getFormat")
    }

    override fun getEncoded(): ByteArray {
        return encoded
    }

    override fun getX(): BigInteger {
        error("Illegal state: getX")
    }
}
