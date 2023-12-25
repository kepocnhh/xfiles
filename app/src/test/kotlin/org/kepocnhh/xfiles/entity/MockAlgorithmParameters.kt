package org.kepocnhh.xfiles.entity

import org.kepocnhh.xfiles.TestProvider
import java.security.AlgorithmParameters
import java.security.AlgorithmParametersSpi
import java.security.Provider
import java.security.spec.AlgorithmParameterSpec

private class MockAlgorithmParametersSpi(
    private val encoded: ByteArray = "MockAlgorithmParametersSpi:encoded".toByteArray(),
) : AlgorithmParametersSpi() {
    private var paramSpec: AlgorithmParameterSpec? = null

    override fun engineInit(paramSpec: AlgorithmParameterSpec?) {
        this.paramSpec = paramSpec
    }

    override fun engineInit(params: ByteArray?) {
        TODO("Not yet implemented: engineInit")
    }

    override fun engineInit(params: ByteArray?, format: String?) {
        TODO("Not yet implemented: engineInit")
    }

    override fun <T : AlgorithmParameterSpec?> engineGetParameterSpec(paramSpec: Class<T>?): T {
        return this.paramSpec as T
    }

    override fun engineGetEncoded(): ByteArray {
        TODO("Not yet implemented: engineGetEncoded")
    }

    override fun engineGetEncoded(format: String?): ByteArray {
        return encoded
    }

    override fun engineToString(): String {
        TODO("Not yet implemented: engineToString")
    }
}

internal class MockAlgorithmParameters(
    encoded: ByteArray = "MockAlgorithmParameters:encoded".toByteArray(),
    provider: Provider = TestProvider("MockAlgorithmParameters:provider"),
    algorithm: String = "MockAlgorithmParameters:algorithm",
) : AlgorithmParameters(
    MockAlgorithmParametersSpi(encoded = encoded),
    provider,
    algorithm,
) {
    init {

    }
}
