package org.kepocnhh.xfiles.provider.security

import java.security.AlgorithmParameters
import java.security.SecureRandom

internal interface AlgorithmParameterGeneratorProvider {
    fun generate(size: Int, random: SecureRandom): AlgorithmParameters
}
