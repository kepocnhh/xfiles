package org.kepocnhh.xfiles.provider.security

import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import org.kepocnhh.xfiles.entity.MockPrivateKey
import org.kepocnhh.xfiles.entity.MockPublicKey
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
            val sig = mockBytes(issuer)
            val signature: Signature = MockSignature(
                values = listOf(
                    MockSignature.DataSet(
                        sig = sig,
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
            Assert.assertTrue(actual.contentEquals(sig))
        }
    }

    @Test
    fun verifyTest() {
        runTest(timeout = 2.seconds) {
            val issuer = "${this::class.java.name}:verifyTest"
            val decrypted = mockBytes(issuer)
            val sig = mockBytes(issuer)
            val publicKey = MockPublicKey(issuer = issuer)
            val signature: Signature = MockSignature(
                values = listOf(
                    MockSignature.DataSet(
                        sig = sig,
                        decrypted = decrypted,
                        publicKey = publicKey,
                    ),
                ),
            )
            val provider: SignatureProvider = FinalSignatureProvider(signature)
            val result = provider.verify(
                key = publicKey,
                decrypted = decrypted,
                sig = sig,
            )
            Assert.assertTrue(result)
        }
    }

    @Test
    fun verifyErrorTest() {
        runTest(timeout = 2.seconds) {
            val issuer = "${this::class.java.name}:verifyErrorTest"
            val decrypted = mockBytes(issuer)
            val sig = mockBytes(issuer)
            val publicKey = MockPublicKey(issuer = issuer)
            val signature: Signature = MockSignature(
                values = listOf(
                    MockSignature.DataSet(
                        sig = sig,
                        decrypted = decrypted,
                        publicKey = publicKey,
                    ),
                ),
            )
            val provider: SignatureProvider = FinalSignatureProvider(signature)
            val wrongSig = mockBytes(issuer)
            check(!sig.contentEquals(wrongSig))
            val result = provider.verify(
                key = publicKey,
                decrypted = decrypted,
                sig = wrongSig,
            )
            Assert.assertFalse(result)
        }
    }
}
