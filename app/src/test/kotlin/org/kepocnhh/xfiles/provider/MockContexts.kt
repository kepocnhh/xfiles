package org.kepocnhh.xfiles.provider

import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlin.coroutines.CoroutineContext

internal fun mockContexts(
    main: CoroutineContext = UnconfinedTestDispatcher(),
    default: CoroutineContext = UnconfinedTestDispatcher(),
): Contexts {
    return Contexts(
        main = main,
        default = default,
    )
}
