package org.kepocnhh.xfiles.provider.data

import java.util.UUID

internal class MockEncryptedLocalDataProvider(
    override var appId: UUID? = null,
    override var databaseId: UUID? = null,
) : EncryptedLocalDataProvider
