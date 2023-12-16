package org.kepocnhh.xfiles.module.enter.settings

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.kepocnhh.xfiles.entity.mockSecurityService
import org.kepocnhh.xfiles.entity.mockSecurityServices
import org.kepocnhh.xfiles.module.app.mockInjection
import org.kepocnhh.xfiles.collect
import org.kepocnhh.xfiles.entity.SecuritySettings
import org.kepocnhh.xfiles.entity.mockSecuritySettings
import org.kepocnhh.xfiles.module.app.mockEncrypted
import org.kepocnhh.xfiles.onEachIndexed
import org.kepocnhh.xfiles.provider.MockEncryptedFileProvider
import org.kepocnhh.xfiles.provider.data.MockLocalDataProvider
import org.kepocnhh.xfiles.provider.mockContexts
import org.kepocnhh.xfiles.provider.mockPathNames
import kotlin.time.Duration.Companion.seconds

internal class SettingsViewModelTest {
    @Test
    fun requestCipherTest() {
        runTest(timeout = 10.seconds) {
            val init = mockSecurityService(
                provider = "SettingsViewModelTest:cipherTest:provider",
                algorithm = "SettingsViewModelTest:cipherTest:algorithm",
            )
            val injection = mockInjection(
                contexts = mockContexts(main = coroutineContext),
                local = MockLocalDataProvider(
                    services = mockSecurityServices(
                        cipher = init,
                    ),
                ),
            )
            val viewModel = SettingsViewModel(injection)
            viewModel
                .cipher
                .onEachIndexed { (index, value) ->
                    when (index) {
                        0 -> {
                            assertNull(value)
                            viewModel.requestCipher()
                        }
                        1 -> {
                            assertNotNull(value)
                            checkNotNull(value)
                            assertEquals(init, value)
                        }
                        else -> error("Unexpected index: $index!")
                    }
                }
                .collect(2)
        }
    }

    @Test
    fun requestSettingsTest() {
        runTest(timeout = 10.seconds) {
            val init = mockSecuritySettings(
                aesKeyLength = SecuritySettings.AESKeyLength.BITS_256,
                pbeIterations = SecuritySettings.PBEIterations.NUMBER_2_20,
                dsaKeyLength = SecuritySettings.DSAKeyLength.BITS_1024_3,
                hasBiometric = true,
            )
            val injection = mockInjection(
                contexts = mockContexts(main = coroutineContext),
                local = MockLocalDataProvider(
                    securitySettings = init,
                ),
            )
            val viewModel = SettingsViewModel(injection)
            viewModel
                .settings
                .onEachIndexed { (index, value) ->
                    when (index) {
                        0 -> {
                            assertNull(value)
                            viewModel.requestSettings()
                        }
                        1 -> {
                            assertNotNull(value)
                            checkNotNull(value)
                            assertEquals(init, value)
                        }
                        else -> error("Unexpected index: $index!")
                    }
                }
                .collect(2)
        }
    }

    @Test
    fun databaseExistsTest() {
        runTest(timeout = 10.seconds) {
            val dataBase = "SettingsViewModelTest:databaseExistsTest:dataBase"
            val injection = mockInjection(
                contexts = mockContexts(main = coroutineContext),
                pathNames = mockPathNames(
                    dataBase = dataBase,
                ),
                encrypted = mockEncrypted(
                    files = MockEncryptedFileProvider(
                        exists = setOf(dataBase),
                    ),
                ),
            )
            val viewModel = SettingsViewModel(injection)
            viewModel
                .databaseExists
                .onEachIndexed { (index, value) ->
                    when (index) {
                        0 -> {
                            assertNull(value)
                            viewModel.requestDatabase()
                        }
                        1 -> {
                            assertNotNull(value)
                            checkNotNull(value)
                            assertTrue(value)
                        }
                        else -> error("Unexpected index: $index!")
                    }
                }
                .collect(2)
        }
    }

    @Test
    fun databaseNotExistsTest() {
        runTest(timeout = 10.seconds) {
            val injection = mockInjection(
                contexts = mockContexts(main = coroutineContext),
            )
            val viewModel = SettingsViewModel(injection)
            viewModel
                .databaseExists
                .onEachIndexed { (index, value) ->
                    when (index) {
                        0 -> {
                            assertNull(value)
                            viewModel.requestDatabase()
                        }
                        1 -> {
                            assertNotNull(value)
                            checkNotNull(value)
                            assertFalse(value)
                        }
                        else -> error("Unexpected index: $index!")
                    }
                }
                .collect(2)
        }
    }

    @Test
    fun setSettingsTest() {
        runTest(timeout = 10.seconds) {
            val init = mockSecuritySettings(
                aesKeyLength = SecuritySettings.AESKeyLength.BITS_256,
                pbeIterations = SecuritySettings.PBEIterations.NUMBER_2_20,
                dsaKeyLength = SecuritySettings.DSAKeyLength.BITS_1024_3,
                hasBiometric = true,
            )
            val injection = mockInjection(
                contexts = mockContexts(main = coroutineContext),
                local = MockLocalDataProvider(
                    securitySettings = init,
                ),
            )
            val expected = mockSecuritySettings(
                aesKeyLength = SecuritySettings.AESKeyLength.BITS_256,
                pbeIterations = SecuritySettings.PBEIterations.NUMBER_2_16,
                dsaKeyLength = SecuritySettings.DSAKeyLength.BITS_1024_2,
                hasBiometric = false,
            )
            check(expected != init)
            val viewModel = SettingsViewModel(injection)
            viewModel
                .settings
                .onEachIndexed { (index, value) ->
                    when (index) {
                        0 -> {
                            assertNull(value)
                            viewModel.requestSettings()
                        }
                        1 -> {
                            assertNotNull(value)
                            checkNotNull(value)
                            assertEquals(init, value)
                            viewModel.setSettings(expected)
                        }
                        2 -> {
                            assertNotNull(value)
                            checkNotNull(value)
                            assertEquals(expected, value)
                        }
                        else -> error("Unexpected index: $index!")
                    }
                }
                .collect(3)
        }
    }
}
