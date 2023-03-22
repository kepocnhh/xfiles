package org.kepocnhh.xfiles.implementation.provider

import org.kepocnhh.xfiles.foundation.provider.Injection
import org.kepocnhh.xfiles.foundation.provider.coroutine.Contexts
import org.kepocnhh.xfiles.foundation.provider.encrypted.EncryptedFileProvider
import org.kepocnhh.xfiles.implementation.provider.coroutine.mockContexts
import org.kepocnhh.xfiles.implementation.provider.encrypted.MockEncryptedFileProvider

internal fun mockInjection(
    contexts: Contexts = mockContexts(),
    file: EncryptedFileProvider = MockEncryptedFileProvider(),
): Injection {
    return Injection(
        contexts = contexts,
        file = file,
    )
}
