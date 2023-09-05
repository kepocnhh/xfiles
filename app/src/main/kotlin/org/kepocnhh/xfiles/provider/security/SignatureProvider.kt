package org.kepocnhh.xfiles.provider.security

import java.security.PrivateKey
import java.security.PublicKey
import java.security.SecureRandom

internal interface SignatureProvider {
    fun sign(key: PrivateKey, random: SecureRandom, decrypted: ByteArray): ByteArray
    fun verify(key: PublicKey, decrypted: ByteArray, sig: ByteArray): Boolean
}
