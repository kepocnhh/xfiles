package org.kepocnhh.xfiles.provider

import android.content.Context
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKeys
import java.io.File

internal class FinalEncryptedFileProvider(
    context: Context,
    private val file: File,
) : EncryptedFileProvider {
    init {
        if (file.exists()) {
            check(!file.isDirectory)
        }
    }

    private val encrypted = EncryptedFile.Builder(
        file,
        context,
        MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
        EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB,
    ).build()

    override fun exists(): Boolean {
        return file.exists()
    }

    override fun delete() {
        check(file.delete())
    }

    override fun writeText(text: String) {
        file.delete() // todo ?
        encrypted.openFileOutput().use {
//            it.writer().write(text) // it does not work
            it.write(text.toByteArray())
        }
    }

    override fun readText(): String {
        return encrypted.openFileInput().use {
            it.reader().readText()
        }
    }
}
