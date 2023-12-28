package org.kepocnhh.xfiles.provider.security

import org.kepocnhh.xfiles.entity.MockPrivateKey
import org.kepocnhh.xfiles.entity.MockPublicKey
import java.security.PrivateKey
import java.security.PublicKey

internal class MockKeyFactoryProvider(
    private val privateKey: PrivateKey = MockPrivateKey(),
    private val publicKey: PublicKey = MockPublicKey(),
) : KeyFactoryProvider {
    override fun generatePublic(bytes: ByteArray): PublicKey {
        return publicKey
    }

    override fun generatePrivate(bytes: ByteArray): PrivateKey {
        return privateKey
    }
}
