package org.kepocnhh.xfiles.provider

import org.kepocnhh.xfiles.entity.KeyMeta

internal interface Serializer {
    fun serialize(value: KeyMeta): ByteArray
    fun toKeyMeta(bytes: ByteArray): KeyMeta
    fun toSecrets(bytes: ByteArray): Map<String, String>
}
