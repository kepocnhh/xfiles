package org.kepocnhh.xfiles.provider.security

import org.kepocnhh.xfiles.entity.mockUUID
import java.util.UUID

internal class MockUUIDGenerator(
    private val uuid: UUID = mockUUID(),
) : UUIDGenerator {
    override fun generate(): UUID {
        return uuid
    }
}
