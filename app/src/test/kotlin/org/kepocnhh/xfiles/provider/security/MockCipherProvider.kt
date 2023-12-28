package org.kepocnhh.xfiles.provider.security

import java.security.spec.AlgorithmParameterSpec
import javax.crypto.SecretKey

internal class MockCipherProvider(
    private val values: List<Triple<ByteArray, ByteArray, SecretKey>> = emptyList(),
) : CipherProvider {
    class NoDecryptedException(
        val key: SecretKey,
        val params: AlgorithmParameterSpec,
        val encrypted: ByteArray
    ): Exception()

    override fun encrypt(
        key: SecretKey,
        params: AlgorithmParameterSpec,
        decrypted: ByteArray
    ): ByteArray {
        for ((encrypted, d, k) in values) {
            if (key == k && decrypted.contentEquals(d)) return encrypted
        }
        error("Cipher: No encrypted!")
    }

    override fun decrypt(
        key: SecretKey,
        params: AlgorithmParameterSpec,
        encrypted: ByteArray
    ): ByteArray {
        for ((e, decrypted, k) in values) {
            if (key == k && encrypted.contentEquals(e)) return decrypted
        }
        throw NoDecryptedException(key = key, params = params, encrypted = encrypted)
    }
}
