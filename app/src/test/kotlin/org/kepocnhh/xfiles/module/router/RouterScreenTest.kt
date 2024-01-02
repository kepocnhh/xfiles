package org.kepocnhh.xfiles.module.router

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.kepocnhh.xfiles.App
import org.kepocnhh.xfiles.TestActivity
import org.kepocnhh.xfiles.TestProvider
import org.kepocnhh.xfiles.clearStores
import org.kepocnhh.xfiles.entity.MockSecretKey
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
import org.kepocnhh.xfiles.provider.data.MockLocalDataProvider
import org.kepocnhh.xfiles.provider.mockPathNames
import org.kepocnhh.xfiles.provider.security.MockCipherProvider
import org.kepocnhh.xfiles.provider.security.MockSecurityProvider
import org.kepocnhh.xfiles.setContent
import org.kepocnhh.xfiles.setInjection
import org.kepocnhh.xfiles.waitOne
import org.kepocnhh.xfiles.waitUntilTrue
import org.robolectric.RobolectricTestRunner
import java.security.Security
import java.util.concurrent.atomic.AtomicBoolean
import javax.crypto.SecretKey

@RunWith(RobolectricTestRunner::class)
internal class RouterScreenTest {
    @get:Rule
    val rule = createAndroidComposeRule<TestActivity>()

    @Before
    fun before() {
        App.clearStores()
    }

    @After
    fun after() {
        Security.removeProvider("AndroidOpenSSL")
    }

