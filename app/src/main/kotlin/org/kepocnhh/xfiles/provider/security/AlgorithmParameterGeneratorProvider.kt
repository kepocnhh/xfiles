package org.kepocnhh.xfiles.provider.security

import java.security.AlgorithmParameters

internal interface AlgorithmParameterGeneratorProvider {
    fun generate(size: Int): AlgorithmParameters
}
