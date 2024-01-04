package org.kepocnhh.xfiles.provider

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
internal class FinalTimeProviderTest {
    @Test(timeout = 2_000)
    fun getCurrentDeviceTest() {
        val provider: TimeProvider = FinalTimeProvider
        val actual = provider.now()
        Assert.assertTrue(System.currentTimeMillis() >= actual.inWholeMilliseconds)
    }
}
