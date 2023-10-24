package org.kepocnhh.xfiles.provider

import java.io.FileInputStream

internal class MockEncryptedFileProvider : EncryptedFileProvider {
    override fun exists(pathname: String): Boolean {
        TODO("Not yet implemented: exists")
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
