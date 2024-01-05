package org.kepocnhh.xfiles.provider.data

import java.util.UUID

internal interface EncryptedLocalDataProvider {
    var appId: UUID?
    var databaseId: UUID?
}

internal fun EncryptedLocalDataProvider.requireAppId(): UUID {
    return appId ?: error("No app id!")
}

internal fun EncryptedLocalDataProvider.requireDatabaseId(): UUID {
    return databaseId ?: error("No database id!")
}
