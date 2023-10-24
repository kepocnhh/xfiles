package org.kepocnhh.xfiles.provider

import kotlinx.coroutines.test.TestScope
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

internal fun TestScope.mockContexts(
    default: CoroutineContext = UnconfinedTestDispatcher(),
): Contexts {
    return mockContexts(
        main = coroutineContext,
        default = default,
    )
}
