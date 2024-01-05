package org.kepocnhh.xfiles.provider

import kotlin.coroutines.CoroutineContext

internal data class Contexts(
    val main: CoroutineContext,
    val default: CoroutineContext,
)
