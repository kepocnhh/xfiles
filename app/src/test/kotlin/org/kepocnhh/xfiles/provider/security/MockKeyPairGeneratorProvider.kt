package org.kepocnhh.xfiles.provider.security

import java.security.KeyPair
import java.security.spec.AlgorithmParameterSpec

internal class MockKeyPairGeneratorProvider(
    private val pairs: Map<AlgorithmParameterSpec, KeyPair> = emptyMap(),
) : KeyPairGeneratorProvider {
    override fun generate(params: AlgorithmParameterSpec): KeyPair {
        return pairs[params] ?: error("No key pair!")
    }
}
