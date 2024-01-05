package org.kepocnhh.xfiles.entity

import java.util.UUID
import kotlin.time.Duration

internal fun mockDataBase(
    id: UUID = mockUUID(),
    updated: Duration = Duration.ZERO,
    secrets: Map<UUID, Pair<String, String>> = emptyMap(),
): DataBase {
    return DataBase(
        id = id,
        updated = updated,
        secrets = secrets,
    )
}
