package org.kepocnhh.xfiles.entity

internal fun mockBiometricMeta(
    password: ByteArray = "BiometricMeta:password".toByteArray(),
    iv: ByteArray = "BiometricMeta:iv".toByteArray(),
): BiometricMeta {
    return BiometricMeta(
        password = password,
        iv = iv,
    )
}

internal fun mockBiometricMeta(issuer: String): BiometricMeta {
    return mockBiometricMeta(
        password = "$issuer:biometric:meta:password".toByteArray(),
        iv = "$issuer:biometric:meta:iv".toByteArray(),
    )
}
