package org.kepocnhh.xfiles.util.security

import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.AlgorithmParameterSpec
import javax.crypto.Cipher
import javax.crypto.SecretKey

internal fun Cipher.encrypt(key: PublicKey, decrypted: ByteArray): ByteArray {
    init(Cipher.ENCRYPT_MODE, key)
    return doFinal(decrypted)
}

internal fun Cipher.encrypt(key: SecretKey, params: AlgorithmParameterSpec, decrypted: ByteArray): ByteArray {
    init(Cipher.ENCRYPT_MODE, key, params)
    return doFinal(decrypted)
}

internal fun Cipher.decrypt(key: SecretKey, params: AlgorithmParameterSpec, decrypted: ByteArray): ByteArray {
    init(Cipher.DECRYPT_MODE, key, params)
    return doFinal(decrypted)
}

internal fun Cipher.decrypt(key: PrivateKey, decrypted: ByteArray): ByteArray {
    init(Cipher.DECRYPT_MODE, key)
    return doFinal(decrypted)
}
