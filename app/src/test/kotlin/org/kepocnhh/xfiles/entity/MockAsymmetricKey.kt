package org.kepocnhh.xfiles.entity

internal fun mockAsymmetricKey(
    publicDecrypted: ByteArray = "foo:publicDecrypted".toByteArray(),
    privateEncrypted: ByteArray = "bar:privateEncrypted".toByteArray(),
): AsymmetricKey {
    return AsymmetricKey(
        publicDecrypted = publicDecrypted,
        privateEncrypted = privateEncrypted,
    )
}
