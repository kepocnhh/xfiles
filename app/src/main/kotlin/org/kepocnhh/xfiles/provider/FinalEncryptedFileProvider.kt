package org.kepocnhh.xfiles.provider

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKeys
import org.kepocnhh.xfiles.BuildConfig
import java.io.File
import java.io.InputStream

internal class FinalEncryptedFileProvider(
    private val context: Context,
) : EncryptedFileProvider {
    private fun File.encrypted(): EncryptedFile {
        return EncryptedFile.Builder(
            this,
            context,
            MasterKeys.getOrCreate(getSpec()),
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB,
        ).build()
    }

    override fun exists(pathname: String): Boolean {
        return context.filesDir.resolve(pathname).exists()
    }

    override fun delete(pathname: String) {
        context.filesDir.resolve(pathname).delete()
    }

    override fun openInput(pathname: String): InputStream {
        return context.filesDir.resolve(pathname).encrypted().openFileInput()
    }

    override fun writeBytes(pathname: String, bytes: ByteArray) {
        val file = context.filesDir.resolve(pathname)
        file.delete()
        file.encrypted().openFileOutput().use {
            it.write(bytes)
        }
    }

    companion object {
        private const val KEY_ALIAS = BuildConfig.APPLICATION_ID + ":encrypted:files"
        private const val KEY_SIZE = 256

        private fun getSpec(): KeyGenParameterSpec {
            val purposes = KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            val blocks = KeyProperties.BLOCK_MODE_GCM
//            val blocks = KeyProperties.BLOCK_MODE_CBC
            val paddings = KeyProperties.ENCRYPTION_PADDING_NONE
//            val paddings = KeyProperties.ENCRYPTION_PADDING_PKCS7
            return KeyGenParameterSpec
                .Builder(KEY_ALIAS, purposes)
                .setBlockModes(blocks)
                .setEncryptionPaddings(paddings)
                .setKeySize(KEY_SIZE)
                .build()
        }
    }
}
