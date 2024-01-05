package org.kepocnhh.xfiles.provider

internal interface EncryptedFileProvider {
    fun exists(pathname: String): Boolean
    fun delete(pathname: String)
    fun readBytes(pathname: String): ByteArray
    fun writeBytes(pathname: String, bytes: ByteArray)
}
