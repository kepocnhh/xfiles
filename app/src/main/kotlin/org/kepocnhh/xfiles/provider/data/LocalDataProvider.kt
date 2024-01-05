package org.kepocnhh.xfiles.provider.data

import org.kepocnhh.xfiles.entity.Device
import org.kepocnhh.xfiles.entity.SecurityServices
import org.kepocnhh.xfiles.entity.SecuritySettings
import org.kepocnhh.xfiles.module.app.ThemeState

internal interface LocalDataProvider {
    var themeState: ThemeState
    var services: SecurityServices?
    var securitySettings: SecuritySettings
    var device: Device?
}

@Suppress("NotImplementedDeclaration")
internal fun LocalDataProvider.requireServices(): SecurityServices {
    return services ?: TODO("No services!")
}

internal fun LocalDataProvider.requireDevice(): Device {
    return device ?: error("No device!")
}
