package org.kepocnhh.xfiles.module.theme

import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.withIndex
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.kepocnhh.xfiles.module.app.mockInjection
import org.kepocnhh.xfiles.provider.mockContexts
import kotlin.time.Duration.Companion.seconds

internal class ThemeViewModelTest {
    @Test
    fun foo() {
        runTest(
            timeout = 5.seconds,
        ) {
            val injection = mockInjection(
                contexts = mockContexts(),
            )
            val viewModel = ThemeViewModel(injection)
            viewModel
                .state
                .withIndex()
                .onEach { (index, value) ->
                    when (index) {
                        0 -> {
                            assertNull(value)
                            viewModel.requestThemeState()
                        }
                        1 -> {
                            TODO("foo: $index $value")
                            assertNotNull(value)
                            checkNotNull(value)
                        }
                        else -> TODO("Index: $index, value: $value")
                    }
                }
                .take(2)
                .collect()
        }
    }
}
