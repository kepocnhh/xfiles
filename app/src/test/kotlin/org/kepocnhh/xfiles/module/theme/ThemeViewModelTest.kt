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
import org.kepocnhh.xfiles.module.app.ColorsType
import org.kepocnhh.xfiles.module.app.Language
import org.kepocnhh.xfiles.module.app.ThemeState
import org.kepocnhh.xfiles.module.app.mockInjection
import org.kepocnhh.xfiles.provider.data.MockLocalDataProvider
import org.kepocnhh.xfiles.provider.mockContexts
import kotlin.time.Duration.Companion.seconds

internal class ThemeViewModelTest {
    @Test
    fun foo() {
        runTest(timeout = 10.seconds) {
            val colorsType1 = ColorsType.LIGHT
            val language1 = Language.RUSSIAN
            val colorsType2 = ColorsType.DARK
            val language2 = Language.ENGLISH
            check(colorsType1 != colorsType2)
            check(language1 != language2)
            val injection = mockInjection(
                contexts = mockContexts(),
                local = MockLocalDataProvider(themeState = ThemeState(colorsType = colorsType1, language = language1))
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
                            assertEquals(colorsType1, value.colorsType)
                            assertEquals(language1, value.language)
                            viewModel.setColorsType(colorsType2)
                        }
                        2 -> {
                            assertNotNull(value)
                            checkNotNull(value)
                            assertEquals(colorsType2, value.colorsType)
                            assertEquals(language1, value.language)
                            viewModel.setLanguage(language2)
                        }
                        3 -> {
                            assertNotNull(value)
                            checkNotNull(value)
                            assertEquals(colorsType2, value.colorsType)
                            assertEquals(language2, value.language)
                        }
                        else -> error("Unexpected index: $index!")
                    }
                }
                .take(4)
                .collect()
        }
    }
}
