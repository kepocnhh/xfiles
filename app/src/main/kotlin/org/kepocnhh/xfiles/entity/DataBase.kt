package org.kepocnhh.xfiles.entity

import java.util.UUID
import kotlin.time.Duration

internal data class DataBase(
    val id: UUID,
    val updated: Duration,
    val secrets: Map<UUID, Pair<String, String>>,
)
