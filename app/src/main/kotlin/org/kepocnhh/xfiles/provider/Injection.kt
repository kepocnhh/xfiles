package org.kepocnhh.xfiles.provider

internal data class Injection(
    val loggers: LoggerFactory,
    val contexts: Contexts,
    val file: EncryptedFileProvider,
)
