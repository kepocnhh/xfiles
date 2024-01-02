package org.kepocnhh.xfiles.provider.security

import java.security.SecureRandom

@Suppress("ComplexInterface")
internal interface SecurityProvider {
    fun getMessageDigest(algorithm: HashAlgorithm): MessageDigestProvider
    fun getCipher(): CipherProvider
    fun getKeyPairGenerator(): KeyPairGeneratorProvider
    fun getAlgorithmParameterGenerator(): AlgorithmParameterGeneratorProvider
    fun getSignature(): SignatureProvider
    fun getSecretKeyFactory(): SecretKeyFactoryProvider
    fun getSecureRandom(): SecureRandom
    fun getKeyFactory(): KeyFactoryProvider
    fun uuids(): UUIDGenerator
    fun base64(): Base64Provider
}
