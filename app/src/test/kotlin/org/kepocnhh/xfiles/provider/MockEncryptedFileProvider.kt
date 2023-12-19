package org.kepocnhh.xfiles.provider

import java.io.ByteArrayInputStream
import java.io.InputStream

internal class MockEncryptedFileProvider(
    private val exists: Set<String> = emptySet(),
    private val inputs: Map<String, ByteArray> = emptyMap(),
) : EncryptedFileProvider {
    override fun exists(pathname: String): Boolean {
        return exists.contains(pathname)
    }

    override fun delete(pathname: String) {
        TODO("Not yet implemented: delete")
    }

    override fun openInput(pathname: String): InputStream {
        val bytes = inputs[pathname] ?: error("No input by $pathname!")
        return ByteArrayInputStream(bytes)
    }

    override fun writeBytes(pathname: String, bytes: ByteArray) {
        TODO("Not yet implemented: writeBytes")
    }
}
