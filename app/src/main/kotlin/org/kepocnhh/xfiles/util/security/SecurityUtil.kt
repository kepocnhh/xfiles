package org.kepocnhh.xfiles.util.security

import org.kepocnhh.xfiles.entity.SecuritySettings
import java.security.NoSuchProviderException
import java.security.Provider
import java.security.Security
import kotlin.math.pow

internal object SecurityUtil {
    fun requireProvider(name: String): Provider {
        return Security.getProviders().firstOrNull { it.name == name } ?: throw NoSuchProviderException("No such provider \"$name\"!")
    }

    fun getValue(settings: SecuritySettings.PBEIterations): Int {
        return when (settings) {
            SecuritySettings.PBEIterations.NUMBER_2_10 -> 2.0.pow(10).toInt()
            SecuritySettings.PBEIterations.NUMBER_2_16 -> 2.0.pow(16).toInt()
            SecuritySettings.PBEIterations.NUMBER_2_20 -> 2.0.pow(20).toInt()
        }
    }

    fun getValue(settings: SecuritySettings.AESKeyLength): Int {
        return when (settings) {
            SecuritySettings.AESKeyLength.BITS_256 -> 8 * 32 // 256 bits (32 octets)
        }
    }

    fun getBlockSize(settings: SecuritySettings.AESKeyLength): Int {
        return when (settings) {
            SecuritySettings.AESKeyLength.BITS_256 -> 16
        }
    }

    fun getValue(settings: SecuritySettings.DSAKeyLength): Int {
        return when (settings) {
            SecuritySettings.DSAKeyLength.BITS_1024_1 -> 1024 * 1
            SecuritySettings.DSAKeyLength.BITS_1024_2 -> 1024 * 2
            SecuritySettings.DSAKeyLength.BITS_1024_3 -> 1024 * 3
        }
    }
}
