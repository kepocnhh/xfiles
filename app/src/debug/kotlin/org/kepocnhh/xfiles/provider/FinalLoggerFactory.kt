package org.kepocnhh.xfiles.provider

import android.util.Log

internal object FinalLoggerFactory : LoggerFactory {
    override fun newLogger(tag: String): Logger {
        return AndroidLogger(tag)
    }
}

private class AndroidLogger(private val tag: String) : Logger {
    override fun debug(message: String) {
        Log.d(tag, message)
    }

    override fun warning(message: String) {
        Log.w(tag, message)
    }
}
