package org.kepocnhh.xfiles.provider

internal object FinalLoggerFactory : LoggerFactory {
    override fun newLogger(tag: String): Logger {
        return EmptyLogger
    }
}

private object EmptyLogger : Logger {
    override fun debug(message: String) {
        // noop
    }

    override fun warning(message: String) {
        // noop
    }
}
