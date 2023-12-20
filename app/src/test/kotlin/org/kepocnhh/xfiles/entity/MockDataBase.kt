package org.kepocnhh.xfiles.entity

import java.util.UUID
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

internal fun mockDataBase(
    id: UUID = UUID.randomUUID(),
    updated: Duration = 42.seconds,
    secrets: Map<UUID, Pair<String, String>> = emptyMap(),
): DataBase {
    return DataBase(
        id = id,
        updated = updated,
        secrets = secrets,
    )
}
