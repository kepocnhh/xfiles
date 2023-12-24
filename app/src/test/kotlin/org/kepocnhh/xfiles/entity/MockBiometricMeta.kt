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
