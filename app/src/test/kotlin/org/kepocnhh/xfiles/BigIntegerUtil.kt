package org.kepocnhh.xfiles

import java.math.BigInteger

internal object BigIntegerUtil {
    fun fromBits(count: Int): BigInteger {
        return BigInteger(ByteArray(count / 8).also { it[0] = 1 }).shiftLeft(7)
    }
}
