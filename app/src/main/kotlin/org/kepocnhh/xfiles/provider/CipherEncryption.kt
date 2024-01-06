package org.kepocnhh.xfiles.provider

import javax.crypto.Cipher

internal class CipherEncrypt(
    private val cipher: Cipher,
) : Encrypt {
    override fun doFinal(decrypted: ByteArray): Encrypt.Data {
        return Encrypt.Data(
            encrypted = cipher.doFinal(decrypted),
            iv = cipher.iv ?: error("No IV!"),
        )
    }
}

internal class CipherDecrypt(
    private val cipher: Cipher,
) : Decrypt {
    override fun doFinal(encrypted: ByteArray): ByteArray {
        return cipher.doFinal(encrypted)
    }
}
