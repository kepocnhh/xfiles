package org.kepocnhh.xfiles.provider

import org.kepocnhh.xfiles.entity.AsymmetricKey
import org.kepocnhh.xfiles.entity.DataBase
import org.kepocnhh.xfiles.entity.KeyMeta

internal interface Serializer {
    fun serialize(value: KeyMeta): ByteArray
    fun toKeyMeta(bytes: ByteArray): KeyMeta
    fun serialize(value: DataBase): ByteArray
    fun toDataBase(bytes: ByteArray): DataBase
    fun serialize(value: AsymmetricKey): ByteArray
    fun toAsymmetricKey(bytes: ByteArray): AsymmetricKey
}
