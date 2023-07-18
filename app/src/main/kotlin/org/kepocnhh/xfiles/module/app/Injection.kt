package org.kepocnhh.xfiles.module.app

import org.kepocnhh.xfiles.provider.Contexts
import org.kepocnhh.xfiles.provider.EncryptedFileProvider
import org.kepocnhh.xfiles.provider.LoggerFactory

internal data class Injection(
    val loggers: LoggerFactory,
    val contexts: Contexts,
    val files: EncryptedFileProvider,
)
