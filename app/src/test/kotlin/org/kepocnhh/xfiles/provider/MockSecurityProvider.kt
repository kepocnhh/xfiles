package org.kepocnhh.xfiles.provider

import org.kepocnhh.xfiles.provider.security.AlgorithmParameterGeneratorProvider
import org.kepocnhh.xfiles.provider.security.CipherProvider
import org.kepocnhh.xfiles.provider.security.KeyFactoryProvider
import org.kepocnhh.xfiles.provider.security.KeyPairGeneratorProvider
import org.kepocnhh.xfiles.provider.security.MessageDigestProvider
import org.kepocnhh.xfiles.provider.security.SecretKeyFactoryProvider
import org.kepocnhh.xfiles.provider.security.SecurityProvider
import org.kepocnhh.xfiles.provider.security.SignatureProvider
import java.security.SecureRandom

internal class MockSecurityProvider : SecurityProvider {
    override fun getMessageDigest(): MessageDigestProvider {
        TODO("Not yet implemented: getMessageDigest")
    }

    override fun getCipher(): CipherProvider {
        TODO("Not yet implemented: getCipher")
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
