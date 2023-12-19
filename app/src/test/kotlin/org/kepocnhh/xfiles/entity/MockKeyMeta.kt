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
