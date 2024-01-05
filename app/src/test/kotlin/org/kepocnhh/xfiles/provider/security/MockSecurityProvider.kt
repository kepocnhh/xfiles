package org.kepocnhh.xfiles.provider.security

import java.security.SecureRandom

@Suppress("LongParameterList")
internal class MockSecurityProvider(
    private val cipher: CipherProvider = MockCipherProvider(),
    private val uuids: UUIDGenerator = MockUUIDGenerator(),
    private val signature: SignatureProvider = MockSignatureProvider(),
    private val keyFactory: KeyFactoryProvider = MockKeyFactoryProvider(),
    private val random: SecureRandom = MockSecureRandom(),
    private val sha512: MessageDigestProvider = MockMessageDigestProvider(),
    private val md5: MessageDigestProvider = MockMessageDigestProvider(),
    private val base64: Base64Provider = MockBase64Provider(),
    private val keyPairGenerator: KeyPairGeneratorProvider = MockKeyPairGeneratorProvider(),
    private val algorithmParamsGenerator: AlgorithmParameterGeneratorProvider =
        MockAlgorithmParameterGeneratorProvider(),
    private val secretKeyFactory: SecretKeyFactoryProvider = MockSecretKeyFactoryProvider(),
) : SecurityProvider {
    override fun getMessageDigest(algorithm: HashAlgorithm): MessageDigestProvider {
        return when (algorithm) {
            HashAlgorithm.MD5 -> md5
            HashAlgorithm.SHA512 -> sha512
        }
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
