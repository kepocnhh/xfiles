package org.kepocnhh.xfiles.foundation.provider.logger

internal interface LoggerFactory {
    fun newLogger(tag: String): Logger
}

internal interface Logger {
    fun debug(message: String)
}
