package org.kepocnhh.xfiles.util.android

import android.content.ClipboardManager

internal fun ClipboardManager.getPrimaryClipTextOrNull(): CharSequence? {
    if (!hasPrimaryClip()) return null
    val primaryClip = primaryClip ?: return null
    if (primaryClip.itemCount != 1) return null
    return primaryClip.getItemAt(0).text
}

@Suppress("Deprecation")
internal fun ClipboardManager.clear() {
    text = ""
}
