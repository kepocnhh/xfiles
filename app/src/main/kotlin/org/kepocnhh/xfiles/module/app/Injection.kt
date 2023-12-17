package org.kepocnhh.xfiles.module.app

import org.kepocnhh.xfiles.entity.SecurityServices
import org.kepocnhh.xfiles.provider.Contexts
import org.kepocnhh.xfiles.provider.DeviceProvider
import org.kepocnhh.xfiles.provider.EncryptedFileProvider
import org.kepocnhh.xfiles.provider.LoggerFactory
import org.kepocnhh.xfiles.provider.PathNames
import org.kepocnhh.xfiles.provider.data.EncryptedLocalDataProvider
import org.kepocnhh.xfiles.provider.data.LocalDataProvider
import org.kepocnhh.xfiles.provider.security.SecurityProvider

internal data class Injection(
    val loggers: LoggerFactory,
    val contexts: Contexts,
    val encrypted: Encrypted,
    val local: LocalDataProvider,
    val security: (SecurityServices) -> SecurityProvider,
    val pathNames: PathNames,
    val devices: DeviceProvider,
)

internal data class Encrypted(
    val files: EncryptedFileProvider,
    val local: EncryptedLocalDataProvider,
)
