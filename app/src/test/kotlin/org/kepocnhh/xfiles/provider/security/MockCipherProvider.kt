package org.kepocnhh.xfiles.provider.security

import java.security.spec.AlgorithmParameterSpec
import javax.crypto.SecretKey

internal class MockCipherProvider(
    private val values: List<DataSet> = emptyList(),
) : CipherProvider {
    class DataSet(
        val encrypted: ByteArray,
        val decrypted: ByteArray,
        val secretKey: SecretKey,
    )

    class NoDecryptedException(
        val key: SecretKey,
        val params: AlgorithmParameterSpec,
        val encrypted: ByteArray,
    ) : Exception()

    override fun encrypt(
        key: SecretKey,
        params: AlgorithmParameterSpec,
        decrypted: ByteArray,
    ): ByteArray {
        for (it in values) {
            if (key == it.secretKey && decrypted.contentEquals(it.decrypted)) return it.encrypted
        }
        error("Cipher: No encrypted!")
    }

    override fun decrypt(
        key: SecretKey,
        params: AlgorithmParameterSpec,
        encrypted: ByteArray,
    ): ByteArray {
        for (it in values) {
            if (key == it.secretKey && encrypted.contentEquals(it.encrypted)) return it.decrypted
        }
        throw NoDecryptedException(key = key, params = params, encrypted = encrypted)
    }
}
