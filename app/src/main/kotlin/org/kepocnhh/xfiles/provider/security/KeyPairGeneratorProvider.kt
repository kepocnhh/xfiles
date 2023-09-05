package org.kepocnhh.xfiles.provider.security

import java.security.KeyPair
import java.security.spec.AlgorithmParameterSpec

internal interface KeyPairGeneratorProvider {
    fun generate(params: AlgorithmParameterSpec): KeyPair
}
