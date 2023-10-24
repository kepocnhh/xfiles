package org.kepocnhh.xfiles.module.app

import org.kepocnhh.xfiles.entity.SecurityServices
import org.kepocnhh.xfiles.provider.Contexts
import org.kepocnhh.xfiles.provider.EncryptedFileProvider
import org.kepocnhh.xfiles.provider.LoggerFactory
import org.kepocnhh.xfiles.provider.MockEncryptedFileProvider
import org.kepocnhh.xfiles.provider.MockLocalDataProvider
import org.kepocnhh.xfiles.provider.MockLoggerFactory
import org.kepocnhh.xfiles.provider.MockSecurityProvider
import org.kepocnhh.xfiles.provider.PathNames
import org.kepocnhh.xfiles.provider.data.LocalDataProvider
import org.kepocnhh.xfiles.provider.mockContexts
import org.kepocnhh.xfiles.provider.mockPathNames
import org.kepocnhh.xfiles.provider.security.SecurityProvider

internal fun mockInjection(
    loggers: LoggerFactory = MockLoggerFactory(),
    contexts: Contexts = mockContexts(),
    files: EncryptedFileProvider = MockEncryptedFileProvider(),
    local: LocalDataProvider = MockLocalDataProvider(),
    security: (SecurityServices) -> SecurityProvider = { MockSecurityProvider() },
    pathNames: PathNames = mockPathNames(),
): Injection {
    return Injection(
        loggers = loggers,
        contexts = contexts,
        files = files,
        local = local,
        security = security,
        pathNames = pathNames,
    )
}
