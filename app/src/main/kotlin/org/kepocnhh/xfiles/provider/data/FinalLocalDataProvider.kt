package org.kepocnhh.xfiles.provider.data

import android.content.Context
import android.content.SharedPreferences
import org.kepocnhh.xfiles.BuildConfig
import org.kepocnhh.xfiles.entity.Defaults
import org.kepocnhh.xfiles.entity.SecurityService
import org.kepocnhh.xfiles.entity.SecurityServices
import org.kepocnhh.xfiles.entity.SecuritySettings
import org.kepocnhh.xfiles.module.app.ColorsType
import org.kepocnhh.xfiles.module.app.Language
import org.kepocnhh.xfiles.module.app.ThemeState

internal class FinalLocalDataProvider(
    context: Context,
    private val defaults: Defaults,
) : LocalDataProvider {
    private val preferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)

    override var themeState: ThemeState
        get() {
            return ThemeState(
                colorsType = preferences
                    .getString("colorsType", null)
                    ?.let(ColorsType::valueOf)
                    ?: defaults.themeState.colorsType,
                language = preferences
                    .getString("language", null)
                    ?.let(Language::valueOf)
                    ?: defaults.themeState.language,
            )
        }
        set(value) {
            preferences.edit()
                .putString("colorsType", value.colorsType.name)
                .putString("language", value.language.name)
                .commit()
        }

    private fun getSecurityService(key: String): SecurityService {
        return SecurityService(
            provider = preferences.getString("$key:provider", null)!!,
            algorithm = preferences.getString("$key:algorithm", null)!!,
        )
    }

    private fun SharedPreferences.Editor.put(key: String, service: SecurityService): SharedPreferences.Editor {
        return putString("$key:provider", service.provider).putString("$key:algorithm", service.algorithm)
    }

    private fun SharedPreferences.Editor.removeSecurityService(key: String): SharedPreferences.Editor {
        return remove("$key:provider").remove("$key:algorithm")
    }

    override var services: SecurityServices?
        get() {
            val exists = preferences.getBoolean("services", false)
            if (!exists) return null
            return SecurityServices(
                cipher = getSecurityService("cipher"),
                symmetric = getSecurityService("symmetric"),
                asymmetric = getSecurityService("asymmetric"),
                signature = getSecurityService("signature"),
                hash = getSecurityService("hash"),
                random = getSecurityService("random"),
            )
        }
        set(value) {
            if (value == null) {
                preferences.edit()
                    .putBoolean("services", false)
                    .removeSecurityService("cipher")
                    .removeSecurityService("symmetric")
                    .removeSecurityService("asymmetric")
                    .removeSecurityService("signature")
                    .removeSecurityService("hash")
                    .removeSecurityService("random")
                    .commit()
            } else {
                preferences.edit()
                    .putBoolean("services", true)
                    .put("cipher", value.cipher)
                    .put("symmetric", value.symmetric)
                    .put("asymmetric", value.asymmetric)
                    .put("signature", value.signature)
                    .put("hash", value.hash)
                    .put("random", value.random)
                    .commit()
            }
        }

    override var securitySettings: SecuritySettings
        get() {
            return SecuritySettings(
                aesKeyLength = preferences
                    .getString("aesKeyLength", null)
                    ?.let(SecuritySettings.AESKeyLength::valueOf)
                    ?: defaults.securitySettings.aesKeyLength,
                dsaKeyLength = preferences
                    .getString("dsaKeyLength", null)
                    ?.let(SecuritySettings.DSAKeyLength::valueOf)
                    ?: defaults.securitySettings.dsaKeyLength,
                pbeIterations = preferences
                    .getString("pbeIterations", null)
                    ?.let(SecuritySettings.PBEIterations::valueOf)
                    ?: defaults.securitySettings.pbeIterations,
            )
        }
        set(value) {
            preferences.edit()
                .putString("aesKeyLength", value.aesKeyLength.name)
                .putString("dsaKeyLength", value.dsaKeyLength.name)
                .putString("pbeIterations", value.pbeIterations.name)
                .commit()
        }
}
