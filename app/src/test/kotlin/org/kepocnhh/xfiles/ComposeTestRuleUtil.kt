package org.kepocnhh.xfiles

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.junit4.ComposeTestRule
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

internal fun ComposeTestRule.waitOne(
    matcher: SemanticsMatcher,
    useUnmergedTree: Boolean = false,
    duration: Duration = 1.seconds,
) {
    waitCount(matcher = matcher, useUnmergedTree = useUnmergedTree, count = 1, duration = duration)
}

internal fun ComposeTestRule.waitCount(
    matcher: SemanticsMatcher,
    useUnmergedTree: Boolean = false,
    count: Int,
    duration: Duration = 1.seconds,
) {
    waitUntil(timeoutMillis = duration.inWholeMilliseconds) {
        onAllNodes(matcher, useUnmergedTree = useUnmergedTree)
            .fetchSemanticsNodes()
            .size == count
    }
}

internal fun ComposeTestRule.waitZero(
    matcher: SemanticsMatcher,
    duration: Duration = 1.seconds,
) {
    waitUntil(timeoutMillis = duration.inWholeMilliseconds) {
        onAllNodes(matcher)
            .fetchSemanticsNodes()
            .isEmpty()
    }
}

internal fun <T : Any> ComposeTestRule.waitUntilPresent(
    ref: AtomicReference<T?>,
    duration: Duration = 1.seconds,
): T {
    waitUntil(timeoutMillis = duration.inWholeMilliseconds) {
        ref.get() != null
    }
    return ref.get() ?: error("No value!")
}

internal fun ComposeTestRule.waitUntilTrue(
    ref: AtomicBoolean,
    duration: Duration = 1.seconds,
) {
    waitUntil(timeoutMillis = duration.inWholeMilliseconds) {
        ref.get()
    }
}
