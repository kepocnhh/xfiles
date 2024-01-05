package org.kepocnhh.xfiles.entity

import java.util.Locale
import java.util.UUID
import java.util.concurrent.atomic.AtomicInteger

private object UUIDUtil {
    val number = AtomicInteger(0)
}

internal fun mockUUID(
    number: Int = UUIDUtil.number.incrementAndGet(),
    locale: Locale = Locale.US,
): UUID {
    check(number in 1..999_999)
    return UUID.fromString("4dd63734-6bf5-4e15-be86-ccfdf" + String.format(locale, "%06d", number))
}
