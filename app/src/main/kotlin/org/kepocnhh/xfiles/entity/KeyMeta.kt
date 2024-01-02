package org.kepocnhh.xfiles.entity

internal class KeyMeta(
    val salt: ByteArray,
    val ivDB: ByteArray,
    val ivPrivate: ByteArray,
) {
    override fun toString(): String {
        return "KeyMeta(${salt.size}/${ivDB.size}/${ivPrivate.size})"
    }

    override fun equals(other: Any?): Boolean {
        return when (other) {
            !is KeyMeta -> false
            else -> {
                return salt.contentEquals(other.salt) &&
                    ivDB.contentEquals(other.ivDB) &&
                    ivPrivate.contentEquals(other.ivPrivate)
            }
        }
    }

    override fun hashCode(): Int {
        return salt.contentHashCode() +
            ivDB.contentHashCode() +
            ivPrivate.contentHashCode()
    }
}
