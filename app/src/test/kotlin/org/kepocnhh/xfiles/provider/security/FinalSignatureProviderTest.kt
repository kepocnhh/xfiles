package org.kepocnhh.xfiles.provider.security

import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import org.kepocnhh.xfiles.entity.MockPrivateKey
import org.kepocnhh.xfiles.entity.MockSignature
import org.kepocnhh.xfiles.mockBytes
import java.security.Signature
import kotlin.time.Duration.Companion.seconds

internal class FinalSignatureProviderTest {
    @Test
    fun signTest() {
        runTest(timeout = 2.seconds) {
            val issuer = "${this::class.java.name}:signTest"
            val privateKey = MockPrivateKey(issuer = issuer)
            val decrypted = mockBytes(issuer)
            val sign = mockBytes(issuer)
            val signature: Signature = MockSignature(
                values = listOf(
                    MockSignature.DataSet(
                        sign = sign,
                        decrypted = decrypted,
                        privateKey = privateKey,
                    ),
                ),
            )
            val provider: SignatureProvider = FinalSignatureProvider(signature)
            val actual = provider.sign(
                key = privateKey,
                random = MockSecureRandom(),
                decrypted = decrypted,
            )
            Assert.assertTrue(actual.contentEquals(sign))
        }
    }

    @Test
    fun verifyTest() {
        runTest(timeout = 2.seconds) {
            val issuer = "${this::class.java.name}:verifyTest"
            TODO(issuer)
        }
    }
}
