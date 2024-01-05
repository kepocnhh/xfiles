package org.kepocnhh.xfiles.entity

internal fun mockBiometricMeta(
    passwordEncrypted: ByteArray = "BiometricMeta:password:encrypted".toByteArray(),
    iv: ByteArray = "BiometricMeta:iv".toByteArray(),
): BiometricMeta {
    return BiometricMeta(
        passwordEncrypted = passwordEncrypted,
        iv = iv,
    )
}

internal fun mockBiometricMeta(issuer: String): BiometricMeta {
    return mockBiometricMeta(
        passwordEncrypted = "$issuer:biometric:meta:password:encrypted".toByteArray(),
        iv = "$issuer:biometric:meta:iv".toByteArray(),
    )
}
