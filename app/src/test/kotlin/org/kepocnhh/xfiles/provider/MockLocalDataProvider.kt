package org.kepocnhh.xfiles.provider

import org.kepocnhh.xfiles.entity.SecurityServices
import org.kepocnhh.xfiles.entity.SecuritySettings
import org.kepocnhh.xfiles.module.app.ThemeState
import org.kepocnhh.xfiles.provider.data.LocalDataProvider

internal class MockLocalDataProvider : LocalDataProvider {
    override var themeState: ThemeState
        get() = TODO("Not yet implemented: themeState")
        set(value) {}
    override var services: SecurityServices?
        get() = TODO("Not yet implemented: services")
        set(value) {}
    override var securitySettings: SecuritySettings
        get() = TODO("Not yet implemented: securitySettings")
        set(value) {}
}
