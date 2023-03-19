package org.kepocnhh.xfiles

import android.content.Context
import android.widget.Toast

internal fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}
