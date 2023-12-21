package org.kepocnhh.xfiles.provider.security

import java.security.PrivateKey
import java.security.PublicKey
import java.security.SecureRandom

internal class MockSignatureProvider(
    private val dataSets: List<DataSet> = emptyList(),
) : SignatureProvider {
    class DataSet(
        val decrypted: ByteArray,
        val sig: ByteArray,
        val privateKey: PrivateKey,
        val publicKey: PublicKey,
    )

    override fun sign(key: PrivateKey, random: SecureRandom, decrypted: ByteArray): ByteArray {
        for (it in dataSets) {
            if (it.privateKey != key) continue
            if (it.decrypted.contentEquals(decrypted)) return it.sig
        }
        error("No signature!")
    }

    override fun verify(key: PublicKey, decrypted: ByteArray, sig: ByteArray): Boolean {
        for (it in dataSets) {
            if (it.publicKey != key) continue
            if (it.decrypted.contentEquals(decrypted)) return it.sig.contentEquals(sig)
        }
        error("No signature!")
    }
}
