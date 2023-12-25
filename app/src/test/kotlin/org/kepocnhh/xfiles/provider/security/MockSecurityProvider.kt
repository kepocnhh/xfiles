package org.kepocnhh.xfiles.provider.security

import java.security.SecureRandom

internal class MockSecurityProvider(
    private val cipher: CipherProvider = MockCipherProvider(),
    private val uuids: UUIDGenerator = MockUUIDGenerator(),
    private val signature: SignatureProvider = MockSignatureProvider(),
    private val keyFactory: KeyFactoryProvider = MockKeyFactoryProvider(),
    private val random: SecureRandom = MockSecureRandom(),
    private val md: MessageDigestProvider = MockMessageDigestProvider(),
    private val base64: Base64Provider = MockBase64Provider(),
    private val keyPairGenerator: KeyPairGeneratorProvider = MockKeyPairGeneratorProvider(),
    private val algorithmParamsGenerator: AlgorithmParameterGeneratorProvider = MockAlgorithmParameterGeneratorProvider(),
    private val secretKeyFactory: SecretKeyFactoryProvider = MockSecretKeyFactoryProvider(),
) : SecurityProvider {
    override fun getMessageDigest(): MessageDigestProvider {
        return md
    }

    override fun getCipher(): CipherProvider {
        return cipher
    }

    override fun getKeyPairGenerator(): KeyPairGeneratorProvider {
        return keyPairGenerator
    }

    override fun getAlgorithmParameterGenerator(): AlgorithmParameterGeneratorProvider {
        return algorithmParamsGenerator
    }

    override fun getSignature(): SignatureProvider {
        return signature
    }

    override fun getSecretKeyFactory(): SecretKeyFactoryProvider {
        return secretKeyFactory
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

    override fun base64(): Base64Provider {
        return base64
    }
}
