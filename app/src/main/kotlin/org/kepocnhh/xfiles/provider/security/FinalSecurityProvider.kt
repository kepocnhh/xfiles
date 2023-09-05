package org.kepocnhh.xfiles.provider.security

import java.security.AlgorithmParameterGenerator
import java.security.AlgorithmParameters
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.MessageDigest
import java.security.PrivateKey
import java.security.PublicKey
import java.security.SecureRandom
import java.security.Signature
import java.security.spec.AlgorithmParameterSpec
import javax.crypto.Cipher
import javax.crypto.SecretKey

private class MessageDigestProviderImpl(
    private val delegate: MessageDigest,
) : MessageDigestProvider {
    override fun digest(bytes: ByteArray): ByteArray {
        return delegate.digest(bytes)
    }
}

private class CipherProviderImpl(
    private val delegate: Cipher
) : CipherProvider {
    override fun encrypt(
        key: SecretKey,
        params: AlgorithmParameterSpec,
        decrypted: ByteArray
    ): ByteArray {
        delegate.init(Cipher.ENCRYPT_MODE, key, params)
        return delegate.doFinal(decrypted)
    }

    override fun decrypt(
        key: SecretKey,
        params: AlgorithmParameterSpec,
        decrypted: ByteArray
    ): ByteArray {
        delegate.init(Cipher.DECRYPT_MODE, key, params)
        return delegate.doFinal(decrypted)
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

internal class FinalSecurityProvider : SecurityProvider {
    companion object {
        private const val provider = "BC"
    }

    override fun getMessageDigest(algorithm: String): MessageDigestProvider {
        return MessageDigestProviderImpl(MessageDigest.getInstance(algorithm))
    }

    override fun getCipher(transformation: String): CipherProvider {
        return CipherProviderImpl(Cipher.getInstance(transformation, provider))
    }

    override fun getKeyPairGenerator(algorithm: String): KeyPairGeneratorProvider {
        return KeyPairGeneratorProviderImpl(KeyPairGenerator.getInstance(algorithm, provider))
    }

    override fun getAlgorithmParameterGenerator(algorithm: String): AlgorithmParameterGeneratorProvider {
        return AlgorithmParameterGeneratorProviderImpl(AlgorithmParameterGenerator.getInstance(algorithm, provider))
    }

    override fun getSignature(algorithm: String): SignatureProvider {
        return SignatureProviderImpl(Signature.getInstance(algorithm, provider))
    }
}
