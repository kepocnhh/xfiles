package org.kepocnhh.xfiles.provider.security

import java.security.SecureRandom

internal class MockSecurityProvider(
    private val cipher: CipherProvider = MockCipherProvider(),
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
        TODO("Not yet implemented: getSignature")
    }

    override fun getSecretKeyFactory(): SecretKeyFactoryProvider {
        TODO("Not yet implemented: getSecretKeyFactory")
    }

    override fun getSecureRandom(): SecureRandom {
        TODO("Not yet implemented: getSecureRandom")
    }

    override fun getKeyFactory(): KeyFactoryProvider {
        TODO("Not yet implemented: getKeyFactory")
    }
}
