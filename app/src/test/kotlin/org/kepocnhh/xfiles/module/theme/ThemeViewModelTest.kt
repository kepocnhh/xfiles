package org.kepocnhh.xfiles.module.theme

import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.withIndex
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.kepocnhh.xfiles.module.app.mockInjection
import org.kepocnhh.xfiles.module.app.mockThemeState
import org.kepocnhh.xfiles.provider.data.MockLocalDataProvider
import org.kepocnhh.xfiles.provider.mockContexts
import kotlin.time.Duration.Companion.seconds

internal class ThemeViewModelTest {
    @Test
    fun requestThemeStateTest() {
        runTest(timeout = 10.seconds) {
            val expected = mockThemeState()
            val injection = mockInjection(
                contexts = mockContexts(main = coroutineContext),
                local = MockLocalDataProvider(themeState = expected),
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
                            assertNotNull(value)
                            checkNotNull(value)
                            assertEquals(expected, value)
                        }
                        else -> error("Unexpected index: $index!")
                    }
                }
                .take(2)
                .collect()
        }
    }
}
