package org.kepocnhh.xfiles.provider

import org.kepocnhh.xfiles.entity.Device
import org.kepocnhh.xfiles.entity.mockDevice
import java.util.UUID

internal class MockDeviceProvider(
    private val device: Device = mockDevice(),
    private val uuids: Map<Device, UUID> = emptyMap(),
) : DeviceProvider {
    override fun getCurrentDevice(): Device {
        return device
    }

    override fun toUUID(device: Device): UUID {
        return uuids[device] ?: error("No uuid by $device!")
    }
}
