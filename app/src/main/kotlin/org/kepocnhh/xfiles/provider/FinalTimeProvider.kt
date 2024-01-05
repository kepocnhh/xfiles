package org.kepocnhh.xfiles.provider

import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

internal object FinalTimeProvider : TimeProvider {
    override fun now(): Duration {
        return System.currentTimeMillis().milliseconds
    }
}
