package org.kepocnhh.xfiles.provider

import java.io.InputStream

internal interface EncryptedFileProvider {
    fun exists(pathname: String): Boolean
    fun delete(pathname: String)
    fun openInput(pathname: String): InputStream // todo only read bytes
    fun writeBytes(pathname: String, bytes: ByteArray)
}

@Deprecated(message = "Read bytes only!")
internal fun EncryptedFileProvider.readText(pathname: String): String {
    return openInput(pathname).use { it.reader().readText() }
}

internal fun EncryptedFileProvider.readBytes(pathname: String): ByteArray {
    return openInput(pathname).use { it.readBytes() }
}
