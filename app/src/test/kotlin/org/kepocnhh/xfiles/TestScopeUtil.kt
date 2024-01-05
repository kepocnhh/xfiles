package org.kepocnhh.xfiles

import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlin.coroutines.CoroutineContext

internal suspend fun waitUntil(
    scope: TestScope,
    context: CoroutineContext = UnconfinedTestDispatcher(),
    block: suspend () -> Unit,
    action: suspend () -> Unit,
) {
    val job = scope.launch(context) {
        block()
    }
    action()
    job.join()
}
