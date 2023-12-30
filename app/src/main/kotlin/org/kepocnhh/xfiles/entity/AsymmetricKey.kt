package org.kepocnhh.xfiles.entity

internal class AsymmetricKey(
    val publicKeyDecrypted: ByteArray,
    val privateKeyEncrypted: ByteArray,
) {
    override fun equals(other: Any?): Boolean {
        return when (other) {
            is AsymmetricKey -> publicKeyDecrypted.contentEquals(other.publicKeyDecrypted) &&
                    privateKeyEncrypted.contentEquals(other.privateKeyEncrypted)
            else -> false
        }
    }

    override fun hashCode(): Int {
        return publicKeyDecrypted.contentHashCode() + privateKeyEncrypted.contentHashCode()
    }
}
