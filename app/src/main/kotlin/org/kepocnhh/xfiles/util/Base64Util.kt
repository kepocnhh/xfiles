package org.kepocnhh.xfiles.util

import android.util.Base64

internal fun ByteArray.base64(flags: Int = Base64.DEFAULT): String {
    return Base64.encodeToString(this, flags)
}
