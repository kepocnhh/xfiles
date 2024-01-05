package org.kepocnhh.xfiles.provider

import org.kepocnhh.xfiles.entity.Device
import java.util.UUID

internal interface DeviceProvider {
    fun getCurrentDevice(): Device
    fun toUUID(device: Device): UUID
}
