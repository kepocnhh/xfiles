package org.kepocnhh.xfiles.provider.biometric

import javax.crypto.Cipher

internal interface BiometricProvider {
    fun deleteSecretKey()

    /**
     * @param mode [Cipher.ENCRYPT_MODE] or [Cipher.DECRYPT_MODE]
     */
    fun getCipher(mode: Int): Cipher
}
