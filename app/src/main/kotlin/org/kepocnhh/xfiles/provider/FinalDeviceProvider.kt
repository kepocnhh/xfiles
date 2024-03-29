package org.kepocnhh.xfiles.provider

import android.os.Build
import org.kepocnhh.xfiles.entity.Device
import org.kepocnhh.xfiles.provider.security.MessageDigestProvider
import java.nio.ByteBuffer
import java.util.UUID

internal class FinalDeviceProvider(
    private val md5: MessageDigestProvider,
) : DeviceProvider {
    override fun getCurrentDevice(): Device {
        return Device(
            manufacturer = Build.MANUFACTURER,
            brand = Build.BRAND,
            model = Build.MODEL,
            name = Build.DEVICE,
            supportedABIs = Build.SUPPORTED_ABIS.toSet(),
        )
    }

    override fun toUUID(device: Device): UUID {
        val input = mapOf(
            "manufacturer" to device.manufacturer,
            "brand" to device.brand,
            "model" to device.model,
            "name" to device.name,
            "supportedABIs" to device.supportedABIs.joinToString(prefix = "[", separator = ",", postfix = "]"),
        ).entries.joinToString(separator = ",") { (key, value) ->
            "$key=$value"
        }
        val digest = md5.digest(input.toByteArray())
        check(digest.size == 16)
        val buffer = ByteBuffer.wrap(digest)
        return UUID(buffer.long, buffer.long)
    }
}
