package org.kepocnhh.xfiles.implementation.module.router

import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.withIndex
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Test
import org.kepocnhh.xfiles.implementation.provider.mockInjection

internal class RouterViewModelTest {
    @Test
    fun bar() {
        val injection = mockInjection()
        val viewModel = RouterViewModel(injection)
        runTest(dispatchTimeoutMs = 10_000) {
            viewModel.state
                .withIndex()
                .onEach { (index, value) ->
                    when (index) {
                        0 -> {
                            assertNull(value)
                            viewModel.requestFile()
                        }
                        1 -> {
                            val exists = checkNotNull(value)
                            assertFalse("File exists!", exists)
                        }
                        else -> TODO()
                    }
                }.take(2).collect()
        }
    }
}
