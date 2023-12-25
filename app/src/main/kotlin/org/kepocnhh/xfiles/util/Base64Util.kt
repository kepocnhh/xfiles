package org.kepocnhh.xfiles.util

import android.util.Base64

@Deprecated(message = "replace with Base64Provider:encode")
internal fun ByteArray.base64(flags: Int = Base64.DEFAULT): String {
    return Base64.encodeToString(this, flags)
}

internal fun String.base64(): ByteArray {
    return Base64.decode(this, Base64.DEFAULT)
}
