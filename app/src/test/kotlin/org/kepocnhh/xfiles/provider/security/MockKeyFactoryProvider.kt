package org.kepocnhh.xfiles.provider.security

import org.kepocnhh.xfiles.entity.MockPrivateKey
import java.security.PrivateKey
import java.security.PublicKey

internal class MockKeyFactoryProvider(
    private val privateKey: PrivateKey = MockPrivateKey(),
) : KeyFactoryProvider {
    override fun generatePublic(bytes: ByteArray): PublicKey {
        TODO("Not yet implemented: generatePublic")
    }

    override fun generatePrivate(bytes: ByteArray): PrivateKey {
        return privateKey
    }
}
