package org.kepocnhh.xfiles.provider

import org.kepocnhh.xfiles.entity.KeyMeta

internal class MockSerializer(
    private val values: Map<Any, ByteArray> = emptyMap(),
) : Serializer {
    override fun serialize(value: KeyMeta): ByteArray {
        return values[value] ?: error("No bytes by $value!")
    }

    override fun toKeyMeta(bytes: ByteArray): KeyMeta {
        for ((value, expected) in values) {
            if (expected.contentEquals(bytes)) {
                check(value is KeyMeta)
                return value
            }
        }
        error("No value!")
    }

    override fun toSecrets(bytes: ByteArray): Map<String, String> {
        for ((value, expected) in values) {
            if (expected.contentEquals(bytes)) {
                return value as Map<String, String>
            }
        }
        error("No value!")
    }
}
