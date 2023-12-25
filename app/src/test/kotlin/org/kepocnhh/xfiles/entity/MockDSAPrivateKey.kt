package org.kepocnhh.xfiles.entity

import java.math.BigInteger
import java.security.interfaces.DSAParams
import java.security.interfaces.DSAPrivateKey

internal class MockDSAPrivateKey(
    private val encoded: ByteArray = "MockDSAPrivateKey:encoded".toByteArray(),
    private val params: DSAParams = MockDSAParameterSpec(),
) : DSAPrivateKey {
    override fun getParams(): DSAParams {
        return params
    }

    override fun getAlgorithm(): String {
        TODO("Not yet implemented: getAlgorithm")
    }

    override fun getFormat(): String {
        TODO("Not yet implemented: getFormat")
    }

    override fun getEncoded(): ByteArray {
        return encoded
    }

    override fun getX(): BigInteger {
        TODO("Not yet implemented: getX")
    }
}
