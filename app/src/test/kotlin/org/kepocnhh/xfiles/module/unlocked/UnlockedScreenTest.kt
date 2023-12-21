package org.kepocnhh.xfiles.module.unlocked

import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
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
import org.kepocnhh.xfiles.setContent
import org.kepocnhh.xfiles.setInjection
import org.kepocnhh.xfiles.waitOne
import org.robolectric.RobolectricTestRunner
import java.util.UUID
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

    @Test(timeout = 2_000)
    fun emptyTest() {
        val dataBase = mockDataBase()
        val dataBaseDecrypted = "dataBase:decrypted".toByteArray()
        val dataBaseEncrypted = "dataBase:encrypted".toByteArray()
        val symmetric = mockKeyMeta()
        val symmetricDecrypted = "symmetric:decrypted".toByteArray()
        val pathNames = mockPathNames()
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
                        pathNames.dataBase to dataBaseEncrypted,
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
        rule.setContent(injection) {
            UnlockedScreen(
                key = key,
                broadcast = {
                    error("Illegal state!")
                },
            )
        }
        val isEmpty = hasContentDescription("UnlockedScreen:empty")
        rule.waitOne(isEmpty)
    }

    @Test(timeout = 2_000)
    fun filledTest() {
        val dataBase = mockDataBase(
            secrets = (1..4).associate { number ->
                UUID.randomUUID() to ("title:$number" to "secret:$number")
            },
        )
        val dataBaseDecrypted = "dataBase:decrypted".toByteArray()
        val dataBaseEncrypted = "dataBase:encrypted".toByteArray()
        check(dataBase.secrets.size == 4)
        val symmetric = mockKeyMeta()
        val symmetricDecrypted = "symmetric:decrypted".toByteArray()
        val pathNames = mockPathNames()
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
                        pathNames.dataBase to dataBaseEncrypted,
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
        rule.setContent(injection) {
            UnlockedScreen(
                key = key,
                broadcast = {
                    error("Illegal state!")
                },
            )
        }
        val isEmpty = hasContentDescription("UnlockedScreen:empty")
        rule.onNode(isEmpty).assertDoesNotExist()
        val isButton = SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button)
        dataBase.secrets.forEach { (uuid, pair) ->
            val (title, _) = pair
            val isTitle = hasContentDescription("UnlockedScreen:item:$uuid:title")
            rule.waitOne(isTitle and hasText(title))
            setOf("show", "copy", "delete").forEach { type ->
                rule.waitOne(isButton and hasContentDescription("UnlockedScreen:item:$uuid:$type"))
                rule.waitOne(hasContentDescription("UnlockedScreen:item:$uuid:$type:icon"))
            }
        }
    }

    @Test(timeout = 2_000)
    fun showTest() {
        val id = UUID.randomUUID()
        val title = "UnlockedScreenTest:showTest:title"
        val secret = "UnlockedScreenTest:showTest:secret"
        val dataBase = mockDataBase(
            secrets = mapOf(id to (title to secret)),
        )
        val dataBaseDecrypted = "dataBase:decrypted".toByteArray()
        val dataBaseEncrypted = "dataBase:encrypted".toByteArray()
        check(dataBase.secrets.size == 1)
        val symmetric = mockKeyMeta()
        val symmetricDecrypted = "symmetric:decrypted".toByteArray()
        val pathNames = mockPathNames()
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
                        pathNames.dataBase to dataBaseEncrypted,
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
        rule.setContent(injection) {
            UnlockedScreen(
                key = key,
                broadcast = {
                    error("Illegal state!")
                },
            )
        }
        val isSecret = hasContentDescription("UnlockedScreen:secret")
        rule.onNode(isSecret).assertDoesNotExist()
        val isButton = SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button)
        val isShow = hasContentDescription("UnlockedScreen:item:$id:show")
        rule.onNode(isButton and isShow).performClick()
        rule.waitOne(isSecret and hasText(secret))
    }
}
