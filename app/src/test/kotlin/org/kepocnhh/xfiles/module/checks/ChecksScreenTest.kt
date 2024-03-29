package org.kepocnhh.xfiles.module.checks

import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import org.junit.After
import org.junit.Assert.assertFalse
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
import org.kepocnhh.xfiles.textMatcher
import org.kepocnhh.xfiles.waitOne
import org.robolectric.RobolectricTestRunner
import java.security.Security
import java.util.concurrent.atomic.AtomicBoolean

@Suppress(
    "StringLiteralDuplication",
    "NonBooleanPropertyPrefixedWithIs",
    "IgnoredReturnValue",
)
@RunWith(RobolectricTestRunner::class)
internal class ChecksScreenTest {
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
    fun onCompleteTest() {
        val issuer = "ChecksScreenTest:onCompleteTest"
        val provider = TestProvider(
            name = "AndroidOpenSSL",
            services = listOf(
                "MessageDigest" to "SHA-512",
                "MessageDigest" to "MD5",
                "SecureRandom" to "SHA1PRNG",
            ),
        )
        Security.insertProviderAt(provider, 0)
        val injection = mockInjection()
        App.setInjection(injection)
        val completed = AtomicBoolean(false)
        rule.setContent {
            App.Theme.Composition(themeState = mockThemeState()) {
                ChecksScreen(
                    onComplete = {
                        completed.set(true)
                    },
                    onExit = {
                        error("$issuer: onExit: Illegal state!")
                    },
                )
            }
        }
        rule.waitUntil {
            completed.get()
        }
    }

    @Test(timeout = 2_000)
    fun errorTest() {
        val issuer = "ChecksScreenTest:errorTest"
        val injection = mockInjection()
        App.setInjection(injection)
        rule.setContent {
            App.Theme.Composition(themeState = mockThemeState()) {
                ChecksScreen(
                    onComplete = {
                        error("$issuer: onComplete: Illegal state!")
                    },
                    onExit = {
                        error("$issuer: onExit: Illegal state!")
                    },
                )
            }
        }
        rule.waitOne(isError and hasText)
    }

    @Test(timeout = 2_000)
    fun errorIdsTest() {
        val issuer = "ChecksScreenTest:errorIdsTest"
        rule.setContent {
            App.Theme.Composition(themeState = mockThemeState()) {
                ChecksScreen(
                    state = ChecksViewModel.State.OnError(ChecksViewModel.ChecksType.IDS, IllegalStateException()),
                    onExit = {
                        error("$issuer: onExit: Illegal state!")
                    },
                )
            }
        }
        rule.waitOne(isError and hasText)
    }

    @Test(timeout = 2_000)
    fun exitTest() {
        val issuer = "ChecksScreenTest:exitTest"
        val injection = mockInjection()
        App.setInjection(injection)
        val exit = AtomicBoolean(false)
        rule.setContent {
            App.Theme.Composition(themeState = mockThemeState()) {
                ChecksScreen(
                    onComplete = {
                        error("$issuer: onComplete: Illegal state!")
                    },
                    onExit = {
                        exit.set(true)
                    },
                )
            }
        }
        rule.waitOne(isError and hasText)
        assertFalse(exit.get())
        val isButton = SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button)
        val isExit = hasContentDescription("ChecksScreen:exit")
        rule.onNode(isButton and isExit).performClick()
        rule.waitUntil {
            exit.get()
        }
    }

    companion object {
        val isError = hasContentDescription("ChecksScreen:error")
        val hasText = textMatcher("has text") { text ->
            text.isNotEmpty()
        }
    }
}
