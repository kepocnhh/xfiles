package org.kepocnhh.xfiles.entity

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

internal fun mockDataBase(
    id: String = "foo:id",
    updated: Duration = 42.seconds,
    secrets: Map<String, Pair<String, String>> = emptyMap(),
): DataBase {
    return DataBase(
        id = id,
        updated = updated,
        secrets = secrets,
    )
}