    @Test(timeout = 2_000)
    fun backTest() {
        val backRef = AtomicBoolean(false)
        val injection = mockInjection()
        App.setInjection(injection)
        rule.setContent {
            App.Theme.Composition(themeState = mockThemeState()) {
                RouterScreen(
                    onBack = {
                        backRef.set(true)
                    },
                )
            }
        }
        check(!backRef.get())
        val isButton = SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button)
        val isExit = hasContentDescription("ChecksScreen:exit")
        rule.onNode(isButton and isExit).performClick()
        rule.waitUntilTrue(backRef)
    }

    @Test(timeout = 2_000)
    fun enterScreenNoDatabaseTest() {
        val issuer = "RouterScreenTest:enterScreenNoDatabaseTest"
        val provider = TestProvider(
            name = "AndroidOpenSSL",
            services = listOf(
                "MessageDigest" to "SHA-512",
                "SecureRandom" to "SHA1PRNG",
            ),
        )
        Security.insertProviderAt(provider, 0)
        val pathNames = mockPathNames(issuer = issuer)
        val injection = mockInjection(
            pathNames = pathNames,
        )
        val strings = rule.setContent(injection) {
            RouterScreen(
                onBack = {
                    error("Illegal state!")
                },
            )
        }
        val isTraversalGroup = SemanticsMatcher.expectValue(SemanticsProperties.IsTraversalGroup, true)
        val isEnterScreen = hasContentDescription("EnterScreen")
        rule.waitOne(isTraversalGroup and isEnterScreen)
        rule.onNode(isTraversalGroup and isEnterScreen).assertIsDisplayed()
        check(!injection.encrypted.files.exists(pathNames.dataBase))
        val isNoDatabase = hasContentDescription("EnterScreen:noDatabase")
        rule.onNode(isNoDatabase).assertTextEquals(strings.noDatabase)
        val isUnlockedScreen = hasContentDescription("UnlockedScreen")
        rule.onNode(isTraversalGroup and isUnlockedScreen).assertDoesNotExist()
    }

    @Test(timeout = 2_000)
    fun enterScreenTest() {
        val issuer = "RouterScreenTest:enterScreenTest"
        val provider = TestProvider(
            name = "AndroidOpenSSL",
            services = listOf(
                "MessageDigest" to "SHA-512",
                "SecureRandom" to "SHA1PRNG",
            ),
        )
        Security.insertProviderAt(provider, 0)
        val pathNames = mockPathNames(issuer = issuer)
        val injection = mockInjection(
            pathNames = pathNames,
            encrypted = mockEncrypted(
                files = MockEncryptedFileProvider(
                    exists = setOf(pathNames.dataBase),
                ),
            ),
        )
        val strings = rule.setContent(injection) {
            RouterScreen(
                onBack = {
                    error("Illegal state!")
                },
            )
        }
        val isTraversalGroup = SemanticsMatcher.expectValue(SemanticsProperties.IsTraversalGroup, true)
        val isEnterScreen = hasContentDescription("EnterScreen")
        rule.waitOne(isTraversalGroup and isEnterScreen)
        rule.onNode(isTraversalGroup and isEnterScreen).assertIsDisplayed()
        check(injection.encrypted.files.exists(pathNames.dataBase))
        val isDatabaseExists = hasContentDescription("EnterScreen:databaseExists")
        rule.onNode(isDatabaseExists).assertTextEquals(strings.databaseExists)
        val isUnlockedScreen = hasContentDescription("UnlockedScreen")
        rule.onNode(isTraversalGroup and isUnlockedScreen).assertDoesNotExist()
    }

    @Test(timeout = 2_000)
    fun unlockedScreenEmptyTest() {
        val issuer = "RouterScreenTest:unlockedScreenTest"
        val provider = TestProvider(
            name = "AndroidOpenSSL",
            services = listOf(
                "MessageDigest" to "SHA-512",
                "SecureRandom" to "SHA1PRNG",
            ),
        )
        Security.insertProviderAt(provider, 0)
        val pathNames = mockPathNames(issuer = issuer)
        val dataBase = mockDataBase()
        val dataBaseDecrypted = mockBytes(issuer)
        val dataBaseEncrypted = mockBytes(issuer)
        val symmetric = mockKeyMeta(issuer = issuer)
        val symmetricDecrypted = mockBytes(issuer)
        val securityServices = mockSecurityServices(issuer = issuer)
        val secretKey = MockSecretKey(issuer = issuer)
        val injection = mockInjection(
            pathNames = pathNames,
            local = MockLocalDataProvider(services = securityServices),
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
            security = {
                check(it == securityServices)
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
        )
        App.setInjection(injection)
        rule.setContent {
            App.Theme.Composition(themeState = mockThemeState()) {
                val keyState = remember {
                    mutableStateOf<SecretKey?>(secretKey)
                }
                RouterScreen.OnChecked(keyState = keyState)
            }
        }
        val isTraversalGroup = SemanticsMatcher.expectValue(SemanticsProperties.IsTraversalGroup, true)
        val isEnterScreen = hasContentDescription("EnterScreen")
        val isUnlockedScreen = hasContentDescription("UnlockedScreen")
        rule.waitOne(isTraversalGroup and isUnlockedScreen)
        rule.onNode(isTraversalGroup and isUnlockedScreen).assertIsDisplayed()
        check(dataBase.secrets.isEmpty())
        val isEmpty = hasContentDescription("UnlockedScreen:empty")
        rule.onNode(isEmpty).assertIsDisplayed()
        rule.onNode(isTraversalGroup and isEnterScreen).assertDoesNotExist()
    }

    @Test(timeout = 2_000)
    fun unlockedScreenTest() {
        val issuer = "RouterScreenTest:unlockedScreenTest"
        val provider = TestProvider(
            name = "AndroidOpenSSL",
            services = listOf(
                "MessageDigest" to "SHA-512",
                "SecureRandom" to "SHA1PRNG",
            ),
        )
        Security.insertProviderAt(provider, 0)
        val pathNames = mockPathNames(issuer = issuer)
        val dataBase = mockDataBase(
            secrets = (1..4).associate { number ->
                mockUUID() to ("title:$number" to "secret:$number")
            },
        )
        val dataBaseDecrypted = mockBytes(issuer)
        val dataBaseEncrypted = mockBytes(issuer)
        val symmetric = mockKeyMeta(issuer = issuer)
        val symmetricDecrypted = mockBytes(issuer)
        val securityServices = mockSecurityServices(issuer = issuer)
        val secretKey = MockSecretKey(issuer = issuer)
        val injection = mockInjection(
            pathNames = pathNames,
            local = MockLocalDataProvider(services = securityServices),
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
            security = {
                check(it == securityServices)
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
        )
        App.setInjection(injection)
        rule.setContent {
            App.Theme.Composition(themeState = mockThemeState()) {
                val keyState = remember {
                    mutableStateOf<SecretKey?>(secretKey)
                }
                RouterScreen.OnChecked(keyState = keyState)
            }
        }
        val isTraversalGroup = SemanticsMatcher.expectValue(SemanticsProperties.IsTraversalGroup, true)
        val isEnterScreen = hasContentDescription("EnterScreen")
        val isUnlockedScreen = hasContentDescription("UnlockedScreen")
        rule.waitOne(isTraversalGroup and isUnlockedScreen)
        rule.onNode(isTraversalGroup and isUnlockedScreen).assertIsDisplayed()
        check(dataBase.secrets.isNotEmpty())
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
        rule.onNode(isTraversalGroup and isEnterScreen).assertDoesNotExist()
    }
}
