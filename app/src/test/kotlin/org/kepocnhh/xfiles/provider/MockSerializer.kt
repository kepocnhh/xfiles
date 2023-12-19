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
        error("No value(KeyMeta)!")
    }

    override fun toSecretTitles(bytes: ByteArray): Map<String, String> {
        for ((value, expected) in values) {
            if (expected.contentEquals(bytes)) {
                val secrets = value as? Map<String, Pair<String, String>> ?: error("Type error!")
                return secrets.mapValues { (_, pair) ->
                    val (title, _) = pair
                    title
                }
            }
        }
        error("No value(Secret/Titles)!")
    }

    override fun toSecretValues(bytes: ByteArray): Map<String, String> {
        for ((value, expected) in values) {
            if (expected.contentEquals(bytes)) {
                val secrets = value as? Map<String, Pair<String, String>> ?: error("Type error!")
                return secrets.mapValues { (_, pair) ->
                    val (_, secret) = pair
                    secret
                }
            }
        }
        error("No value(Secret/Values)!")
    }
}
