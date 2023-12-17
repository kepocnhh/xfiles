package org.kepocnhh.xfiles.module.app

import org.kepocnhh.xfiles.entity.SecurityServices
import org.kepocnhh.xfiles.provider.Contexts
import org.kepocnhh.xfiles.provider.DeviceProvider
import org.kepocnhh.xfiles.provider.LoggerFactory
import org.kepocnhh.xfiles.provider.MockDeviceProvider
import org.kepocnhh.xfiles.provider.MockLoggerFactory
import org.kepocnhh.xfiles.provider.PathNames
import org.kepocnhh.xfiles.provider.data.LocalDataProvider
import org.kepocnhh.xfiles.provider.data.MockLocalDataProvider
import org.kepocnhh.xfiles.provider.mockContexts
import org.kepocnhh.xfiles.provider.mockPathNames
import org.kepocnhh.xfiles.provider.security.SecurityProvider

@Suppress("LongParameterList")
internal fun mockInjection(
    loggers: LoggerFactory = MockLoggerFactory,
    contexts: Contexts = mockContexts(),
    encrypted: Encrypted = mockEncrypted(),
    local: LocalDataProvider = MockLocalDataProvider(),
    security: (SecurityServices) -> SecurityProvider = { TODO("No security provider!") },
    pathNames: PathNames = mockPathNames(),
    devices: DeviceProvider = MockDeviceProvider(),
): Injection {
    return Injection(
        loggers = loggers,
        contexts = contexts,
        encrypted = encrypted,
        local = local,
        security = security,
        pathNames = pathNames,
        devices = devices,
    )
}
