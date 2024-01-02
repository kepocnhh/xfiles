package org.kepocnhh.xfiles.provider

internal class MockEncrypt(
    private val values: List<Pair<ByteArray, Encrypt.Data>> = emptyList(),
) : Encrypt {
    override fun doFinal(decrypted: ByteArray): Encrypt.Data {
        for ((d, data) in values) {
            if (decrypted.contentEquals(d)) return data
        }
        error("No encrypted data!")
    }
}

internal class MockDecrypt(
    private val values: List<Pair<ByteArray, ByteArray>> = emptyList(),
) : Decrypt {
    override fun doFinal(encrypted: ByteArray): ByteArray {
        for ((e, decrypted) in values) {
            if (encrypted.contentEquals(e)) return decrypted
        }
        error("No decrypted!")
    }
}
