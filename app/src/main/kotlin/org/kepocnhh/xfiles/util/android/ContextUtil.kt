package org.kepocnhh.xfiles.util.android

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.widget.Toast

internal fun Context.showToast(message: CharSequence, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

internal fun <T : Activity> Context.findActivity(): T? {
    val result = this as? T
    if (result != null) return result
    if (this is ContextWrapper) return baseContext.findActivity()
    return null
}
