package org.kepocnhh.xfiles.entity

import kotlin.time.Duration

internal data class DataBase(
    // todo UUID
    val id: String,
    val updated: Duration,
    // todo UUID
    val secrets: Map<String, Pair<String, String>>,
)
