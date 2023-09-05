package org.kepocnhh.xfiles.entity

internal class KeyMeta(
    val salt: ByteArray,
    val iv: ByteArray,
    val iterations: Int,
    val bits: Int,
)
