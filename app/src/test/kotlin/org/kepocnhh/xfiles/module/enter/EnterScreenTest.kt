package org.kepocnhh.xfiles.module.enter

import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.kepocnhh.xfiles.App
import org.kepocnhh.xfiles.BigIntegerUtil
import org.kepocnhh.xfiles.TestActivity
import org.kepocnhh.xfiles.clearStores
import org.kepocnhh.xfiles.entity.MockAlgorithmParameters
import org.kepocnhh.xfiles.entity.MockDSAParameterSpec
import org.kepocnhh.xfiles.entity.MockDSAPrivateKey
import org.kepocnhh.xfiles.entity.MockPBEKeySpec
import org.kepocnhh.xfiles.entity.MockPrivateKey
import org.kepocnhh.xfiles.entity.MockPublicKey
import org.kepocnhh.xfiles.entity.MockSecretKey
import org.kepocnhh.xfiles.entity.SecuritySettings
import org.kepocnhh.xfiles.entity.mockAsymmetricKey
import org.kepocnhh.xfiles.entity.mockDataBase
import org.kepocnhh.xfiles.entity.mockDevice
import org.kepocnhh.xfiles.entity.mockKeyMeta
import org.kepocnhh.xfiles.entity.mockKeyPair
import org.kepocnhh.xfiles.entity.mockSecurityServices
import org.kepocnhh.xfiles.entity.mockSecuritySettings
import org.kepocnhh.xfiles.entity.mockUUID
import org.kepocnhh.xfiles.mockBytes
import org.kepocnhh.xfiles.module.app.mockEncrypted
import org.kepocnhh.xfiles.module.app.mockInjection
import org.kepocnhh.xfiles.module.app.mockThemeState
import org.kepocnhh.xfiles.provider.MockDeviceProvider
import org.kepocnhh.xfiles.provider.MockEncryptedFileProvider
import org.kepocnhh.xfiles.provider.MockSerializer
import org.kepocnhh.xfiles.provider.MockTimeProvider
import org.kepocnhh.xfiles.provider.data.MockEncryptedLocalDataProvider
import org.kepocnhh.xfiles.provider.data.MockLocalDataProvider
import org.kepocnhh.xfiles.provider.mockPathNames
import org.kepocnhh.xfiles.provider.security.MockAlgorithmParameterGeneratorProvider
import org.kepocnhh.xfiles.provider.security.MockBase64Provider
import org.kepocnhh.xfiles.provider.security.MockCipherProvider
import org.kepocnhh.xfiles.provider.security.MockKeyFactoryProvider
import org.kepocnhh.xfiles.provider.security.MockKeyPairGeneratorProvider
import org.kepocnhh.xfiles.provider.security.MockMessageDigestProvider
import org.kepocnhh.xfiles.provider.security.MockSecretKeyFactoryProvider
import org.kepocnhh.xfiles.provider.security.MockSecureRandom
import org.kepocnhh.xfiles.provider.security.MockSecurityProvider
import org.kepocnhh.xfiles.provider.security.MockSignatureProvider
import org.kepocnhh.xfiles.provider.security.MockUUIDGenerator
import org.kepocnhh.xfiles.setInjection
import org.kepocnhh.xfiles.waitUntilPresent
import org.robolectric.RobolectricTestRunner
import java.util.concurrent.atomic.AtomicReference
import kotlin.math.pow
import kotlin.time.Duration.Companion.seconds

@Suppress("NonBooleanPropertyPrefixedWithIs")
@RunWith(RobolectricTestRunner::class)
internal class EnterScreenTest {
    @get:Rule
    val rule = createAndroidComposeRule<TestActivity>()

    @Before
    fun before() {
        App.clearStores()
    }

