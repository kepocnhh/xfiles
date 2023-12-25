package org.kepocnhh.xfiles.provider.security

import java.security.SecureRandom

internal interface SecurityProvider {
    // todo getMD5
    @Deprecated(message = "replace with getSHA512")
    fun getMessageDigest(): MessageDigestProvider
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
