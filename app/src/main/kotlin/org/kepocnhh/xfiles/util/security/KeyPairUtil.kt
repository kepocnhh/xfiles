package org.kepocnhh.xfiles.util.security

import java.security.KeyFactory
import java.security.KeyPair
import java.security.spec.KeySpec
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec

internal fun KeyFactory.generateKeyPair(
    publicKeySpec: KeySpec,
    privateKeySpec: KeySpec,
): KeyPair {
    return KeyPair(
        generatePublic(publicKeySpec),
        generatePrivate(privateKeySpec),
    )
}

internal fun KeyFactory.generateKeyPair(
    public: ByteArray,
    private: ByteArray,
): KeyPair {
    return generateKeyPair(
        publicKeySpec = X509EncodedKeySpec(public),
        privateKeySpec = PKCS8EncodedKeySpec(private),
    )
}
