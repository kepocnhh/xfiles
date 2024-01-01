package org.kepocnhh.xfiles.module.unlocked.items

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.kepocnhh.xfiles.App
import org.kepocnhh.xfiles.TestActivity
import org.kepocnhh.xfiles.module.app.mockInjection
import org.kepocnhh.xfiles.module.app.mockThemeState
import org.kepocnhh.xfiles.setContent
import org.kepocnhh.xfiles.waitUntilPresent
import org.robolectric.RobolectricTestRunner
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

@RunWith(RobolectricTestRunner::class)
internal class AddItemScreenTest {
    @get:Rule
    val rule = createAndroidComposeRule<TestActivity>()

    @Test(timeout = 2_000)
    fun backTest() {
        val cancelled = AtomicBoolean(false)
        rule.setContent {
            App.Theme.Composition(themeState = mockThemeState()) {
                AddItemScreen(
                    onAdd = { _, _ ->
                        error("Illegal state!")
                    },
                    onCancel = {
                        cancelled.set(true)
                    },
                )
            }
        }
        assertFalse(cancelled.get())
        Espresso.pressBack()
        rule.waitUntil {
            cancelled.get()
        }
    }

    @Test(timeout = 2_000)
    fun addTest() {
        val issuer = "AddItemScreenTest:addTest"
        val title = "$issuer:title"
        val secret = "$issuer:secret"
        val added = AtomicReference<Pair<String, String>?>(null)
        val strings = rule.setContent(mockInjection()) {
            val focusedState = remember { mutableStateOf<Focused?>(null) }
            val valuesState = remember { mutableStateMapOf<Focused, String>() }
            Column {
                Box(modifier = Modifier.weight(4f)) {
                    AddItemScreen(
                        focusedState = focusedState,
                        valuesState = valuesState,
                        secretFieldState = SecretFieldState(
                            expanded = true,
                            size = null,
                            x = 0f,
                        ),
                        onSecretFieldSize = {
                            // noop
                        },
                        onShowSecretField = {
                            // noop
                        },
                        onExpandedSecretField = {
                            // noop
                        },
                        onAdd = { t, s ->
                            added.set(t to s)
                        },
                    )
                }
                BasicText(
                    modifier = Modifier
                        .semantics {
                            role = Role.Button
                            contentDescription = "$issuer:set:title"
                        }
                        .weight(1f)
                        .clickable {
                            valuesState[Focused.TITLE] = title
                        },
                    text = "set title",
                )
                BasicText(
                    modifier = Modifier
                        .semantics {
                            role = Role.Button
                            contentDescription = "$issuer:set:secret"
                        }
                        .weight(1f)
                        .clickable {
                            valuesState[Focused.SECRET] = secret
                        },
                    text = "set secret",
                )
            }
        }
        val isTitle = hasContentDescription("TextFocused:${Focused.TITLE.name}")
        val isTitleValue = hasContentDescription("TextFocused:value:${Focused.TITLE.name}")
        rule.onNode(isTitleValue).assert(hasText(strings.addItem.hintTitle))
        val isButton = SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button)
        rule.onNode(isTitle).performClick()
        rule.onNode(isButton and hasContentDescription("$issuer:set:title")).performClick()
        rule.onNode(isTitleValue).assert(hasText(title))
        val isSecretValue = hasContentDescription("TextFocused:value:${Focused.SECRET.name}")
        rule.onNode(isSecretValue).assert(hasText(strings.addItem.hintSecret))
        val isAction = hasContentDescription("Keyboard:action")
        rule.onNode(isButton and isAction, useUnmergedTree = true)
            .assert(hasText(strings.addItem.next))
            .performClick()
        rule.onNode(isButton and hasContentDescription("$issuer:set:secret")).performClick()
        rule.onNode(isSecretValue).assert(hasText("*".repeat(secret.length)))
        rule.onNode(isButton and isAction, useUnmergedTree = true)
            .assert(hasText(strings.addItem.done))
            .performClick()
        val pair = rule.waitUntilPresent(added)
        assertEquals(title, pair.first)
        assertEquals(secret, pair.second)
    }
}
