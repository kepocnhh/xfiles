package org.kepocnhh.xfiles.implementation.provider.logger

import android.util.Log
import org.kepocnhh.xfiles.foundation.provider.logger.Logger
import org.kepocnhh.xfiles.foundation.provider.logger.LoggerFactory

internal object FinalLoggerFactory : LoggerFactory {
    override fun newLogger(tag: String): Logger {
        return AndroidLogger(tag)
    }
}

private class AndroidLogger(private val tag: String) : Logger {
    override fun debug(message: String) {
        Log.d(tag, message)
    }
}
