package org.kepocnhh.xfiles.entity

internal fun mockSecuritySettings(
    aesKeyLength: SecuritySettings.AESKeyLength = SecuritySettings.AESKeyLength.values().first(),
    pbeIterations: SecuritySettings.PBEIterations = SecuritySettings.PBEIterations.values().first(),
    dsaKeyLength: SecuritySettings.DSAKeyLength = SecuritySettings.DSAKeyLength.values().first(),
): SecuritySettings {
    return SecuritySettings(
        aesKeyLength = aesKeyLength,
        pbeIterations = pbeIterations,
        dsaKeyLength = dsaKeyLength,
    )
}
