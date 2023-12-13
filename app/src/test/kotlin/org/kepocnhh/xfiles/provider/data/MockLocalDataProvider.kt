package org.kepocnhh.xfiles.provider.data

import org.kepocnhh.xfiles.entity.Device
import org.kepocnhh.xfiles.entity.SecurityServices
import org.kepocnhh.xfiles.entity.SecuritySettings
import org.kepocnhh.xfiles.entity.mockSecuritySettings
import org.kepocnhh.xfiles.module.app.ThemeState
import org.kepocnhh.xfiles.module.app.mockThemeState

internal class MockLocalDataProvider(
    override var themeState: ThemeState = mockThemeState(),
    override var services: SecurityServices? = null,
    override var securitySettings: SecuritySettings = mockSecuritySettings(),
    override var device: Device? = null,
) : LocalDataProvider
