package org.kepocnhh.xfiles.provider.security

import android.os.Build
import org.kepocnhh.xfiles.entity.SecurityServices
import java.security.AlgorithmParameterGenerator
import java.security.AlgorithmParameters
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.MessageDigest
import java.security.PrivateKey
import java.security.PublicKey
import java.security.SecureRandom
import java.security.Signature
import java.security.spec.AlgorithmParameterSpec
import java.security.spec.KeySpec
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.UUID
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory

private class KeyPairGeneratorProviderImpl(
    private val delegate: KeyPairGenerator,
) : KeyPairGeneratorProvider {
    override fun generate(params: AlgorithmParameterSpec): KeyPair {
        delegate.initialize(params)
        return delegate.generateKeyPair()
    }
}

private class AlgorithmParameterGeneratorProviderImpl(
    private val delegate: AlgorithmParameterGenerator,
) : AlgorithmParameterGeneratorProvider {
    override fun generate(size: Int, random: SecureRandom): AlgorithmParameters {
        delegate.init(size, random)
        return delegate.generateParameters()
    }
}

private class SecretKeyFactoryProviderImpl(
    private val delegate: SecretKeyFactory,
) : SecretKeyFactoryProvider {
    override fun generate(params: KeySpec): SecretKey {
        return delegate.generateSecret(params)
    }
}

private class KeyFactoryProviderImpl(
    private val delegate: KeyFactory,
) : KeyFactoryProvider {
    override fun generatePublic(bytes: ByteArray): PublicKey {
        return delegate.generatePublic(X509EncodedKeySpec(bytes))
    }

    override fun generatePrivate(bytes: ByteArray): PrivateKey {
        return delegate.generatePrivate(PKCS8EncodedKeySpec(bytes))
    }
}

private object UUIDGeneratorImpl : UUIDGenerator {
    override fun generate(): UUID {
        return UUID.randomUUID()
    }
}

internal class FinalSecurityProvider(
    private val services: SecurityServices,
) : SecurityProvider {
    override fun getMessageDigest(algorithm: HashAlgorithm): MessageDigestProvider {
        val service = when (algorithm) {
            HashAlgorithm.MD5 -> services.md5
            HashAlgorithm.SHA512 -> services.sha512
        }
        return FinalMessageDigestProvider(MessageDigest.getInstance(service.algorithm, service.provider))
    }

    override fun getCipher(): CipherProvider {
        val service = services.cipher
        return FinalCipherProvider(Cipher.getInstance(service.algorithm, service.provider))
    }

    override fun getKeyPairGenerator(): KeyPairGeneratorProvider {
        val service = services.asymmetric
        return KeyPairGeneratorProviderImpl(KeyPairGenerator.getInstance(service.algorithm, service.provider))
    }

    override fun getAlgorithmParameterGenerator(): AlgorithmParameterGeneratorProvider {
        val service = services.asymmetric
        return AlgorithmParameterGeneratorProviderImpl(
            delegate = AlgorithmParameterGenerator.getInstance(service.algorithm, service.provider),
        )
    }

    override fun getSignature(): SignatureProvider {
        val service = services.signature
        return FinalSignatureProvider(Signature.getInstance(service.algorithm, service.provider))
    }

    override fun getSecretKeyFactory(): SecretKeyFactoryProvider {
        val service = services.symmetric
        return SecretKeyFactoryProviderImpl(SecretKeyFactory.getInstance(service.algorithm, service.provider))
    }

    override fun getSecureRandom(): SecureRandom {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            SecureRandom.getInstanceStrong()
        } else {
            val service = services.random
            SecureRandom.getInstance(service.algorithm, service.provider)
        }
    }

    override fun getKeyFactory(): KeyFactoryProvider {
        val service = services.asymmetric
        return KeyFactoryProviderImpl(KeyFactory.getInstance(service.algorithm, service.provider))
    }

    override fun uuids(): UUIDGenerator {
        return UUIDGeneratorImpl
    }

    override fun base64(): Base64Provider {
        return FinalBase64Provider
    }
}
