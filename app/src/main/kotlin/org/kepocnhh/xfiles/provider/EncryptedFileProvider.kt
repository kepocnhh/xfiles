package org.kepocnhh.xfiles.provider

import java.io.FileInputStream

internal interface EncryptedFileProvider {
    fun exists(pathname: String): Boolean
    fun delete(pathname: String)
    fun openInput(pathname: String): FileInputStream
    fun writeBytes(pathname: String, bytes: ByteArray)
}

internal fun EncryptedFileProvider.readText(pathname: String): String {
    return openInput(pathname).use { it.reader().readText() }
}
