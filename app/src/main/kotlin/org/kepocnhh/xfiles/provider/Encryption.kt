package org.kepocnhh.xfiles.provider

internal interface Encrypt {
    class Data(
        val encrypted: ByteArray,
        val iv: ByteArray,
    )

    fun doFinal(decrypted: ByteArray): Data
}

internal interface Decrypt {
    /**
     * @return decrypted
     */
    fun doFinal(encrypted: ByteArray): ByteArray
}
