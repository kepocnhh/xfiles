package org.kepocnhh.xfiles.provider

import android.content.Context
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKeys
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

internal class FinalEncryptedFileProvider(
    private val context: Context,
) : EncryptedFileProvider {
    private fun File.encrypted(): EncryptedFile {
        return EncryptedFile.Builder(
            this,
            context,
            MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB,
        ).build()
    }

    override fun exists(pathname: String): Boolean {
        return context.filesDir.resolve(pathname).exists()
    }

    override fun delete(pathname: String) {
        check(context.filesDir.resolve(pathname).delete())
    }

    override fun openInput(pathname: String): FileInputStream {
        return context.filesDir.resolve(pathname).encrypted().openFileInput()
    }

    override fun writeBytes(pathname: String, bytes: ByteArray) {
        val file = context.filesDir.resolve(pathname)
        file.delete()
        file.encrypted().openFileOutput().use {
            it.write(bytes)
        }
    }
}
