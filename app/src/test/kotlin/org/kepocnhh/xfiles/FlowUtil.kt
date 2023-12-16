package org.kepocnhh.xfiles

import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.withIndex

internal fun <T : Any> Flow<T?>.onEachIndexed(action: suspend (IndexedValue<T?>) -> Unit): Flow<IndexedValue<T?>> {
    return withIndex().onEach(action)
}

internal suspend fun Flow<*>.collect(count: Int) {
    take(count).collect()
}
