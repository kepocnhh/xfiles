package org.kepocnhh.xfiles.module.unlocked

import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.kepocnhh.xfiles.App
import org.kepocnhh.xfiles.TestActivity
import org.kepocnhh.xfiles.clearStores
import org.kepocnhh.xfiles.entity.MockSecretKey
import org.kepocnhh.xfiles.entity.mockDataBase
import org.kepocnhh.xfiles.entity.mockKeyMeta
import org.kepocnhh.xfiles.entity.mockSecurityServices
import org.kepocnhh.xfiles.module.app.mockEncrypted
import org.kepocnhh.xfiles.module.app.mockInjection
import org.kepocnhh.xfiles.module.app.mockThemeState
import org.kepocnhh.xfiles.provider.MockEncryptedFileProvider
import org.kepocnhh.xfiles.provider.MockSerializer
import org.kepocnhh.xfiles.provider.data.MockLocalDataProvider
import org.kepocnhh.xfiles.provider.mockPathNames
import org.kepocnhh.xfiles.provider.security.MockCipherProvider
import org.kepocnhh.xfiles.provider.security.MockSecurityProvider
import org.kepocnhh.xfiles.setInjection
import org.robolectric.RobolectricTestRunner
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

@RunWith(RobolectricTestRunner::class)
internal class UnlockedScreenTest {
    @get:Rule
    val rule = createAndroidComposeRule<TestActivity>()

    @Before
    fun before() {
        App.clearStores()
    }

    @Test(timeout = 2_000)
    fun backTest() {
        val locked = AtomicBoolean(false)
        val dataBase = mockDataBase()
        val dataBaseDecrypted = "dataBase:decrypted".toByteArray()
        val dataBaseEncrypted = "dataBase:encrypted".toByteArray()
        val pathNames = mockPathNames()
        val symmetric = mockKeyMeta()
        val symmetricDecrypted = "symmetric:decrypted".toByteArray()
        val key = MockSecretKey("UnlockedScreenTest:backTest:SecretKey".toByteArray())
        val injection = mockInjection(
            local = MockLocalDataProvider(services = mockSecurityServices()),
            pathNames = pathNames,
            security = {
                MockSecurityProvider(
                    cipher = MockCipherProvider(
                        values = listOf(
                            Triple(dataBaseEncrypted, dataBaseDecrypted, key),
                        ),
                    ),
                )
            },
            encrypted = mockEncrypted(
                files = MockEncryptedFileProvider(
                    inputs = mapOf(
                        pathNames.symmetric to symmetricDecrypted,
                    ),
                    refs = mapOf(
                        pathNames.dataBase to AtomicReference(dataBaseEncrypted),
                    ),
                ),
            ),
            serializer = MockSerializer(
                values = mapOf(
                    symmetric to symmetricDecrypted,
                    dataBase to dataBaseDecrypted,
                ),
            ),
        )
        App.setInjection(injection)
        rule.setContent {
            App.Theme.Composition(themeState = mockThemeState()) {
                UnlockedScreen(
                    key = key,
                    broadcast = {
                        when (it) {
                            UnlockedScreen.Broadcast.Lock -> {
                                locked.set(true)
                            }
                        }
                    },
                )
            }
        }
        assertFalse(locked.get())
        Espresso.pressBack()
        rule.waitUntil {
            locked.get()
        }
    }

    @Test(timeout = 2_000)
    fun onLockTest() {
        val locked = AtomicBoolean(false)
        val dataBase = mockDataBase()
        val dataBaseDecrypted = "dataBase:decrypted".toByteArray()
        val dataBaseEncrypted = "dataBase:encrypted".toByteArray()
        val pathNames = mockPathNames()
        val symmetric = mockKeyMeta()
        val symmetricDecrypted = "symmetric:decrypted".toByteArray()
        val key = MockSecretKey()
        val injection = mockInjection(
            local = MockLocalDataProvider(services = mockSecurityServices()),
            pathNames = pathNames,
            security = {
                MockSecurityProvider(
                    cipher = MockCipherProvider(
                        values = listOf(
                            Triple(dataBaseEncrypted, dataBaseDecrypted, key),
                        ),
                    ),
                )
            },
            encrypted = mockEncrypted(
                files = MockEncryptedFileProvider(
                    inputs = mapOf(
                        pathNames.symmetric to symmetricDecrypted,
                    ),
                    refs = mapOf(
                        pathNames.dataBase to AtomicReference(dataBaseEncrypted),
                    ),
                ),
            ),
            serializer = MockSerializer(
                values = mapOf(
                    symmetric to symmetricDecrypted,
                    dataBase to dataBaseDecrypted,
                ),
            ),
        )
        App.setInjection(injection)
        rule.setContent {
            App.Theme.Composition(themeState = mockThemeState()) {
                UnlockedScreen(
                    key = key,
                    broadcast = {
                        when (it) {
                            UnlockedScreen.Broadcast.Lock -> {
                                locked.set(true)
                            }
                        }
                    },
                )
            }
        }
        assertFalse(locked.get())
        val isButton = SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button)
        val isLock = hasContentDescription("UnlockedScreen:lock")
        rule.onNode(isButton and isLock).performClick()
        rule.waitUntil {
            locked.get()
        }
    }
}
