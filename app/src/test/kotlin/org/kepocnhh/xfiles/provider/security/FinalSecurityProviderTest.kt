package org.kepocnhh.xfiles.provider.security

import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.kepocnhh.xfiles.entity.SecurityServices
import org.kepocnhh.xfiles.entity.mockSecurityServices
import org.kepocnhh.xfiles.util.security.SecurityUtil
import org.kepocnhh.xfiles.util.security.requireService
import org.kepocnhh.xfiles.util.security.toSecurityService
import org.robolectric.RobolectricTestRunner
import java.math.BigInteger

@RunWith(RobolectricTestRunner::class)
internal class FinalSecurityProviderTest {
    @Test(timeout = 2_000)
    fun uuidsTest() {
        val issuer = "FinalSecurityProviderTest:uuidsTest"
        val services: SecurityServices = mockSecurityServices(issuer = issuer)
        val provider: SecurityProvider = FinalSecurityProvider(services = services)
        val actual = provider.uuids().generate()
        Assert.assertEquals(36, actual.toString().length)
        Assert.assertEquals(4, actual.toString().filter { it == '-' }.length)
        val unexpected = provider.uuids().generate()
        Assert.assertNotEquals(unexpected, actual)
        Assert.assertNotEquals(provider.uuids().generate(), actual)
        Assert.assertNotEquals(unexpected, provider.uuids().generate())
    }

    @Test(timeout = 2_000)
    fun md5Test() {
        val issuer = "FinalSecurityProviderTest:md5Test"
        val services: SecurityServices = mockSecurityServices(
            md5 = SecurityUtil
                .requireProvider(BouncyCastleProvider.PROVIDER_NAME)
                .requireService(type = "MessageDigest", algorithm = "MD5")
                .toSecurityService(),
        )
        val provider: SecurityProvider = FinalSecurityProvider(services = services)
        val digest = provider.getMessageDigest(HashAlgorithm.MD5).digest(issuer.toByteArray())
        val actual = String.format("%032x", BigInteger(1, digest))
        Assert.assertEquals("947504882f12496b2d428c21f1961adc", actual)
    }

    @Test(timeout = 2_000)
    fun sha512Test() {
        val issuer = "FinalSecurityProviderTest:sha512Test"
        val services: SecurityServices = mockSecurityServices(
            sha512 = SecurityUtil
                .requireProvider(BouncyCastleProvider.PROVIDER_NAME)
                .requireService(type = "MessageDigest", algorithm = "SHA-512")
                .toSecurityService(),
        )
        val provider: SecurityProvider = FinalSecurityProvider(services = services)
        val digest = provider.getMessageDigest(HashAlgorithm.SHA512).digest(issuer.toByteArray())
        val actual = String.format("%032x", BigInteger(1, digest))
        val expected = "48328a5165fa362902fa1abec0d0a7a6cb0a26b1520eb328e207c4d0012201fba5d36bcbc0c5831df1e02fd06f1e44e66a20437ebde67dae7ad9a55abbfe9ca1"
        Assert.assertEquals(expected, actual)
    }
}
