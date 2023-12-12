package org.kepocnhh.xfiles.provider.data

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import org.kepocnhh.xfiles.BuildConfig
import java.util.UUID

internal class FinalEncryptedLocalDataProvider(context: Context) : EncryptedLocalDataProvider {
    private val preferences = EncryptedSharedPreferences.create(
        BuildConfig.APPLICATION_ID + "_encrypted_shared_preferences",
        MasterKeys.getOrCreate(getSpec()),
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )

    override var appId: UUID?
        get() {
            return preferences.getString("appId", null)?.let(UUID::fromString)
        }
        set(value) {
            preferences
                .edit()
                .also { editor ->
                    if (value == null) {
                        editor.remove("appId")
                    } else {
                        editor.putString("appId", value.toString())
                    }
                }
                .commit()
        }

    override var databaseId: UUID?
        get() {
            return preferences.getString("databaseId", null)?.let(UUID::fromString)
        }
        set(value) {
            preferences
                .edit()
                .also { editor ->
                    if (value == null) {
                        editor.remove("databaseId")
                    } else {
                        editor.putString("databaseId", value.toString())
                    }
                }
                .commit()
        }

    companion object {
        private const val KEY_ALIAS = BuildConfig.APPLICATION_ID + ":encrypted:shared:preferences"
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
