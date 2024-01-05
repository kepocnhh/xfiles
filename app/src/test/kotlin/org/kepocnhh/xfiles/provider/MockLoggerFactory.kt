package org.kepocnhh.xfiles.provider

internal object MockLoggerFactory : LoggerFactory {
    override fun newLogger(tag: String): Logger {
        return MockLogger
    }
}

internal object MockLogger : Logger {
    override fun debug(message: String) {
        // noop
    }

    override fun warning(message: String) {
        // noop
    }
}
