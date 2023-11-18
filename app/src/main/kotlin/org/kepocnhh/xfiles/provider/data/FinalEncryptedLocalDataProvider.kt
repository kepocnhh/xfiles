package org.kepocnhh.xfiles.provider.data

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import org.kepocnhh.xfiles.BuildConfig
import java.security.KeyStore
import java.util.UUID
import javax.crypto.KeyGenerator

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
                .also {
                    if (value == null) {
                        it.remove("appId")
                    } else {
                        it.putString("appId", value.toString())
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
                .also {
                    if (value == null) {
                        it.remove("databaseId")
                    } else {
                        it.putString("databaseId", value.toString())
                    }
                }
                .commit()
        }

    companion object {
        private const val KEY_ALIAS = BuildConfig.APPLICATION_ID + ":encrypted:shared:preferences"
        private const val KEY_SIZE = 256

        private fun deleteSecretKey() {
            val keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)
            try {
                keyStore.deleteEntry(KEY_ALIAS)
            } catch (e: Throwable) {
                println("delete entry \"$KEY_ALIAS\" error: $e")
            }
        }

        private fun getOrCreate(keyAlias: String): String {
//            deleteSecretKey() // todo
            val keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)
            if (keyStore.containsAlias(keyAlias)) return keyAlias
            val algorithm = KeyProperties.KEY_ALGORITHM_AES
            val keyGenerator = KeyGenerator.getInstance(algorithm, keyStore.provider)
            keyGenerator.init(getSpec())
            keyGenerator.generateKey()
            return keyAlias
        }

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
