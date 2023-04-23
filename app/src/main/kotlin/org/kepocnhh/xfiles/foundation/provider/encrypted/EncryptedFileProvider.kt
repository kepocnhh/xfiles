package org.kepocnhh.xfiles.foundation.provider.encrypted

internal interface EncryptedFileProvider {
    fun exists(): Boolean
    fun delete()
    fun writeText(text: String)
    fun readText(): String
}
