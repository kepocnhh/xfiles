package org.kepocnhh.xfiles.provider.biometric

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import org.kepocnhh.xfiles.BuildConfig
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

internal class FinalBiometricProvider : BiometricProvider {
    override fun deleteSecretKey() {
        val keyStore = getKeyStore()
        try {
            keyStore.deleteEntry(keyAlias)
        } catch (e: Throwable) {
            // todo
        }
    }

    override fun getCipher(mode: Int): Cipher {
        val cipher = getCipher()
        when (mode) {
            Cipher.ENCRYPT_MODE -> {
                cipher.init(mode, getOrCreate(keyAlias))
                // todo params
            }
            Cipher.DECRYPT_MODE -> {
//                cipher.init(mode, getOrCreate(keyAlias), params)
                TODO()
            }
            else -> error("Operation mode \"$mode\" is not supported!")
        }
        return cipher
    }

    companion object {
        private const val algorithm = KeyProperties.KEY_ALGORITHM_AES
        private const val blocks = KeyProperties.BLOCK_MODE_GCM
//        private const val blocks = KeyProperties.BLOCK_MODE_CBC
        private const val paddings = KeyProperties.ENCRYPTION_PADDING_NONE
//        private const val paddings = KeyProperties.ENCRYPTION_PADDING_PKCS7
        private val keyAlias = BuildConfig.APPLICATION_ID + ":foo:1" // todo

        private fun getOrCreate(keyAlias: String): SecretKey {
            val keyStore = getKeyStore()
            if (keyStore.containsAlias(keyAlias)) return keyStore.getKey(keyAlias, null) as SecretKey
            val purposes = KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            val keySize = 256
            val spec = KeyGenParameterSpec
                .Builder(keyAlias, purposes)
                .setBlockModes(blocks)
                .setEncryptionPaddings(paddings)
                .setKeySize(keySize)
                .setUserAuthenticationRequired(true)
                .setInvalidatedByBiometricEnrollment(true)
                .setUserAuthenticationValidityDurationSeconds(0)
                .build()
            val keyGenerator = KeyGenerator.getInstance(algorithm, keyStore.provider)
            keyGenerator.init(spec)
            return keyGenerator.generateKey()
        }

        private fun getCipher(): Cipher {
            return Cipher.getInstance( "$algorithm/$blocks/$paddings")
        }

        private fun getKeyStore(): KeyStore {
            val keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)
            return keyStore
        }
    }
}
