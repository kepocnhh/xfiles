package org.kepocnhh.xfiles.provider.security

import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import org.kepocnhh.xfiles.entity.MockBaseBlockCipher
import org.kepocnhh.xfiles.entity.MockCipher
import org.kepocnhh.xfiles.entity.MockSecretKey
import org.kepocnhh.xfiles.mockBytes
import javax.crypto.spec.IvParameterSpec
import kotlin.time.Duration.Companion.seconds

internal class FinalCipherProviderTest {
    @Test
    fun decryptTest() {
        runTest(timeout = 2.seconds) {
            val issuer = "${this::class.java.name}:decryptTest"
            val encrypted = mockBytes(issuer)
            val decrypted = mockBytes(issuer)
            check(!encrypted.contentEquals(decrypted))
            val iv = mockBytes(issuer)
            val key = MockSecretKey(issuer = issuer)
            val cipher = MockCipher(
                cipherSpi = MockBaseBlockCipher(
                    values = listOf(
                        MockBaseBlockCipher.DataSet(
                            key = key,
                            encrypted = encrypted,
                            decrypted = decrypted,
                            iv = iv,
                        ),
                    ),
                ),
            )
            val provider: CipherProvider = FinalCipherProvider(cipher)
            val actual = provider.decrypt(
                encrypted = encrypted,
                key = key,
                params = IvParameterSpec(iv),
            )
            Assert.assertTrue(actual.contentEquals(decrypted))
        }
    }

    @Test
    fun encryptTest() {
        runTest(timeout = 2.seconds) {
            val issuer = "${this::class.java.name}:encryptTest"
            val encrypted = mockBytes(issuer)
            val decrypted = mockBytes(issuer)
            check(!encrypted.contentEquals(decrypted))
            val iv = mockBytes(issuer)
            val key = MockSecretKey(issuer = issuer)
            val cipher = MockCipher(
                cipherSpi = MockBaseBlockCipher(
                    values = listOf(
                        MockBaseBlockCipher.DataSet(
                            key = key,
                            encrypted = encrypted,
                            decrypted = decrypted,
                            iv = iv,
                        ),
                    ),
                ),
            )
            val provider: CipherProvider = FinalCipherProvider(cipher)
            val actual = provider.encrypt(
                decrypted = decrypted,
                key = key,
                params = IvParameterSpec(iv),
            )
            Assert.assertTrue(actual.contentEquals(encrypted))
        }
    }
}
