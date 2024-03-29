package org.kepocnhh.xfiles.provider

import org.kepocnhh.xfiles.entity.AsymmetricKey
import org.kepocnhh.xfiles.entity.BiometricMeta
import org.kepocnhh.xfiles.entity.DataBase
import org.kepocnhh.xfiles.entity.KeyMeta

@Suppress("StringLiteralDuplication")
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

    override fun serialize(value: DataBase): ByteArray {
        return values[value] ?: error("No bytes by $value!")
    }

    override fun toDataBase(bytes: ByteArray): DataBase {
        for ((value, expected) in values) {
            if (expected.contentEquals(bytes)) {
                check(value is DataBase)
                return value
            }
        }
        error("No value(DataBase)!")
    }

    override fun serialize(value: AsymmetricKey): ByteArray {
        return values[value] ?: error("No bytes by $value!")
    }

    override fun toAsymmetricKey(bytes: ByteArray): AsymmetricKey {
        for ((value, expected) in values) {
            if (expected.contentEquals(bytes)) {
                check(value is AsymmetricKey)
                return value
            }
        }
        error("No value(AsymmetricKey)!")
    }

    override fun serialize(value: BiometricMeta): ByteArray {
        return values[value] ?: error("No bytes by $value!")
    }

    override fun toBiometricMeta(bytes: ByteArray): BiometricMeta {
        for ((value, expected) in values) {
            if (expected.contentEquals(bytes)) {
                check(value is BiometricMeta)
                return value
            }
        }
        error("No value(BiometricMeta)!")
    }
}
