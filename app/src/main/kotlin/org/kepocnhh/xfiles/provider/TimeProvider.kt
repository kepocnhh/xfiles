package org.kepocnhh.xfiles.provider

import kotlin.time.Duration

internal interface TimeProvider {
    fun now(): Duration
}
