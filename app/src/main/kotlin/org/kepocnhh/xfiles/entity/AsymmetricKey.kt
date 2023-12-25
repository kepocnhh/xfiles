package org.kepocnhh.xfiles.entity

internal class AsymmetricKey(
    val publicDecrypted: ByteArray,
    val privateEncrypted: ByteArray,
) {
    override fun equals(other: Any?): Boolean {
        return when (other) {
            is AsymmetricKey -> publicDecrypted.contentEquals(other.publicDecrypted) &&
                    privateEncrypted.contentEquals(other.privateEncrypted)
            else -> false
        }
    }

    override fun hashCode(): Int {
        return publicDecrypted.contentHashCode() + privateEncrypted.contentHashCode()
    }
}
