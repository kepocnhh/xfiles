package org.kepocnhh.xfiles.entity

import org.kepocnhh.xfiles.TestProvider
import org.kepocnhh.xfiles.mockBytes
import java.security.AlgorithmParameters
import java.security.AlgorithmParametersSpi
import java.security.Provider
import java.security.spec.AlgorithmParameterSpec

private class MockAlgorithmParametersSpi(
    private val encoded: ByteArray = mockBytes(prefix = "MockAlgorithmParametersSpi:encoded"),
) : AlgorithmParametersSpi() {
    private var paramSpec: AlgorithmParameterSpec? = null

    override fun engineInit(paramSpec: AlgorithmParameterSpec?) {
        this.paramSpec = paramSpec
    }

    override fun engineInit(params: ByteArray?) {
        error("Illegal state: engineInit")
    }

    override fun engineInit(params: ByteArray?, format: String?) {
        error("Illegal state: engineInit")
    }

    override fun <T : AlgorithmParameterSpec?> engineGetParameterSpec(paramSpec: Class<T>?): T {
        return this.paramSpec as T
    }

    override fun engineGetEncoded(): ByteArray {
        error("Illegal state: engineGetEncoded")
    }

    override fun engineGetEncoded(format: String?): ByteArray {
        return encoded
    }

    override fun engineToString(): String {
        error("Illegal state: engineToString")
    }
}

internal class MockAlgorithmParameters(
    encoded: ByteArray = mockBytes(prefix = "MockAlgorithmParameters:encoded"),
    provider: Provider = TestProvider("MockAlgorithmParameters:provider"),
    algorithm: String = "MockAlgorithmParameters:algorithm",
) : AlgorithmParameters(
    MockAlgorithmParametersSpi(encoded = encoded),
    provider,
    algorithm,
) {
    constructor(issuer: String) : this(
        encoded = mockBytes("$issuer:mock:algorithm:parameters:encoded"),
        provider = TestProvider("$issuer:mock:algorithm:parameters:provider"),
        algorithm = "$issuer:mock:algorithm:parameters:algorithm",
    )
}
