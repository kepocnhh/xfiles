package org.kepocnhh.xfiles.module.enter.settings

import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.kepocnhh.xfiles.App
import org.kepocnhh.xfiles.TestActivity
import org.kepocnhh.xfiles.clearStores
import org.kepocnhh.xfiles.entity.mockSecurityServices
import org.kepocnhh.xfiles.module.app.ColorsType
import org.kepocnhh.xfiles.module.app.mockEncrypted
import org.kepocnhh.xfiles.module.app.mockInjection
import org.kepocnhh.xfiles.module.app.mockThemeState
import org.kepocnhh.xfiles.provider.MockEncryptedFileProvider
import org.kepocnhh.xfiles.provider.data.MockLocalDataProvider
import org.kepocnhh.xfiles.provider.mockPathNames
import org.kepocnhh.xfiles.setContent
import org.kepocnhh.xfiles.setInjection
import org.kepocnhh.xfiles.waitOne
import org.robolectric.RobolectricTestRunner
import java.util.concurrent.atomic.AtomicBoolean

@Suppress(
    "StringLiteralDuplication",
    "NonBooleanPropertyPrefixedWithIs",
)
@RunWith(RobolectricTestRunner::class)
internal class SettingsScreenTest {
    @get:Rule
    val rule = createAndroidComposeRule<TestActivity>()

    @Before
    fun before() {
        App.clearStores()
    }

    @Test(timeout = 5_000)
    fun backTest() {
        val injection = mockInjection(
            local = MockLocalDataProvider(
                services = mockSecurityServices(),
            ),
        )
        App.setInjection(injection)
        val back = AtomicBoolean(false)
        rule.setContent {
            App.Theme.Composition(themeState = mockThemeState()) {
                SettingsScreen(
                    onBack = {
                        back.set(true)
                    },
                )
            }
        }
        assertFalse(back.get())
        Espresso.pressBack()
        assertTrue(back.get())
    }

    @Test(timeout = 5_000)
    fun themeStateTest() {
        val injection = mockInjection(
            local = MockLocalDataProvider(
                themeState = mockThemeState(
                    colorsType = ColorsType.DARK,
                ),
                services = mockSecurityServices(),
            ),
        )
        val strings = rule.setContent(injection) {
            SettingsScreen(
                onBack = {
                    error("Illegal state!")
                },
            )
        }
        val isColorsType = hasContentDescription("SettingsScreen:colors:type")
        rule.waitOne(isColorsType and hasText(strings.dark))
    }

    @Test(timeout = 5_000)
    fun biometricTest() {
        val injection = mockInjection(
            local = MockLocalDataProvider(
                services = mockSecurityServices(),
            ),
        )
        val strings = rule.setContent(injection) {
            SettingsScreen(
                onBack = {
                    error("Illegal state!")
                },
            )
        }
        val isBiometricValue = hasContentDescription("SettingsScreen:cipher:biometric:value")
        rule.waitOne(isBiometricValue and hasText(strings.no))
        val isButton = SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button)
        val isBiometric = hasContentDescription("SettingsScreen:cipher:biometric")
        rule.onNode(isButton and isBiometric).performClick()
        rule.waitOne(isBiometricValue and hasText(strings.yes))
    }

    @Test(timeout = 5_000)
    fun databaseExistsTest() {
        val pathNames = mockPathNames()
        val injection = mockInjection(
            pathNames = pathNames,
            local = MockLocalDataProvider(
                services = mockSecurityServices(),
            ),
            encrypted = mockEncrypted(
                files = MockEncryptedFileProvider(
                    exists = setOf(pathNames.dataBase),
                ),
            ),
        )
        val strings = rule.setContent(injection) {
            SettingsScreen(
                onBack = {
                    error("Illegal state!")
                },
            )
        }
        val isBiometricValue = hasContentDescription("SettingsScreen:cipher:biometric:value")
        rule.waitOne(isBiometricValue and hasText(strings.no))
        val isButton = SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button)
        val isBiometric = hasContentDescription("SettingsScreen:cipher:biometric")
        rule.onNode(isButton and isBiometric).performClick()
        rule.waitOne(isBiometricValue and hasText(strings.no))
    }
}
