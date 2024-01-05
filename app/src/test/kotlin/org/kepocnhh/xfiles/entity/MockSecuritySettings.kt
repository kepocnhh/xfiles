package org.kepocnhh.xfiles.entity

internal fun mockSecuritySettings(
    aesKeyLength: SecuritySettings.AESKeyLength =
        SecuritySettings.AESKeyLength.entries.firstOrNull() ?: error("No AES!"),
    pbeIterations: SecuritySettings.PBEIterations =
        SecuritySettings.PBEIterations.entries.firstOrNull() ?: error("No PBE!"),
    dsaKeyLength: SecuritySettings.DSAKeyLength =
        SecuritySettings.DSAKeyLength.entries.firstOrNull() ?: error("No DSA!"),
    hasBiometric: Boolean = false,
): SecuritySettings {
    return SecuritySettings(
        aesKeyLength = aesKeyLength,
        pbeIterations = pbeIterations,
        dsaKeyLength = dsaKeyLength,
        hasBiometric = hasBiometric,
    )
}
