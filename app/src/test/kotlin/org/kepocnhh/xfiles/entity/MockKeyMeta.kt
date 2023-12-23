package org.kepocnhh.xfiles.entity

internal fun mockKeyMeta(
    salt: ByteArray = "foo:salt".toByteArray(),
    ivDB: ByteArray = "foo:ivDB".toByteArray(),
    ivPrivate: ByteArray = "foo:ivPrivate".toByteArray(),
): KeyMeta {
    return KeyMeta(
        salt = salt,
        ivDB = ivDB,
        ivPrivate = ivPrivate,
    )
}

internal fun mockKeyMeta(
    issuer: String,
): KeyMeta {
    return mockKeyMeta(
        salt = "$issuer:salt".toByteArray(),
        ivDB = "$issuer:ivDB".toByteArray(),
        ivPrivate = "$issuer:ivPrivate".toByteArray(),
    )
}
