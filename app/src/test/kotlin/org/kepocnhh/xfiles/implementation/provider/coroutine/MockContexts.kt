package org.kepocnhh.xfiles.implementation.provider.coroutine

import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlin.coroutines.CoroutineContext
import org.kepocnhh.xfiles.foundation.provider.coroutine.Contexts
import kotlin.coroutines.EmptyCoroutineContext

internal fun mockContexts(
    main: CoroutineContext = UnconfinedTestDispatcher(),
    io: CoroutineContext = UnconfinedTestDispatcher()
): Contexts {
    return Contexts(
        main = main,
        io = io
    )
}
