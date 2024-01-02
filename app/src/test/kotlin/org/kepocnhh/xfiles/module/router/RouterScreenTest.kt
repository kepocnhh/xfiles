package org.kepocnhh.xfiles.module.router

import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.kepocnhh.xfiles.App
import org.kepocnhh.xfiles.TestActivity
import org.kepocnhh.xfiles.TestProvider
import org.kepocnhh.xfiles.clearStores
import org.kepocnhh.xfiles.module.app.mockInjection
import org.kepocnhh.xfiles.module.app.mockThemeState
import org.kepocnhh.xfiles.setInjection
import org.kepocnhh.xfiles.waitOne
import org.kepocnhh.xfiles.waitUntilTrue
import org.robolectric.RobolectricTestRunner
import java.security.Security
import java.util.concurrent.atomic.AtomicBoolean

@RunWith(RobolectricTestRunner::class)
internal class RouterScreenTest {
    @get:Rule
    val rule = createAndroidComposeRule<TestActivity>()

    @Before
    fun before() {
        App.clearStores()
    }

    @After
    fun after() {
        Security.removeProvider("AndroidOpenSSL")
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

    @Test(timeout = 2_000)
    fun enterScreenTest() {
        val provider = TestProvider(
            name = "AndroidOpenSSL",
            services = listOf(
                "MessageDigest" to "SHA-512",
                "SecureRandom" to "SHA1PRNG",
            ),
        )
        Security.insertProviderAt(provider, 0)
        val injection = mockInjection()
        App.setInjection(injection)
        rule.setContent {
            App.Theme.Composition(themeState = mockThemeState()) {
                RouterScreen(
                    onBack = {
                        error("Illegal state!")
                    },
                )
            }
        }
        val isTraversalGroup = SemanticsMatcher.expectValue(SemanticsProperties.IsTraversalGroup, true)
        val isEnterScreen = hasContentDescription("EnterScreen")
        rule.waitOne(isTraversalGroup and isEnterScreen)
        rule.onNode(isTraversalGroup and isEnterScreen).assertIsDisplayed()
        val isUnlockedScreen = hasContentDescription("UnlockedScreen")
        rule.onNode(isTraversalGroup and isUnlockedScreen).assertDoesNotExist()
    }
}
