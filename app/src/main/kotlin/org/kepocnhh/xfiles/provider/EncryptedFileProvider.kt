package org.kepocnhh.xfiles.provider

internal interface EncryptedFileProvider {
    fun exists(): Boolean
    fun delete()
    fun writeText(text: String)
    fun readText(): String
}
