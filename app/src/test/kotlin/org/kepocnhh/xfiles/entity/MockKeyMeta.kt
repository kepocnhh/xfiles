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

internal fun mockKeyMeta(
    salt: String,
    ivDB: String,
    ivPrivate: String,
): KeyMeta {
    return mockKeyMeta(
        salt = salt.toByteArray(),
        ivDB = ivDB.toByteArray(),
        ivPrivate = ivPrivate.toByteArray(),
    )
}

internal fun mockKeyMeta(
    saltSize: Int,
    ivDBSize: Int,
    ivPrivateSize: Int,
): KeyMeta {
    return mockKeyMeta(
        salt = ByteArray(saltSize) { saltSize.toByte() },
        ivDB = ByteArray(ivDBSize) { ivDBSize.toByte() },
        ivPrivate = ByteArray(ivPrivateSize) { ivPrivateSize.toByte() },
    )
}
