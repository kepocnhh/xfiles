package org.kepocnhh.xfiles.provider.security

import java.security.spec.AlgorithmParameterSpec
import javax.crypto.SecretKey

internal interface CipherProvider {
    fun encrypt(key: SecretKey, params: AlgorithmParameterSpec, decrypted: ByteArray): ByteArray
    fun decrypt(key: SecretKey, params: AlgorithmParameterSpec, decrypted: ByteArray): ByteArray
}
