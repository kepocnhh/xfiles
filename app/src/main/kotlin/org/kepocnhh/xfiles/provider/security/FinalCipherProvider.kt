package org.kepocnhh.xfiles.provider.security

import java.security.spec.AlgorithmParameterSpec
import javax.crypto.Cipher
import javax.crypto.SecretKey

internal class FinalCipherProvider(private val delegate: Cipher) : CipherProvider {
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
