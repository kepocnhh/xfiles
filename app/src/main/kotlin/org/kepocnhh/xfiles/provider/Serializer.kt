package org.kepocnhh.xfiles.provider

import org.kepocnhh.xfiles.entity.KeyMeta

internal interface Serializer {
    fun serialize(value: KeyMeta): ByteArray
    fun toKeyMeta(bytes: ByteArray): KeyMeta
    fun toSecretTitles(bytes: ByteArray): Map<String, String>
    fun toSecretValues(bytes: ByteArray): Map<String, String>
}
