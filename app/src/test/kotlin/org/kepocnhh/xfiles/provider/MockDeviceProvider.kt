package org.kepocnhh.xfiles.provider

import org.kepocnhh.xfiles.entity.Device
import org.kepocnhh.xfiles.entity.mockDevice

internal class MockDeviceProvider(private val device: Device = mockDevice()) : DeviceProvider {
    override fun getCurrentDevice(): Device {
        return device
    }
}
