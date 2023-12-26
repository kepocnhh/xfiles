package org.kepocnhh.xfiles.entity

internal class BiometricMeta(
    val password: ByteArray,
    val iv: ByteArray,
) {
    override fun equals(other: Any?): Boolean {
        return when (other) {
            is BiometricMeta -> password.contentEquals(other.password) &&
                    iv.contentEquals(other.iv)
            else -> false
        }
    }

    override fun hashCode(): Int {
        return password.contentHashCode() + iv.contentHashCode()
    }
}
