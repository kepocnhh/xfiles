package org.kepocnhh.xfiles.provider.security

import java.security.PrivateKey
import java.security.PublicKey
import java.security.SecureRandom
import java.security.Signature

internal class FinalSignatureProvider(
    private val delegate: Signature,
) : SignatureProvider {
    override fun sign(key: PrivateKey, random: SecureRandom, decrypted: ByteArray): ByteArray {
        delegate.initSign(key, random)
        delegate.update(decrypted)
        return delegate.sign()
    }

    override fun verify(key: PublicKey, decrypted: ByteArray, sig: ByteArray): Boolean {
        delegate.initVerify(key)
        delegate.update(decrypted)
        return delegate.verify(sig)
    }
}
