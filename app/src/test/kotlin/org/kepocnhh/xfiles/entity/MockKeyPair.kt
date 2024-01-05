package org.kepocnhh.xfiles.entity

import java.security.KeyPair
import java.security.PrivateKey
import java.security.PublicKey

internal fun mockKeyPair(
    publicKey: PublicKey = MockPublicKey(encoded = "foo:public:key".toByteArray()),
    privateKey: PrivateKey = MockPrivateKey(encoded = "foo:private:key".toByteArray()),
): KeyPair {
    return KeyPair(publicKey, privateKey)
}
