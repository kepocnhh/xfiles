package org.kepocnhh.xfiles.provider

import org.kepocnhh.xfiles.entity.Device

internal interface DeviceProvider {
    fun getCurrentDevice(): Device
}
