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

private class MessageDigestProviderImpl(
    private val delegate: MessageDigest,
) : MessageDigestProvider {
    override fun digest(bytes: ByteArray): ByteArray {
        return delegate.digest(bytes)
    }
}

private class CipherProviderImpl(
    private val delegate: Cipher,
) : CipherProvider {
    override fun encrypt(
        key: SecretKey,
        params: AlgorithmParameterSpec,
        decrypted: ByteArray,
    ): ByteArray {
        delegate.init(Cipher.ENCRYPT_MODE, key, params)
        return delegate.doFinal(decrypted)
    }

    override fun decrypt(
        key: SecretKey,
        params: AlgorithmParameterSpec,
        encrypted: ByteArray,
    ): ByteArray {
        delegate.init(Cipher.DECRYPT_MODE, key, params)
        return delegate.doFinal(encrypted)
    }
}

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

private class SignatureProviderImpl(
    private val delegate: Signature,
) : SignatureProvider {
    override fun sign(key: PrivateKey, random: SecureRandom, decrypted: ByteArray): ByteArray {
        delegate.initSign(key, random)
        delegate.update(decrypted)
        return delegate.sign()
    }

    override fun verify(key: PublicKey, decrypted: ByteArray, sig: ByteArray): Boolean {
        delegate.initVerify(key)
        delegate.update(decrypted)
        return delegate.verify(sig)
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
    override fun getMessageDigest(): MessageDigestProvider {
        val service = services.hash
        return MessageDigestProviderImpl(MessageDigest.getInstance(service.algorithm, service.provider))
    }

    override fun getCipher(): CipherProvider {
        val service = services.cipher
        return CipherProviderImpl(Cipher.getInstance(service.algorithm, service.provider))
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
        return SignatureProviderImpl(Signature.getInstance(service.algorithm, service.provider))
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
