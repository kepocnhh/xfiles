package org.kepocnhh.xfiles

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import org.kepocnhh.xfiles.module.app.Injection
import org.kepocnhh.xfiles.module.app.Strings
import java.util.concurrent.atomic.AtomicReference

internal fun App.Companion.setInjection(injection: Injection) {
    val field = App::class.java.getDeclaredField("_injection")
    field.isAccessible = true
    field.set(this, injection)
}

internal fun App.Companion.clearStores() {
    val field = App::class.java.getDeclaredField("vmStores")
    field.isAccessible = true
    val stores = field.get(this) as MutableMap<*, *>
    stores.clear()
}

internal fun ComposeContentTestRule.setContent(
    injection: Injection,
    composable: @Composable () -> Unit,
): Strings {
    App.setInjection(injection)
    val stringsReference = AtomicReference<Strings>(null)
    setContent {
        App.Theme.Composition(themeState = injection.local.themeState) {
            stringsReference.compareAndSet(null, App.Theme.strings)
            composable()
        }
    }
    waitUntil {
        stringsReference.get() != null
    }
    return checkNotNull(stringsReference.get())
}
