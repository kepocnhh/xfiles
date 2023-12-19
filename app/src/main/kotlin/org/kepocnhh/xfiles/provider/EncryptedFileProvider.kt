package org.kepocnhh.xfiles.provider

import java.io.InputStream

internal interface EncryptedFileProvider {
    fun exists(pathname: String): Boolean
    fun delete(pathname: String)
    fun openInput(pathname: String): InputStream
    fun writeBytes(pathname: String, bytes: ByteArray)
}

internal fun EncryptedFileProvider.readText(pathname: String): String {
    return openInput(pathname).use { it.reader().readText() }
}

internal fun EncryptedFileProvider.readBytes(pathname: String): ByteArray {
    return openInput(pathname).use { it.readBytes() }
}
