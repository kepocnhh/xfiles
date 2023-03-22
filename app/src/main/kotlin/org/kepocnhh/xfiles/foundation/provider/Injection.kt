package org.kepocnhh.xfiles.foundation.provider

import org.kepocnhh.xfiles.foundation.provider.coroutine.Contexts
import org.kepocnhh.xfiles.foundation.provider.encrypted.EncryptedFileProvider

internal data class Injection(
    val contexts: Contexts,
    val file: EncryptedFileProvider,
)
