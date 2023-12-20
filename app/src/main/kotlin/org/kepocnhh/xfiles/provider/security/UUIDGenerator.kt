package org.kepocnhh.xfiles.provider.security

import java.util.UUID

internal interface UUIDGenerator {
    fun generate(): UUID
}
