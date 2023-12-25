package org.kepocnhh.xfiles.provider.security

import org.kepocnhh.xfiles.entity.MockAlgorithmParameters
import java.security.AlgorithmParameters
import java.security.SecureRandom

internal class MockAlgorithmParameterGeneratorProvider(
    private val params: AlgorithmParameters = MockAlgorithmParameters(),
) : AlgorithmParameterGeneratorProvider {
    override fun generate(size: Int, random: SecureRandom): AlgorithmParameters {
        return params
    }
}
