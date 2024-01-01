package org.kepocnhh.xfiles

import java.util.concurrent.atomic.AtomicInteger

private object BytesUtil {
    val number = AtomicInteger(0)
}

internal fun mockBytes(
    prefix: String = "mock:bytes",
    number: Int = BytesUtil.number.incrementAndGet(),
): ByteArray {
    check(number in 1..999_999)
    return (prefix + String.format("%06d", number)).toByteArray()
}
