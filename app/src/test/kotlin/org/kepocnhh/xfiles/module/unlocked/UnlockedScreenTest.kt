package org.kepocnhh.xfiles.module.unlocked

import android.content.ClipboardManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
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
import org.kepocnhh.xfiles.entity.mockUUID
import org.kepocnhh.xfiles.mockBytes
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
import org.kepocnhh.xfiles.waitZero
import org.robolectric.RobolectricTestRunner
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import kotlin.time.Duration.Companion.seconds

@Suppress(
    "StringLiteralDuplication",
    "NonBooleanPropertyPrefixedWithIs",
    "LargeClass",
    "IgnoredReturnValue",
    "MultilineLambdaItParameter",
)
@RunWith(RobolectricTestRunner::class)
internal class UnlockedScreenTest {
    @get:Rule
    val rule = createAndroidComposeRule<TestActivity>()

    @Before
    fun before() {
        App.clearStores()
    }

    @Suppress("LongMethod")
    @Test(timeout = 2_000)
    fun backTest() {
        val issuer = "UnlockedScreenTest:backTest"
        val locked = AtomicBoolean(false)
        val dataBase = mockDataBase()
        val dataBaseDecrypted = mockBytes(issuer)
        val dataBaseEncrypted = mockBytes(issuer)
        val pathNames = mockPathNames()
        val symmetric = mockKeyMeta()
        val symmetricDecrypted = mockBytes(issuer)
        val secretKey = MockSecretKey(issuer = issuer)
        val injection = mockInjection(
            local = MockLocalDataProvider(services = mockSecurityServices()),
            pathNames = pathNames,
            security = {
                MockSecurityProvider(
                    cipher = MockCipherProvider(
                        values = listOf(
                            MockCipherProvider.DataSet(
                                encrypted = dataBaseEncrypted,
                                decrypted = dataBaseDecrypted,
                                secretKey = secretKey,
                            ),
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
                    key = secretKey,
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

    @Suppress("LongMethod")
    @Test(timeout = 2_000)
    fun onLockTest() {
        val issuer = "UnlockedScreenTest:onLockTest"
        val locked = AtomicBoolean(false)
        val dataBase = mockDataBase()
        val dataBaseDecrypted = mockBytes(issuer)
        val dataBaseEncrypted = mockBytes(issuer)
        val pathNames = mockPathNames()
        val symmetric = mockKeyMeta()
        val symmetricDecrypted = mockBytes(issuer)
        val secretKey = MockSecretKey(issuer = issuer)
        val injection = mockInjection(
            local = MockLocalDataProvider(services = mockSecurityServices()),
            pathNames = pathNames,
            security = {
                MockSecurityProvider(
                    cipher = MockCipherProvider(
                        values = listOf(
                            MockCipherProvider.DataSet(
                                encrypted = dataBaseEncrypted,
                                decrypted = dataBaseDecrypted,
                                secretKey = secretKey,
                            ),
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
                    key = secretKey,
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
        val issuer = "UnlockedScreenTest:emptyTest"
        val dataBase = mockDataBase()
        val dataBaseDecrypted = mockBytes(issuer)
        val dataBaseEncrypted = mockBytes(issuer)
        val symmetric = mockKeyMeta()
        val symmetricDecrypted = mockBytes(issuer)
        val pathNames = mockPathNames()
        val secretKey = MockSecretKey(issuer = issuer)
        val injection = mockInjection(
            local = MockLocalDataProvider(services = mockSecurityServices()),
            pathNames = pathNames,
            security = {
                MockSecurityProvider(
                    cipher = MockCipherProvider(
                        values = listOf(
                            MockCipherProvider.DataSet(
                                encrypted = dataBaseEncrypted,
                                decrypted = dataBaseDecrypted,
                                secretKey = secretKey,
                            ),
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
                key = secretKey,
                broadcast = {
                    error("Illegal state!")
                },
            )
        }
        val isEmpty = hasContentDescription("UnlockedScreen:empty")
        rule.waitOne(isEmpty)
    }

    @Suppress("LongMethod")
    @Test(timeout = 2_000)
    fun filledTest() {
        val issuer = "UnlockedScreenTest:filledTest"
        val dataBase = mockDataBase(
            secrets = (1..4).associate { number ->
                mockUUID() to ("title:$number" to "secret:$number")
            },
        )
        val dataBaseDecrypted = mockBytes(issuer)
        val dataBaseEncrypted = mockBytes(issuer)
        check(dataBase.secrets.size == 4)
        val symmetric = mockKeyMeta()
        val symmetricDecrypted = mockBytes(issuer)
        val pathNames = mockPathNames()
        val secretKey = MockSecretKey(issuer = issuer)
        val injection = mockInjection(
            local = MockLocalDataProvider(services = mockSecurityServices()),
            pathNames = pathNames,
            security = {
                MockSecurityProvider(
                    cipher = MockCipherProvider(
                        values = listOf(
                            MockCipherProvider.DataSet(
                                encrypted = dataBaseEncrypted,
                                decrypted = dataBaseDecrypted,
                                secretKey = secretKey,
                            ),
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
                key = secretKey,
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

    @Suppress("LongMethod")
    @Test(timeout = 2_000)
    fun showTest() {
        val issuer = "UnlockedScreenTest:showTest"
        val id = mockUUID()
        val title = "UnlockedScreenTest:showTest:title"
        val secret = "UnlockedScreenTest:showTest:secret"
        val dataBase = mockDataBase(
            secrets = mapOf(id to (title to secret)),
        )
        val dataBaseDecrypted = mockBytes(issuer)
        val dataBaseEncrypted = mockBytes(issuer)
        check(dataBase.secrets.size == 1)
        val symmetric = mockKeyMeta()
        val symmetricDecrypted = mockBytes(issuer)
        val pathNames = mockPathNames()
        val secretKey = MockSecretKey(issuer = issuer)
        val injection = mockInjection(
            local = MockLocalDataProvider(services = mockSecurityServices()),
            pathNames = pathNames,
            security = {
                MockSecurityProvider(
                    cipher = MockCipherProvider(
                        values = listOf(
                            MockCipherProvider.DataSet(
                                encrypted = dataBaseEncrypted,
                                decrypted = dataBaseDecrypted,
                                secretKey = secretKey,
                            ),
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
                key = secretKey,
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

    @Suppress("LongMethod")
    @Test(timeout = 2_000)
    fun deleteTest() {
        val issuer = "UnlockedScreenTest:deleteTest"
        val id = mockUUID()
        val title = "UnlockedScreenTest:showTest:title"
        val secret = "UnlockedScreenTest:showTest:secret"
        val initDataBase = mockDataBase(
            secrets = mapOf(id to (title to secret)),
        )
        val initDataBaseDecrypted = mockBytes(issuer)
        val initDataBaseEncrypted = mockBytes(issuer)
        check(initDataBase.secrets.size == 1)
        val dataBaseRef = AtomicReference(initDataBaseEncrypted)
        val updated = 128.seconds
        val editedDataBase = initDataBase.copy(
            updated = updated,
            secrets = emptyMap(),
        )
        check(editedDataBase.secrets.isEmpty())
        val editedDataBaseDecrypted = mockBytes(issuer)
        val editedDataBaseEncrypted = mockBytes(issuer)
        val editedDataBaseSignature = mockBytes(issuer)
        check(initDataBase.updated.inWholeMilliseconds < editedDataBase.updated.inWholeMilliseconds)
        val symmetric = mockKeyMeta()
        val symmetricDecrypted = mockBytes(issuer)
        val asymmetric = mockAsymmetricKey(issuer = issuer)
        val asymmetricDecrypted = mockBytes(issuer)
        val pathNames = mockPathNames()
        val privateKey = MockPrivateKey(issuer = issuer)
        val publicKey = MockPublicKey(issuer = issuer)
        val secretKey = MockSecretKey(issuer = issuer)
        val injection = mockInjection(
            local = MockLocalDataProvider(services = mockSecurityServices()),
            pathNames = pathNames,
            security = {
                MockSecurityProvider(
                    cipher = MockCipherProvider(
                        values = listOf(
                            MockCipherProvider.DataSet(
                                encrypted = initDataBaseEncrypted,
                                decrypted = initDataBaseDecrypted,
                                secretKey = secretKey,
                            ),
                            MockCipherProvider.DataSet(
                                encrypted = editedDataBaseEncrypted,
                                decrypted = editedDataBaseDecrypted,
                                secretKey = secretKey,
                            ),
                            MockCipherProvider.DataSet(
                                encrypted = asymmetric.privateKeyEncrypted,
                                decrypted = privateKey.encoded,
                                secretKey = secretKey,
                            ),
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
                key = secretKey,
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

    @Suppress("LongMethod")
    @Test(timeout = 2_000)
    fun copyTest() {
        val issuer = "UnlockedScreenTest:copyTest"
        val id = mockUUID()
        val title = "$issuer:title"
        val secret = "$issuer:secret"
        val dataBase = mockDataBase(
            secrets = mapOf(id to (title to secret)),
        )
        val dataBaseDecrypted = mockBytes(issuer)
        val dataBaseEncrypted = mockBytes(issuer)
        check(dataBase.secrets.size == 1)
        val symmetric = mockKeyMeta(issuer = "$issuer:symmetric")
        val symmetricDecrypted = mockBytes(issuer)
        val pathNames = mockPathNames()
        val secretKey = MockSecretKey(issuer = issuer)
        val injection = mockInjection(
            local = MockLocalDataProvider(services = mockSecurityServices()),
            pathNames = pathNames,
            security = {
                MockSecurityProvider(
                    cipher = MockCipherProvider(
                        values = listOf(
                            MockCipherProvider.DataSet(
                                encrypted = dataBaseEncrypted,
                                decrypted = dataBaseDecrypted,
                                secretKey = secretKey,
                            ),
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
                key = secretKey,
                broadcast = {
                    error("Illegal state!")
                },
            )
        }
        val isTitle = hasContentDescription("UnlockedScreen:item:$id:title")
        rule.waitOne(isTitle and hasText(title))
        val clipboardManager = rule.activity.getSystemService(ClipboardManager::class.java)
            ?: error("No clipboard manager!")
        assertNull(clipboardManager.primaryClip?.getItemAt(0)?.text)
        val isButton = SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button)
        val isCopy = hasContentDescription("UnlockedScreen:item:$id:copy")
        rule.onNode(isButton and isCopy).performClick()
        val primaryClip = clipboardManager.primaryClip ?: error("No primary clip!")
        assertEquals(1, primaryClip.itemCount)
        val item = primaryClip.getItemAt(0) ?: error("No item!")
        val text = item.text ?: error("No text!")
        assertEquals(secret, text)
    }

    @Suppress("LongMethod")
    @Test(timeout = 2_000)
    fun disposeTest() {
        val issuer = "UnlockedScreenTest:disposeTest"
        val id = mockUUID()
        val title = "$issuer:title"
        val secret = "$issuer:secret"
        val dataBase = mockDataBase(
            secrets = mapOf(id to (title to secret)),
        )
        val dataBaseDecrypted = mockBytes(issuer)
        val dataBaseEncrypted = mockBytes(issuer)
        check(dataBase.secrets.size == 1)
        val symmetric = mockKeyMeta(issuer = "$issuer:symmetric")
        val symmetricDecrypted = mockBytes(issuer)
        val pathNames = mockPathNames()
        val secretKey = MockSecretKey(issuer = issuer)
        val injection = mockInjection(
            local = MockLocalDataProvider(services = mockSecurityServices()),
            pathNames = pathNames,
            security = {
                MockSecurityProvider(
                    cipher = MockCipherProvider(
                        values = listOf(
                            MockCipherProvider.DataSet(
                                encrypted = dataBaseEncrypted,
                                decrypted = dataBaseDecrypted,
                                secretKey = secretKey,
                            ),
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
            Box {
                val visible = remember { mutableStateOf(true) }
                if (visible.value) {
                    UnlockedScreen(
                        key = secretKey,
                        broadcast = {
                            error("Illegal state!")
                        },
                    )
                }
                BasicText(
                    modifier = Modifier
                        .semantics {
                            role = Role.Button
                            contentDescription = "$issuer:hide"
                        }
                        .clickable {
                            visible.value = false
                        },
                    text = "hide",
                )
            }
        }
        val isTitle = hasContentDescription("UnlockedScreen:item:$id:title")
        rule.waitOne(isTitle and hasText(title))
        val isButton = SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button)
        val isCopy = hasContentDescription("UnlockedScreen:item:$id:copy")
        rule.onNode(isButton and isCopy).performClick()
        val clipboardManager = rule.activity.getSystemService(ClipboardManager::class.java)
            ?: error("No clipboard manager!")
        val primaryClip = clipboardManager.primaryClip ?: error("No primary clip!")
        assertEquals(1, primaryClip.itemCount)
        val item = primaryClip.getItemAt(0) ?: error("No item!")
        assertEquals(secret, item.text)
        val isHide = hasContentDescription("$issuer:hide")
        rule.onNode(isButton and isHide).performClick()
        rule.waitZero(isTitle and hasText(title))
        val text = clipboardManager.primaryClip?.getItemAt(0)?.text ?: error("No text!")
        assertTrue(text.isEmpty())
    }
}
