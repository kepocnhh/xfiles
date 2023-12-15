package org.kepocnhh.xfiles.provider

import java.io.FileInputStream

internal class MockEncryptedFileProvider(
    private val exists: Set<String> = emptySet(),
) : EncryptedFileProvider {
    override fun exists(pathname: String): Boolean {
        return exists.contains(pathname)
    }

    override fun delete(pathname: String) {
        TODO("Not yet implemented: delete")
    }

    override fun openInput(pathname: String): FileInputStream {
        TODO("Not yet implemented: openInput")
    }

    override fun writeBytes(pathname: String, bytes: ByteArray) {
        TODO("Not yet implemented: writeBytes")
    }
}
