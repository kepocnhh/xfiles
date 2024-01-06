package org.kepocnhh.xfiles.provider

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.kepocnhh.xfiles.entity.MockBaseBlockCipher
import org.kepocnhh.xfiles.entity.MockCipher
import org.kepocnhh.xfiles.entity.MockSecretKey
import org.kepocnhh.xfiles.mockBytes
import org.robolectric.RobolectricTestRunner
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec

@RunWith(RobolectricTestRunner::class)
internal class CipherEncryptionTest {
    @Test(timeout = 2_000)
    fun encryptTest() {
        val issuer = "${this::class.java.name}:encryptTest"
        val encrypted = mockBytes(issuer)
        val decrypted = mockBytes(issuer)
        check(!encrypted.contentEquals(decrypted))
        val cipher = MockCipher(
            cipherSpi = MockBaseBlockCipher(
                values = listOf(encrypted to decrypted),
            ),
        )
        val iv = mockBytes(issuer)
        cipher.init(Cipher.ENCRYPT_MODE, MockSecretKey(), IvParameterSpec(iv))
        val encrypt: Encrypt = CipherEncrypt(cipher)
        val actual = encrypt.doFinal(decrypted)
        Assert.assertTrue(actual.encrypted.isNotEmpty())
        Assert.assertTrue(actual.iv.isNotEmpty())
        Assert.assertTrue(actual.iv.contentEquals(cipher.iv))
        Assert.assertTrue(actual.iv.contentEquals(iv))
        Assert.assertTrue(actual.encrypted.contentEquals(encrypted))
    }

    @Test(timeout = 2_000)
    fun decryptTest() {
        val issuer = "${this::class.java.name}:decryptTest"
        val encrypted = mockBytes(issuer)
        val decrypted = mockBytes(issuer)
        check(!encrypted.contentEquals(decrypted))
        val cipher = MockCipher(
            cipherSpi = MockBaseBlockCipher(
                values = listOf(encrypted to decrypted),
            ),
        )
        cipher.init(Cipher.DECRYPT_MODE, MockSecretKey())
        val decrypt: Decrypt = CipherDecrypt(cipher)
        val actual = decrypt.doFinal(encrypted)
        Assert.assertTrue(actual.isNotEmpty())
        Assert.assertTrue(actual.contentEquals(decrypted))
    }
}
