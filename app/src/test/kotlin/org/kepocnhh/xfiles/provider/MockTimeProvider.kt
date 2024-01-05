package org.kepocnhh.xfiles.provider

import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

internal class MockTimeProvider(
    private val now: Duration = System.currentTimeMillis().milliseconds,
) : TimeProvider {
    override fun now(): Duration {
        return now
    }
}
