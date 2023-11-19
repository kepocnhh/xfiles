package org.kepocnhh.xfiles.util.android

import android.content.ClipData
import android.os.PersistableBundle

internal object ClipDataUtil {
    fun newSecretText(label: CharSequence, text: CharSequence): ClipData {
        val clipData = ClipData.newPlainText(label, text)
        val extras = PersistableBundle()
        extras.putBoolean("android.content.extra.IS_SENSITIVE", true)
        clipData.description.extras = extras
        return clipData
    }
}
