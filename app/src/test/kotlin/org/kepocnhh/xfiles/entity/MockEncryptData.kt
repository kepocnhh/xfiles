package org.kepocnhh.xfiles.entity

import org.kepocnhh.xfiles.provider.Encrypt

internal fun mockEncryptData(
    encrypted: ByteArray = "mock:encrypt:data:encrypted".toByteArray(),
    iv: ByteArray = "mock:encrypt:data:iv".toByteArray(),
): Encrypt.Data {
    return Encrypt.Data(
        encrypted = encrypted,
        iv = iv,
    )
}

internal fun mockEncryptData(issuer: String): Encrypt.Data {
    return mockEncryptData(
        encrypted = "$issuer:mock:encrypt:data:encrypted".toByteArray(),
        iv = "$issuer:mock:encrypt:data:iv".toByteArray(),
    )
}
