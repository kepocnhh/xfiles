package org.kepocnhh.xfiles.implementation.provider.encrypted

import android.content.Context
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKeys
import org.kepocnhh.xfiles.foundation.provider.encrypted.EncryptedFileProvider
import java.io.File

internal class FinalEncryptedFileProvider(
    private val context: Context,
    private val file: File,
) : EncryptedFileProvider {
    init {
        if (file.exists()) {
            check(!file.isDirectory)
        }
    }

//    private val encrypted = EncryptedFile.Builder(
//        file,
//        context,
//        MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
//        EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB,
//    ).build()

    override fun exists(): Boolean {
        return file.exists()
    }

    override fun delete() {
        check(file.delete())
    }

    override fun writeText(text: String) {
        encrypted().openFileOutput().use {
            it.writer().write(text)
        }
    }

    override fun readText(): String {
        return encrypted().openFileInput().use {
            it.reader().readText()
        }
    }

    private fun encrypted(): EncryptedFile {
        return EncryptedFile.Builder(
            file,
            context,
            MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB,
        ).build()
    }
}
