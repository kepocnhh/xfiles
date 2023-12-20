package org.kepocnhh.xfiles.provider.security

import java.util.UUID

internal class MockUUIDGenerator(
    private val uuid: UUID = UUID.randomUUID(),
) : UUIDGenerator {
    override fun generate(): UUID {
        return uuid
    }
}
