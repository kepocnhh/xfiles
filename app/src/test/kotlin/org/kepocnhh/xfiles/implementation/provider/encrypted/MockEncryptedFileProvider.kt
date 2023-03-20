package org.kepocnhh.xfiles.implementation.provider.encrypted

import org.kepocnhh.xfiles.foundation.provider.encrypted.EncryptedFileProvider

internal class MockEncryptedFileProvider : EncryptedFileProvider {
    override fun exists(): Boolean {
        return false
    }

    override fun delete() {
        TODO("Not yet implemented: delete")
    }

    override fun writeText(text: String) {
        TODO("Not yet implemented: writeText")
    }

    override fun readText(): String {
        TODO("Not yet implemented: readText")
    }
}
