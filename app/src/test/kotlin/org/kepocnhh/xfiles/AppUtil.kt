package org.kepocnhh.xfiles

import org.kepocnhh.xfiles.module.app.Injection

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
