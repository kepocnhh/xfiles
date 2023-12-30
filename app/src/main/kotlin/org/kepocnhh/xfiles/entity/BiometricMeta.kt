package org.kepocnhh.xfiles.entity

internal class BiometricMeta(
    val passwordEncrypted: ByteArray,
    val iv: ByteArray,
) {
    override fun equals(other: Any?): Boolean {
        return when (other) {
            is BiometricMeta -> passwordEncrypted.contentEquals(other.passwordEncrypted) &&
                    iv.contentEquals(other.iv)
            else -> false
        }
    }

    override fun hashCode(): Int {
        return passwordEncrypted.contentHashCode() + iv.contentHashCode()
    }
}
