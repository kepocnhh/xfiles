package org.kepocnhh.xfiles.provider.security

import java.security.MessageDigest
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
}
