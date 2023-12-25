package org.kepocnhh.xfiles.entity

import java.util.Arrays
import javax.crypto.spec.PBEKeySpec

internal class MockPBEKeySpec(
    password: CharArray,
    salt: ByteArray,
    iterationCount: Int,
    keyLength: Int,
) : PBEKeySpec(
    password,
    salt,
    iterationCount,
    keyLength,
) {
    constructor(
        password: String,
        salt: ByteArray,
        iterationCount: Int,
        keyLength: Int,
    ) : this(
        password = password.toCharArray(),
        salt = salt,
        iterationCount = iterationCount,
        keyLength = keyLength,
    )

    constructor(delegate: PBEKeySpec) : this(
        password = delegate.password,
        salt = delegate.salt,
        iterationCount = delegate.iterationCount,
        keyLength = delegate.keyLength,
    )

    override fun equals(other: Any?): Boolean {
        return when (other) {
            is PBEKeySpec -> {
                password.contentEquals(other.password) &&
                        salt.contentEquals(other.salt) &&
                        iterationCount == other.iterationCount &&
                        keyLength == other.keyLength
            }
            else -> false
        }
    }

    override fun hashCode(): Int {
        return Arrays.hashCode(password) +
                Arrays.hashCode(salt) +
                iterationCount +
                keyLength
    }
}
