package org.kepocnhh.xfiles

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.junit4.ComposeTestRule
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

internal fun ComposeTestRule.waitOne(matcher: SemanticsMatcher, duration: Duration = 1.seconds) {
    waitUntil(timeoutMillis = duration.inWholeMilliseconds) {
        onAllNodes(matcher)
            .fetchSemanticsNodes()
            .size == 1
    }
}
