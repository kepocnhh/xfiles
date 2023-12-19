package org.kepocnhh.xfiles.entity

internal fun mockKeyMeta(
    salt: ByteArray = "foo".toByteArray(),
    ivDB: ByteArray = "bar".toByteArray(),
    ivPrivate: ByteArray = "baz".toByteArray(),
): KeyMeta {
    return KeyMeta(
        salt = salt,
        ivDB = ivDB,
        ivPrivate = ivPrivate,
    )
}
