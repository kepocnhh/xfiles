package org.kepocnhh.xfiles.provider

import android.os.Build
import org.kepocnhh.xfiles.entity.Device

internal object FinalDeviceProvider : DeviceProvider {
    override fun getCurrentDevice(): Device {
        return Device(
            manufacturer = Build.MANUFACTURER,
            brand = Build.BRAND,
            model = Build.MODEL,
            name = Build.DEVICE,
            supportedABIs = Build.SUPPORTED_ABIS.toSet(),
        )
    }
}
