package org.kepocnhh.xfiles.module.router

import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.kepocnhh.xfiles.App
import org.kepocnhh.xfiles.TestActivity
import org.kepocnhh.xfiles.clearStores
import org.kepocnhh.xfiles.module.app.mockInjection
import org.kepocnhh.xfiles.module.app.mockThemeState
import org.kepocnhh.xfiles.setInjection
import org.kepocnhh.xfiles.waitUntilTrue
import org.robolectric.RobolectricTestRunner
import java.util.concurrent.atomic.AtomicBoolean

@RunWith(RobolectricTestRunner::class)
internal class RouterScreenTest {
    @get:Rule
    val rule = createAndroidComposeRule<TestActivity>()

    @Before
    fun before() {
        App.clearStores()
    }

    @Test(timeout = 2_000)
    fun backTest() {
        val backRef = AtomicBoolean(false)
        val injection = mockInjection()
        App.setInjection(injection)
        rule.setContent {
            App.Theme.Composition(themeState = mockThemeState()) {
                RouterScreen(
                    onBack = {
                        backRef.set(true)
                    },
                )
            }
        }
        check(!backRef.get())
        val isButton = SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button)
        val isExit = hasContentDescription("ChecksScreen:exit")
        rule.onNode(isButton and isExit).performClick()
        rule.waitUntilTrue(backRef)
    }
}
