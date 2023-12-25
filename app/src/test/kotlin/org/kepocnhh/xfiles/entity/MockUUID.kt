package org.kepocnhh.xfiles.entity

import java.util.UUID

internal fun mockUUID(number: Int = 1): UUID {
    check(number in 1..999_999)
    return UUID.fromString("4dd63734-6bf5-4e15-be86-ccfdf" + String.format("%06d", number))
}
