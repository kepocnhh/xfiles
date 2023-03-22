package org.kepocnhh.xfiles.implementation.provider.coroutine

import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.kepocnhh.xfiles.foundation.provider.coroutine.Contexts
import kotlin.coroutines.CoroutineContext

internal fun mockContexts(
    main: CoroutineContext = UnconfinedTestDispatcher(),
    io: CoroutineContext = UnconfinedTestDispatcher(),
): Contexts {
    return Contexts(
        main = main,
        io = io,
    )
}
