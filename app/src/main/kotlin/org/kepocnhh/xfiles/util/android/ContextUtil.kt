package org.kepocnhh.xfiles.util.android

import android.content.Context
import android.widget.Toast

internal fun Context.showToast(message: CharSequence) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}
