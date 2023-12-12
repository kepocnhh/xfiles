package org.kepocnhh.xfiles.module.enter.settings

import org.kepocnhh.xfiles.entity.SecuritySettings
import kotlin.math.pow

internal fun getNumber(value: SecuritySettings.AESKeyLength): Int {
    return when (value) {
        SecuritySettings.AESKeyLength.BITS_256 -> 256
    }
}

internal fun getNumber(value: SecuritySettings.DSAKeyLength): Int {
    return when (value) {
        SecuritySettings.DSAKeyLength.BITS_1024_1 -> 1024 * 1
        SecuritySettings.DSAKeyLength.BITS_1024_2 -> 1024 * 2
        SecuritySettings.DSAKeyLength.BITS_1024_3 -> 1024 * 3
    }
}

internal fun getNumber(value: SecuritySettings.PBEIterations): Int {
    return when (value) {
        SecuritySettings.PBEIterations.NUMBER_2_10 -> 2.0.pow(10).toInt()
        SecuritySettings.PBEIterations.NUMBER_2_16 -> 2.0.pow(16).toInt()
        SecuritySettings.PBEIterations.NUMBER_2_20 -> 2.0.pow(20).toInt()
    }
}

internal fun getPretty(value: SecuritySettings.PBEIterations): String {
    return when (value) {
        SecuritySettings.PBEIterations.NUMBER_2_10 -> "2^10"
        SecuritySettings.PBEIterations.NUMBER_2_16 -> "2^16"
        SecuritySettings.PBEIterations.NUMBER_2_20 -> "2^20"
    }
}
