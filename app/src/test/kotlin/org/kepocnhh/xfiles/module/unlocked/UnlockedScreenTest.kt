package org.kepocnhh.xfiles.module.unlocked

import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
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
import org.kepocnhh.xfiles.entity.MockPrivateKey
import org.kepocnhh.xfiles.entity.MockPublicKey
import org.kepocnhh.xfiles.entity.MockSecretKey
import org.kepocnhh.xfiles.entity.mockAsymmetricKey
import org.kepocnhh.xfiles.entity.mockDataBase
import org.kepocnhh.xfiles.entity.mockKeyMeta
import org.kepocnhh.xfiles.entity.mockSecurityServices
import org.kepocnhh.xfiles.module.app.mockEncrypted
import org.kepocnhh.xfiles.module.app.mockInjection
import org.kepocnhh.xfiles.module.app.mockThemeState
import org.kepocnhh.xfiles.provider.MockEncryptedFileProvider
import org.kepocnhh.xfiles.provider.MockSerializer
import org.kepocnhh.xfiles.provider.MockTimeProvider
import org.kepocnhh.xfiles.provider.data.MockLocalDataProvider
import org.kepocnhh.xfiles.provider.mockPathNames
import org.kepocnhh.xfiles.provider.security.MockCipherProvider
import org.kepocnhh.xfiles.provider.security.MockKeyFactoryProvider
import org.kepocnhh.xfiles.provider.security.MockSecurityProvider
import org.kepocnhh.xfiles.provider.security.MockSignatureProvider
import org.kepocnhh.xfiles.setContent
import org.kepocnhh.xfiles.setInjection
import org.kepocnhh.xfiles.waitOne
import org.robolectric.RobolectricTestRunner
import java.util.UUID
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import kotlin.time.Duration.Companion.seconds

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
        val isTitle = hasContentDescription("UnlockedScreen:item:$id:title")
        rule.waitOne(isTitle and hasText(title))
        val isSecret = hasContentDescription("UnlockedScreen:secret")
        rule.onNode(isSecret).assertDoesNotExist()
        val isButton = SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button)
        val isShow = hasContentDescription("UnlockedScreen:item:$id:show")
        rule.onNode(isButton and isShow).performClick()
        rule.waitOne(isSecret and hasText(secret))
    }

    @Test(timeout = 2_000)
    fun deleteTest() {
        val id = UUID.randomUUID()
        val title = "UnlockedScreenTest:showTest:title"
        val secret = "UnlockedScreenTest:showTest:secret"
        val initDataBase = mockDataBase(
            secrets = mapOf(id to (title to secret)),
        )
        val initDataBaseDecrypted = "initDataBase:decrypted".toByteArray()
        val initDataBaseEncrypted = "initDataBase:encrypted".toByteArray()
        check(initDataBase.secrets.size == 1)
        val dataBaseRef = AtomicReference(initDataBaseEncrypted)
        val updated = 128.seconds
        val editedDataBase = initDataBase.copy(
            updated = updated,
            secrets = emptyMap(),
        )
        check(editedDataBase.secrets.isEmpty())
        val editedDataBaseDecrypted = "editedDataBase:decrypted".toByteArray()
        val editedDataBaseEncrypted = "editedDataBase:encrypted".toByteArray()
        val editedDataBaseSignature = "editedDataBase:signature".toByteArray()
        check(initDataBase.updated.inWholeMilliseconds < editedDataBase.updated.inWholeMilliseconds)
        val symmetric = mockKeyMeta()
        val symmetricDecrypted = "symmetric:decrypted".toByteArray()
        val asymmetric = mockAsymmetricKey()
        val asymmetricDecrypted = "asymmetric:decrypted".toByteArray()
        val pathNames = mockPathNames()
        val privateKey = MockPrivateKey("UnlockedScreenTest:deleteTest:privateKey".toByteArray())
        val publicKey = MockPublicKey("UnlockedScreenTest:deleteTest:publicKey".toByteArray())
        val key = MockSecretKey("UnlockedScreenTest:deleteTest:secretKey".toByteArray())
        val injection = mockInjection(
            local = MockLocalDataProvider(services = mockSecurityServices()),
            pathNames = pathNames,
            security = {
                MockSecurityProvider(
                    cipher = MockCipherProvider(
                        values = listOf(
                            Triple(initDataBaseEncrypted, initDataBaseDecrypted, key),
                            Triple(editedDataBaseEncrypted, editedDataBaseDecrypted, key),
                            Triple(asymmetric.privateEncrypted, privateKey.encoded, key),
                        ),
                    ),
                    keyFactory = MockKeyFactoryProvider(privateKey = privateKey),
                    signature = MockSignatureProvider(
                        dataSets = listOf(
                            MockSignatureProvider.DataSet(
                                decrypted = editedDataBaseDecrypted,
                                sig = editedDataBaseSignature,
                                privateKey = privateKey,
                                publicKey = publicKey,
                            ),
                        ),
                    ),
                )
            },
            encrypted = mockEncrypted(
                files = MockEncryptedFileProvider(
                    inputs = mapOf(
                        pathNames.symmetric to symmetricDecrypted,
                        pathNames.asymmetric to asymmetricDecrypted,
                    ),
                    refs = mapOf(
                        pathNames.dataBase to dataBaseRef,
                    ),
                ),
            ),
            serializer = MockSerializer(
                values = mapOf(
                    symmetric to symmetricDecrypted,
                    asymmetric to asymmetricDecrypted,
                    initDataBase to initDataBaseDecrypted,
                    editedDataBase to editedDataBaseDecrypted,
                ),
            ),
            time = MockTimeProvider(now = updated),
        )
        val strings = rule.setContent(injection) {
            UnlockedScreen(
                key = key,
                broadcast = {
                    error("Illegal state!")
                },
            )
        }
        val isEmpty = hasContentDescription("UnlockedScreen:empty")
        rule.onNode(isEmpty).assertDoesNotExist()
        val isTitle = hasContentDescription("UnlockedScreen:item:$id:title")
        rule.waitOne(isTitle and hasText(title))
        val dialogText = String.format(strings.unlocked.deleteItem, title)
        rule.onNode(hasText(dialogText)).assertDoesNotExist()
        val isButton = SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button)
        val isDelete = hasContentDescription("UnlockedScreen:item:$id:delete")
        rule.onNode(isButton and isDelete).performClick()
        rule.onNode(hasText(dialogText)).assertIsDisplayed()
        rule.waitOne(isTitle and hasText(title))
        rule.onNode(hasText(strings.yes)).performClick()
        rule.waitOne(isEmpty)
        rule.onNode(isTitle and hasText(title)).assertDoesNotExist()
    }
}
