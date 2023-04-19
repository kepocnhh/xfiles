package org.kepocnhh.xfiles.foundation.provider

import org.kepocnhh.xfiles.foundation.provider.coroutine.Contexts
import org.kepocnhh.xfiles.foundation.provider.encrypted.EncryptedFileProvider
import org.kepocnhh.xfiles.foundation.provider.logger.LoggerFactory

internal data class Injection(
    val loggers: LoggerFactory,
    val contexts: Contexts,
    val file: EncryptedFileProvider,
)
