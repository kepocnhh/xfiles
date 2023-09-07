package org.kepocnhh.xfiles.entity

internal class KeyMeta(
    val salt: ByteArray,
    val ivDB: ByteArray,
    val ivPrivate: ByteArray,
)