    @Suppress("LongMethod")
    @Test(timeout = 2_000)
    fun createNewTest() {
        val issuer = "EnterScreenTest:createNewTest"
        val broadcastRef = AtomicReference<EnterScreen.Broadcast?>(null)
        val hasBiometric = false
        val device = mockDevice(issuer = issuer)
        val appId = mockUUID()
        val deviceId = mockUUID()
        val databaseId = mockUUID()
        val pin = "3490"
        val pinBytes = EnterViewModel.getBytes(
            pin = pin,
            deviceId = deviceId,
            appId = appId,
            databaseId = databaseId,
        )
        val pinBytesDigest = "$issuer:pin:digest".toByteArray()
        val pinBytesDigestEncoded = "$issuer:pin:digest:encoded"
        val symmetric = mockKeyMeta(
            saltSize = 32,
            ivDBSize = 16,
            ivPrivateSize = 16,
        )
        val symmetricDecrypted = "$issuer:symmetric:decrypted".toByteArray()
        val algParams = MockAlgorithmParameters(issuer = issuer)
        val aesKeyLength = 256
        val algParamsSpec = MockDSAParameterSpec(
            p = BigIntegerUtil.fromBits(2048),
            q = BigIntegerUtil.fromBits(aesKeyLength),
        )
        algParams.init(algParamsSpec)
        val publicKey = MockPublicKey(issuer = issuer)
        val privateKey = MockDSAPrivateKey(
            issuer = issuer,
            params = algParamsSpec,
        )
        val privateKeyEncrypted = "$issuer:private:key:encrypted".toByteArray()
        val updated = 123.seconds
        val dataBase = mockDataBase(
            id = databaseId,
            updated = updated,
        )
        check(dataBase.secrets.isEmpty())
        val dataBaseDecrypted = "$issuer:data:base:decrypted".toByteArray()
        val dataBaseEncrypted = "$issuer:data:base:encrypted".toByteArray()
        val dataBaseSignature = "$issuer:data:base:signature".toByteArray()
        val pbeIterations = 2.0.pow(16).toInt()
        val pbeKeySpec = MockPBEKeySpec(
            password = pinBytesDigestEncoded,
            salt = symmetric.salt,
            iterationCount = pbeIterations,
            keyLength = aesKeyLength,
        )
        val secretKey = MockSecretKey(issuer = issuer)
        val asymmetric = mockAsymmetricKey(
            publicKeyDecrypted = publicKey.encoded,
            privateKeyEncrypted = privateKeyEncrypted,
        )
        val asymmetricDecrypted = "$issuer:asymmetric:decrypted".toByteArray()
        val pathNames = mockPathNames(issuer = issuer)
        val files = setOf(
            pathNames.symmetric,
            pathNames.asymmetric,
            pathNames.dataBase,
            pathNames.dataBaseSignature,
//              pathNames.biometric, // no cipher
        )
        val securityServices = mockSecurityServices(issuer = issuer)
        val injection = mockInjection(
            pathNames = pathNames,
            local = MockLocalDataProvider(
                services = securityServices,
                securitySettings = mockSecuritySettings(
                    aesKeyLength = SecuritySettings.AESKeyLength.BITS_256,
                    pbeIterations = SecuritySettings.PBEIterations.NUMBER_2_16,
                    dsaKeyLength = SecuritySettings.DSAKeyLength.BITS_1024_2,
                    hasBiometric = hasBiometric,
                ),
                device = device,
            ),
            encrypted = mockEncrypted(
                local = MockEncryptedLocalDataProvider(
                    appId = appId,
                ),
            ),
            devices = MockDeviceProvider(
                uuids = mapOf(device to deviceId),
            ),
            security = { ss ->
                check(securityServices == ss)
                MockSecurityProvider(
                    sha512 = MockMessageDigestProvider(
                        listOf(pinBytes to pinBytesDigest),
                    ),
                    uuids = MockUUIDGenerator(uuid = databaseId),
                    base64 = MockBase64Provider(
                        values = mapOf(pinBytesDigestEncoded to pinBytesDigest),
                    ),
                    random = MockSecureRandom(
                        values = mapOf(
                            32 to symmetric.salt,
                            16 to symmetric.ivDB,
                        ),
                    ),
                    algorithmParamsGenerator = MockAlgorithmParameterGeneratorProvider(
                        params = algParams,
                    ),
                    keyPairGenerator = MockKeyPairGeneratorProvider(
                        pairs = mapOf(
                            algParamsSpec to mockKeyPair(
                                publicKey = publicKey,
                                privateKey = privateKey,
                            ),
                        ),
                    ),
                    secretKeyFactory = MockSecretKeyFactoryProvider(
                        values = mapOf(
                            pbeKeySpec to secretKey,
                        ),
                    ),
                    cipher = MockCipherProvider(
                        values = listOf(
                            MockCipherProvider.DataSet(
                                encrypted = dataBaseEncrypted,
                                decrypted = dataBaseDecrypted,
                                secretKey = secretKey,
                            ),
                            MockCipherProvider.DataSet(
                                encrypted = privateKeyEncrypted,
                                decrypted = privateKey.encoded,
                                secretKey = secretKey,
                            ),
                        ),
                    ),
                    signature = MockSignatureProvider(
                        dataSets = listOf(
                            MockSignatureProvider.DataSet(
                                decrypted = dataBaseDecrypted,
                                sig = dataBaseSignature,
                                privateKey = privateKey,
                                publicKey = publicKey,
                            ),
                        ),
                    ),
                )
            },
            serializer = MockSerializer(
                values = mapOf(
                    symmetric to symmetricDecrypted,
                    asymmetric to asymmetricDecrypted,
                    dataBase to dataBaseDecrypted,
                ),
            ),
            time = MockTimeProvider(now = updated),
        )
        App.setInjection(injection)
        rule.setContent {
            App.Theme.Composition(themeState = mockThemeState()) {
                EnterScreen(broadcast = broadcastRef::set)
            }
        }
        check(injection.encrypted.local.databaseId == null)
        check(!hasBiometric)
        files.forEach {
            assertFalse("File $it exists!", injection.encrypted.files.exists(it))
        }
        val isButton = SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button)
        check(pin.length == 4)
        pin.forEach { char ->
            check(char.isDigit())
            val isPinButton = hasContentDescription("pin:pad:button:$char")
            rule.onNode(isButton and isPinButton).performClick()
        }
        val broadcast = rule.waitUntilPresent(broadcastRef)
        check(broadcast is EnterScreen.Broadcast.Unlock)
        assertTrue("Keys are not equal!", secretKey.encoded.contentEquals(broadcast.key.encoded))
        assertEquals(injection.encrypted.local.databaseId, databaseId)
        files.forEach {
            assertTrue("File $it does not exist!", injection.encrypted.files.exists(it))
        }
    }

    @Suppress("LongMethod")
    @Test(timeout = 2_000)
    fun unlockTest() {
        val issuer = "EnterScreenTest:unlockTest"
        val broadcastRef = AtomicReference<EnterScreen.Broadcast?>(null)
        val hasBiometric = false
        val pathNames = mockPathNames(issuer = issuer)
        val files = setOf(
            pathNames.symmetric,
            pathNames.asymmetric,
            pathNames.dataBase,
            pathNames.dataBaseSignature,
//              pathNames.biometric, // no cipher
        )
        val device = mockDevice(issuer = issuer)
        val appId = mockUUID()
        val deviceId = mockUUID()
        val databaseId = mockUUID()
        val pin = "0943"
        val pinBytes = EnterViewModel.getBytes(
            pin = pin,
            deviceId = deviceId,
            appId = appId,
            databaseId = databaseId,
        )
        val pinBytesDigestEncoded = "$issuer:pin:digest:encoded"
        val pinBytesDigest = pinBytesDigestEncoded.toByteArray()
        val symmetric = mockKeyMeta(
            saltSize = 32,
            ivDBSize = 16,
            ivPrivateSize = 16,
        )
        val symmetricDecrypted = mockBytes(issuer)
        val publicKey = MockPublicKey(issuer = issuer)
        val privateKey = MockPrivateKey(issuer = issuer)
        val privateKeyEncrypted = mockBytes(issuer)
        val asymmetric = mockAsymmetricKey(
            publicKeyDecrypted = publicKey.encoded,
            privateKeyEncrypted = privateKeyEncrypted,
        )
        val asymmetricDecrypted = mockBytes(issuer)
        val aesKeyLength = 256
        val pbeIterations = 2.0.pow(16).toInt()
        val pbeKeySpec = MockPBEKeySpec(
            password = pinBytesDigestEncoded,
            salt = symmetric.salt,
            iterationCount = pbeIterations,
            keyLength = aesKeyLength,
        )
        val secrets = mapOf(
            mockUUID() to ("foo:1" to "bar:1"),
            mockUUID() to ("foo:2" to "bar:2"),
        )
        val dataBase = mockDataBase(
            id = databaseId,
            updated = 128.seconds,
            secrets = secrets,
        )
        val dataBaseEncrypted = mockBytes(issuer)
        val dataBaseDecrypted = mockBytes(issuer)
        val dataBaseSignature = mockBytes(issuer)
        val secretKey = MockSecretKey(issuer = issuer)
        val securityServices = mockSecurityServices(issuer = issuer)
        val injection = mockInjection(
            pathNames = pathNames,
            local = MockLocalDataProvider(
                services = securityServices,
                securitySettings = mockSecuritySettings(
                    aesKeyLength = SecuritySettings.AESKeyLength.BITS_256,
                    pbeIterations = SecuritySettings.PBEIterations.NUMBER_2_16,
                    dsaKeyLength = SecuritySettings.DSAKeyLength.BITS_1024_2,
                    hasBiometric = hasBiometric,
                ),
                device = device,
            ),
            encrypted = mockEncrypted(
                local = MockEncryptedLocalDataProvider(
                    appId = appId,
                    databaseId = databaseId,
                ),
                files = MockEncryptedFileProvider(
                    exists = files,
                    inputs = mapOf(
                        pathNames.symmetric to symmetricDecrypted,
                        pathNames.dataBase to dataBaseEncrypted,
                        pathNames.asymmetric to asymmetricDecrypted,
                        pathNames.dataBaseSignature to dataBaseSignature,
                    ),
                ),
            ),
            devices = MockDeviceProvider(
                uuids = mapOf(device to deviceId),
            ),
            security = { ss ->
                check(securityServices == ss)
                MockSecurityProvider(
                    sha512 = MockMessageDigestProvider(
                        listOf(pinBytes to pinBytesDigest),
                    ),
                    base64 = MockBase64Provider(
                        values = mapOf(pinBytesDigestEncoded to pinBytesDigest),
                    ),
                    secretKeyFactory = MockSecretKeyFactoryProvider(
                        values = mapOf(
                            pbeKeySpec to secretKey,
                        ),
                    ),
                    cipher = MockCipherProvider(
                        values = listOf(
                            MockCipherProvider.DataSet(
                                encrypted = dataBaseEncrypted,
                                decrypted = dataBaseDecrypted,
                                secretKey = secretKey,
                            ),
                        ),
                    ),
                    keyFactory = MockKeyFactoryProvider(
                        publicKey = publicKey,
                    ),
                    signature = MockSignatureProvider(
                        dataSets = listOf(
                            MockSignatureProvider.DataSet(
                                decrypted = dataBaseDecrypted,
                                sig = dataBaseSignature,
                                privateKey = privateKey,
                                publicKey = publicKey,
                            ),
                        ),
                    ),
                )
            },
            serializer = MockSerializer(
                values = mapOf(
                    symmetric to symmetricDecrypted,
                    asymmetric to asymmetricDecrypted,
                    dataBase to dataBaseDecrypted,
                ),
            ),
        )
        App.setInjection(injection)
        rule.setContent {
            App.Theme.Composition(themeState = mockThemeState()) {
                EnterScreen(broadcast = broadcastRef::set)
            }
        }
        check(injection.encrypted.local.databaseId == databaseId)
        check(injection.security(securityServices).uuids().generate() != databaseId)
        check(!hasBiometric)
        files.forEach {
            assertTrue("File $it does not exist!", injection.encrypted.files.exists(it))
        }
        val isButton = SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button)
        check(pin.length == 4)
        pin.forEach { char ->
            check(char.isDigit())
            val isPinButton = hasContentDescription("pin:pad:button:$char")
            rule.onNode(isButton and isPinButton).performClick()
        }
        val broadcast = rule.waitUntilPresent(broadcastRef)
        check(broadcast is EnterScreen.Broadcast.Unlock)
        assertTrue("Keys are not equal!", secretKey.encoded.contentEquals(broadcast.key.encoded))
    }
}
