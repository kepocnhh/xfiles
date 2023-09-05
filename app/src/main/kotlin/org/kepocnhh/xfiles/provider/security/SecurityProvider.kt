package org.kepocnhh.xfiles.provider.security

internal interface SecurityProvider {
    fun getMessageDigest(algorithm: String): MessageDigestProvider
    fun getCipher(transformation: String): CipherProvider
    fun getKeyPairGenerator(algorithm: String): KeyPairGeneratorProvider
    fun getAlgorithmParameterGenerator(algorithm: String): AlgorithmParameterGeneratorProvider
    fun getSignature(algorithm: String): SignatureProvider
}
