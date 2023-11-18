package org.kepocnhh.xfiles.provider.data

import java.util.UUID

internal interface EncryptedLocalDataProvider {
    var appId: UUID?
    var databaseId: UUID?
}
