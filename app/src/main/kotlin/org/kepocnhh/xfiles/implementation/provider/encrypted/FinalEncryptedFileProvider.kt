package org.kepocnhh.xfiles.implementation.provider.encrypted

import android.content.Context
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKeys
import org.kepocnhh.xfiles.foundation.provider.encrypted.EncryptedFileProvider
import java.io.File

internal class FinalEncryptedFileProvider(
    context: Context,
    private val file: File,
) : EncryptedFileProvider {
    private val encrypted = EncryptedFile.Builder(
        file,
        context,
        MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
        EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB,
    ).build()

    override fun exists(): Boolean {
        if (file.isDirectory) TODO()
        return file.exists()
    }

    override fun delete() {
        file.deleteRecursively()
    }

    override fun writeText(text: String) {
        encrypted.openFileOutput().use {
            it.writer().write(text)
        }
    }

    override fun readText(): String {
        return encrypted.openFileInput().use {
            it.reader().readText()
        }
    }
}
