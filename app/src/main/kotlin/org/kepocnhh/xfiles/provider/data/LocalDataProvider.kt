package org.kepocnhh.xfiles.provider.data

import org.kepocnhh.xfiles.entity.SecurityServices
import org.kepocnhh.xfiles.module.app.ThemeState

internal interface LocalDataProvider {
    var themeState: ThemeState
    var services: SecurityServices?
}
