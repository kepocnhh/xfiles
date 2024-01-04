package org.kepocnhh.xfiles.provider.data

import androidx.test.core.app.ApplicationProvider
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.kepocnhh.xfiles.entity.Defaults
import org.kepocnhh.xfiles.entity.SecuritySettings
import org.kepocnhh.xfiles.entity.mockDevice
import org.kepocnhh.xfiles.entity.mockSecurityServices
import org.kepocnhh.xfiles.module.app.ColorsType
import org.kepocnhh.xfiles.module.app.Language
import org.kepocnhh.xfiles.module.app.ThemeState
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
internal class FinalLocalDataProviderTest {
    @Test(timeout = 2_000)
    fun updateTest() {
        val issuer = "FinalLocalDataProviderTest:updateTest"
        val defaults = Defaults(
            themeState = ThemeState(
                colorsType = ColorsType.AUTO,
                language = Language.AUTO,
            ),
            securitySettings = SecuritySettings(
                pbeIterations = SecuritySettings.PBEIterations.NUMBER_2_16,
                aesKeyLength = SecuritySettings.AESKeyLength.BITS_256,
                dsaKeyLength = SecuritySettings.DSAKeyLength.BITS_1024_2,
                hasBiometric = false,
            ),
        )
        val provider: LocalDataProvider = FinalLocalDataProvider(
            context = ApplicationProvider.getApplicationContext(),
            defaults = defaults,
        )
        Assert.assertNull(provider.device)
        Assert.assertNull(provider.services)
        Assert.assertEquals(defaults.themeState, provider.themeState)
        Assert.assertEquals(defaults.securitySettings, provider.securitySettings)
        val device = mockDevice(issuer = issuer)
        provider.device = device
        Assert.assertEquals(device, provider.device)
        provider.device = null
        Assert.assertNull(provider.device)
        val services = mockSecurityServices(issuer = issuer)
        provider.services = services
        Assert.assertEquals(services, provider.services)
        provider.services = null
        Assert.assertNull(provider.services)
        val themeState = ThemeState(
            colorsType = ColorsType.LIGHT,
            language = Language.RUSSIAN,
        )
        check(themeState != defaults.themeState)
        provider.themeState = themeState
        Assert.assertEquals(themeState, provider.themeState)
        val securitySettings = SecuritySettings(
            pbeIterations = SecuritySettings.PBEIterations.NUMBER_2_20,
            aesKeyLength = SecuritySettings.AESKeyLength.BITS_256,
            dsaKeyLength = SecuritySettings.DSAKeyLength.BITS_1024_3,
            hasBiometric = true,
        )
        check(securitySettings != defaults.securitySettings)
        provider.securitySettings = securitySettings
        Assert.assertEquals(securitySettings, provider.securitySettings)
    }
}
