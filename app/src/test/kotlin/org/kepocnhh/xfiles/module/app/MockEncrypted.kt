package org.kepocnhh.xfiles.module.app

import org.kepocnhh.xfiles.provider.EncryptedFileProvider
import org.kepocnhh.xfiles.provider.MockEncryptedFileProvider
import org.kepocnhh.xfiles.provider.data.EncryptedLocalDataProvider
import org.kepocnhh.xfiles.provider.data.MockEncryptedLocalDataProvider

internal fun mockEncrypted(
    files: EncryptedFileProvider = MockEncryptedFileProvider,
    local: EncryptedLocalDataProvider = MockEncryptedLocalDataProvider(),
): Encrypted {
    return Encrypted(
        files = files,
        local = local,
    )
}
