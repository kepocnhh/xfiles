package org.kepocnhh.xfiles.provider.security

import java.security.SecureRandom

internal class MockSecurityProvider(
    private val cipher: CipherProvider = MockCipherProvider(),
    private val uuids: UUIDGenerator = MockUUIDGenerator(),
    private val signature: SignatureProvider = MockSignatureProvider(),
    private val keyFactory: KeyFactoryProvider = MockKeyFactoryProvider(),
    private val random: SecureRandom = MockSecureRandom(),
) : SecurityProvider {
    override fun getMessageDigest(): MessageDigestProvider {
        TODO("Not yet implemented: getMessageDigest")
    }

    override fun getCipher(): CipherProvider {
        return cipher
    }

    override fun getKeyPairGenerator(): KeyPairGeneratorProvider {
        TODO("Not yet implemented: getKeyPairGenerator")
    }

    override fun getAlgorithmParameterGenerator(): AlgorithmParameterGeneratorProvider {
        TODO("Not yet implemented: getAlgorithmParameterGenerator")
    }

    override fun getSignature(): SignatureProvider {
        return signature
    }

    override fun getSecretKeyFactory(): SecretKeyFactoryProvider {
        TODO("Not yet implemented: getSecretKeyFactory")
    }

    override fun getSecureRandom(): SecureRandom {
        return random
    }

    override fun getKeyFactory(): KeyFactoryProvider {
        return keyFactory
    }

    override fun uuids(): UUIDGenerator {
        return uuids
    }
}
