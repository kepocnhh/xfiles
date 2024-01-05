package org.kepocnhh.xfiles.provider.security

import android.util.Base64

internal object FinalBase64Provider : Base64Provider {
    override fun encode(bytes: ByteArray): String {
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    override fun decode(encoded: String): ByteArray {
        return Base64.decode(encoded, Base64.DEFAULT)
    }
}
