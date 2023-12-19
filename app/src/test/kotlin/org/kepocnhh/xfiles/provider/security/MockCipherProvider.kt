package org.kepocnhh.xfiles.provider.security

import java.security.spec.AlgorithmParameterSpec
import javax.crypto.SecretKey

internal class MockCipherProvider(
    private val values: List<Triple<ByteArray, ByteArray, SecretKey>> = emptyList(),
) : CipherProvider {
    override fun encrypt(
        key: SecretKey,
        params: AlgorithmParameterSpec,
        decrypted: ByteArray
    ): ByteArray {
        TODO("Not yet implemented: encrypt")
    }

    override fun decrypt(
        key: SecretKey,
        params: AlgorithmParameterSpec,
        encrypted: ByteArray
    ): ByteArray {
        for ((e, decrypted, k) in values) {
            if (key == k && encrypted.contentEquals(e)) return decrypted
        }
        error("No decrypted!")
    }
}
