package org.kepocnhh.xfiles.module.unlocked.items

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.Espresso
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.kepocnhh.xfiles.App
import org.kepocnhh.xfiles.TestActivity
import org.kepocnhh.xfiles.module.app.mockThemeState
import org.robolectric.RobolectricTestRunner
import java.util.concurrent.atomic.AtomicBoolean

@RunWith(RobolectricTestRunner::class)
internal class AddItemScreenTest {
    @get:Rule
    val rule = createAndroidComposeRule<TestActivity>()

    @Test(timeout = 2_000)
    fun cancelTest() {
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
}
