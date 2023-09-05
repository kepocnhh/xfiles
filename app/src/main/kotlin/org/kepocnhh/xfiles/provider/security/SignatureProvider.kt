package org.kepocnhh.xfiles.provider.security

import java.security.PrivateKey
import java.security.PublicKey

internal interface SignatureProvider {
    fun sign(key: PrivateKey, decrypted: ByteArray): ByteArray
    fun verify(key: PublicKey, decrypted: ByteArray, sig: ByteArray): Boolean
}
