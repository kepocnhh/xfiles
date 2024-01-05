package org.kepocnhh.xfiles.entity

internal fun mockAsymmetricKey(
    publicKeyDecrypted: ByteArray = "AsymmetricKey:public:key:decrypted".toByteArray(),
    privateKeyEncrypted: ByteArray = "AsymmetricKey:private:key:encrypted".toByteArray(),
): AsymmetricKey {
    return AsymmetricKey(
        publicKeyDecrypted = publicKeyDecrypted,
        privateKeyEncrypted = privateKeyEncrypted,
    )
}

internal fun mockAsymmetricKey(issuer: String): AsymmetricKey {
    return mockAsymmetricKey(
        publicKeyDecrypted = "$issuer:public:key:decrypted".toByteArray(),
        privateKeyEncrypted = "$issuer:private:key:encrypted".toByteArray(),
    )
}
