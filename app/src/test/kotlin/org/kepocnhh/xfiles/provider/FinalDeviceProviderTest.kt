package org.kepocnhh.xfiles.provider

import android.os.Build
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.kepocnhh.xfiles.entity.SecurityServices
import org.kepocnhh.xfiles.entity.mockDevice
import org.kepocnhh.xfiles.entity.mockSecurityServices
import org.kepocnhh.xfiles.provider.security.FinalSecurityProvider
import org.kepocnhh.xfiles.provider.security.HashAlgorithm
import org.kepocnhh.xfiles.provider.security.MessageDigestProvider
import org.kepocnhh.xfiles.provider.security.MockMessageDigestProvider
import org.kepocnhh.xfiles.util.security.SecurityUtil
import org.kepocnhh.xfiles.util.security.requireService
import org.kepocnhh.xfiles.util.security.toSecurityService
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowBuild
import org.robolectric.util.ReflectionHelpers
import java.util.UUID

@RunWith(RobolectricTestRunner::class)
internal class FinalDeviceProviderTest {
    @Test(timeout = 2_000)
    fun getCurrentDeviceTest() {
        val issuer = "FinalDeviceProviderTest:getCurrentDeviceTest"
        val provider: DeviceProvider = FinalDeviceProvider(md5 = MockMessageDigestProvider())
        val expected = mockDevice(issuer = issuer)
        ShadowBuild.setManufacturer(expected.manufacturer)
        ShadowBuild.setBrand(expected.brand)
        ShadowBuild.setModel(expected.model)
        ShadowBuild.setDevice(expected.name)
        check(expected.supportedABIs.size > 1)
        ReflectionHelpers.setStaticField(Build::class.java, "SUPPORTED_ABIS", expected.supportedABIs.toTypedArray())
        val actual = provider.getCurrentDevice()
        Assert.assertEquals(expected, actual)
    }

    @Test(timeout = 2_000)
    fun toUUIDTest() {
        val issuer = "FinalDeviceProviderTest:toUUIDTest"
        val services: SecurityServices = mockSecurityServices(
            md5 = SecurityUtil
                .requireProvider(BouncyCastleProvider.PROVIDER_NAME)
                .requireService(type = "MessageDigest", algorithm = "MD5")
                .toSecurityService(),
        )
        val md5: MessageDigestProvider = FinalSecurityProvider(services = services)
            .getMessageDigest(HashAlgorithm.MD5)
        val provider: DeviceProvider = FinalDeviceProvider(md5 = md5)
        listOf(
            "574a8042-abf9-a5ba-d8ac-927797c48598" to mockDevice(issuer = issuer),
            "ef568358-4036-37d2-c4c5-73500041d1b1" to mockDevice(issuer = "foobar"),
            "f693bdea-9727-c9b5-0adb-02cd3c4f25a5" to mockDevice(issuer = "345442"),
        ).forEach { (expected, device) ->
            val actual = provider.toUUID(device)
            Assert.assertEquals(UUID.fromString(expected), actual)
        }
    }
}
