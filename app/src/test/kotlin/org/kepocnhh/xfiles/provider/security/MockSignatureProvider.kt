package org.kepocnhh.xfiles.provider.security

import java.security.PrivateKey
import java.security.PublicKey
import java.security.SecureRandom

internal class MockSignatureProvider(
    private val signatures: Map<ByteArray, ByteArray> = emptyMap(),
) : SignatureProvider {
    override fun sign(key: PrivateKey, random: SecureRandom, decrypted: ByteArray): ByteArray {
        for ((d, s) in signatures) {
            if (d.contentEquals(decrypted)) return s
        }
        error("No signature!")
    }

    override fun verify(key: PublicKey, decrypted: ByteArray, sig: ByteArray): Boolean {
        for ((d, s) in signatures) {
            if (d.contentEquals(decrypted)) return s.contentEquals(sig)
        }
        error("No signature!")
    }
}
