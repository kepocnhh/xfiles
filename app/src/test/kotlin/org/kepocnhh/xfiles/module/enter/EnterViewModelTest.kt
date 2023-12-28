package org.kepocnhh.xfiles.module.enter

import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.kepocnhh.xfiles.BigIntegerUtil
import org.kepocnhh.xfiles.collectFirst
import org.kepocnhh.xfiles.entity.BiometricMeta
import org.kepocnhh.xfiles.entity.MockAlgorithmParameters
import org.kepocnhh.xfiles.entity.MockDSAParameterSpec
import org.kepocnhh.xfiles.entity.MockDSAPrivateKey
import org.kepocnhh.xfiles.entity.MockPBEKeySpec
import org.kepocnhh.xfiles.entity.MockPrivateKey
import org.kepocnhh.xfiles.entity.MockPublicKey
import org.kepocnhh.xfiles.entity.MockSecretKey
import org.kepocnhh.xfiles.entity.SecuritySettings
import org.kepocnhh.xfiles.entity.mockAsymmetricKey
import org.kepocnhh.xfiles.entity.mockBiometricMeta
import org.kepocnhh.xfiles.entity.mockDataBase
import org.kepocnhh.xfiles.entity.mockDevice
import org.kepocnhh.xfiles.entity.mockEncryptData
import org.kepocnhh.xfiles.entity.mockKeyMeta
import org.kepocnhh.xfiles.entity.mockKeyPair
import org.kepocnhh.xfiles.entity.mockSecurityServices
import org.kepocnhh.xfiles.entity.mockSecuritySettings
import org.kepocnhh.xfiles.entity.mockUUID
import org.kepocnhh.xfiles.module.app.mockEncrypted
import org.kepocnhh.xfiles.module.app.mockInjection
import org.kepocnhh.xfiles.provider.Encrypt
import org.kepocnhh.xfiles.provider.MockDeviceProvider
import org.kepocnhh.xfiles.provider.MockEncrypt
import org.kepocnhh.xfiles.provider.MockEncryptedFileProvider
import org.kepocnhh.xfiles.provider.MockSerializer
import org.kepocnhh.xfiles.provider.MockTimeProvider
import org.kepocnhh.xfiles.provider.data.MockEncryptedLocalDataProvider
import org.kepocnhh.xfiles.provider.data.MockLocalDataProvider
import org.kepocnhh.xfiles.provider.mockPathNames
import org.kepocnhh.xfiles.provider.readBytes
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
import org.kepocnhh.xfiles.waitUntil
import kotlin.math.pow
import kotlin.time.Duration.Companion.seconds

internal class EnterViewModelTest {
    companion object {
        private fun mockState(
            loading: Boolean = false,
            exists: Boolean = false,
            hasBiometric: Boolean = false,
        ): EnterViewModel.State {
            return EnterViewModel.State(
                loading = loading,
                exists = exists,
                hasBiometric = hasBiometric,
            )
        }
    }

    private suspend fun requestStateTest(expected: EnterViewModel.State) {
        val issuer = "EnterViewModelTest:requestStateTest:${expected.hashCode()}"
        val pathNames = mockPathNames(
            dataBase = "$issuer:dataBase",
        )
        val injection = mockInjection(
            pathNames = pathNames,
            local = MockLocalDataProvider(
                securitySettings = mockSecuritySettings(
                    hasBiometric = expected.hasBiometric,
                ),
            ),
            encrypted = mockEncrypted(
                files = MockEncryptedFileProvider(
                    exists = if (expected.exists) setOf(pathNames.dataBase) else emptySet(),
                ),
            ),
        )
        val viewModel = EnterViewModel(injection)
        viewModel
            .state
            .take(2)
            .collectIndexed { index, value ->
                when (index) {
                    0 -> {
                        assertNull(value)
                        viewModel.requestState()
                    }
                    1 -> {
                        assertNotNull(value)
                        checkNotNull(value)
                        assertEquals(expected, value)
                    }
                    else -> error("Unexpected index: $index!")
                }
            }
    }

    @Test
    fun requestStateTest() {
        runTest(timeout = 2.seconds) {
            setOf(
                mockState(),
                mockState(exists = true),
                mockState(hasBiometric = true),
                mockState(exists = true, hasBiometric = true),
            ).forEach { state ->
                requestStateTest(expected = state)
            }
        }
    }

    @Test
    fun requestBiometricTest() {
        runTest(timeout = 2.seconds) {
            val issuer = "EnterViewModelTest:requestBiometricTest"
            val expected = "$issuer:biometric:iv".toByteArray()
            val biometricInput = "$issuer:biometric:input".toByteArray()
            val pathNames = mockPathNames(
                biometric = "$issuer:biometric",
            )
            val injection = mockInjection(
                pathNames = pathNames,
                encrypted = mockEncrypted(
                    files = MockEncryptedFileProvider(
                        inputs = mapOf(pathNames.biometric to biometricInput),
                    ),
                ),
                serializer = MockSerializer(
                    values = mapOf(mockBiometricMeta(iv = expected) to biometricInput),
                ),
            )
            val viewModel = EnterViewModel(injection)
            viewModel
                .state
                .take(2)
                .collectIndexed { index, value ->
                    when (index) {
                        0 -> {
                            assertNull(value)
                            viewModel.requestState()
                        }
                        1 -> {
                            assertNotNull(value)
                            checkNotNull(value)
                        }
                        else -> error("Unexpected index: $index!")
                    }
                }
            waitUntil(
                scope = this,
                block = {
                    viewModel
                        .broadcast
                        .collectFirst {
                            check(it is EnterViewModel.Broadcast.OnBiometric)
                            assertTrue("Initial vectors are not equal!", expected.contentEquals(it.iv))
                        }
                },
                action = viewModel::requestBiometric,
            )
        }
    }

    @Test
    fun deleteFileTest() {
        runTest(timeout = 2.seconds) {
            val issuer = "EnterViewModelTest:deleteFileTest"
            val hasBiometric = true
            val pathNames = mockPathNames(
                dataBase = "$issuer:dataBase",
            )
            val expected = setOf(
                pathNames.symmetric,
                pathNames.asymmetric,
                pathNames.dataBase,
                pathNames.dataBaseSignature,
                pathNames.biometric,
            )
            val injection = mockInjection(
                local = MockLocalDataProvider(
                    securitySettings = mockSecuritySettings(hasBiometric = hasBiometric),
                ),
                pathNames = pathNames,
                encrypted = mockEncrypted(
                    files = MockEncryptedFileProvider(exists = expected),
                ),
            )
            val viewModel = EnterViewModel(injection)
            viewModel
                .state
                .take(3)
                .collectIndexed { index, value ->
                    when (index) {
                        0 -> {
                            assertNull(value)
                            viewModel.requestState()
                        }
                        1 -> {
                            assertNotNull(value)
                            checkNotNull(value)
                            assertTrue(value.exists)
                            assertEquals(hasBiometric, value.hasBiometric)
                            expected.forEach {
                                assertTrue("File $it does not exist!", injection.encrypted.files.exists(it))
                            }
                            viewModel.deleteFile()
                        }
                        2 -> {
                            assertNotNull(value)
                            checkNotNull(value)
                            expected.forEach {
                                assertFalse("File $it exists!", injection.encrypted.files.exists(it))
                            }
                            assertFalse(value.exists)
                            assertEquals(hasBiometric, value.hasBiometric)
                        }
                        else -> error("Unexpected index: $index!")
                    }
                }
        }
    }

    @Test
    fun createNewFileTest() {
        runTest(timeout = 2.seconds) {
            val issuer = "EnterViewModelTest:createNewFileTest"
            val pin = "$issuer:pin"
            val secretKey = MockSecretKey(encoded = "$issuer:key".toByteArray())
            val pathNames = mockPathNames(issuer = issuer)
            val files = setOf(
                pathNames.symmetric,
                pathNames.asymmetric,
                pathNames.dataBase,
                pathNames.dataBaseSignature,
//                pathNames.biometric, // no cipher
            )
            val device = mockDevice(issuer = issuer)
            val deviceUUID = mockUUID(number = 1)
            val appId = mockUUID(number = 2)
            val databaseId = mockUUID(number = 3)
            val pinBytes = listOf(
                pin,
                deviceUUID.toString(),
                appId.toString(),
                databaseId.toString(),
            ).joinToString(separator = "-").toByteArray()
            val pinBytesDigest = "$issuer:pin:digest".toByteArray()
            val pinBytesDigestEncoded = "$issuer:pin:digest:encoded"
            val symmetric = mockKeyMeta(
                saltSize = 32,
                ivDBSize = 16,
                ivPrivateSize = 16,
            )
            val pbeIterations = 2.0.pow(16).toInt()
            val aesKeyLength = 256
            val pbeKeySpec = MockPBEKeySpec(
                password = pinBytesDigestEncoded,
                salt = symmetric.salt,
                iterationCount = pbeIterations,
                keyLength = aesKeyLength,
            )
            val symmetricDecrypted = "$issuer:symmetric:decrypted".toByteArray()
            val algParams = MockAlgorithmParameters()
            val algParamsSpec = MockDSAParameterSpec(
                p = BigIntegerUtil.fromBits(2048),
                q = BigIntegerUtil.fromBits(aesKeyLength),
            )
            algParams.init(algParamsSpec)
            val publicKey = MockPublicKey(encoded = "$issuer:public:key".toByteArray())
            val privateKey = MockDSAPrivateKey(
                encoded = "$issuer:private:key".toByteArray(),
                params = algParamsSpec,
            )
            val privateKeyEncrypted = "$issuer:private:key:encrypted".toByteArray()
            val updated = 123.seconds
            val dataBase = mockDataBase(
                id = databaseId,
                updated = updated,
                secrets = emptyMap(),
            )
            val dataBaseDecrypted = "$issuer:data:base:decrypted".toByteArray()
            val dataBaseSignature = "$issuer:data:base:signature".toByteArray()
            val dataBaseEncrypted = "$issuer:data:base:encrypted".toByteArray()
            val asymmetric = mockAsymmetricKey(
                publicDecrypted = publicKey.encoded,
                privateEncrypted = privateKeyEncrypted,
            )
            val asymmetricDecrypted = "$issuer:asymmetric:decrypted".toByteArray()
            val hasBiometric = false
            val injection = mockInjection(
                local = MockLocalDataProvider(
                    services = mockSecurityServices(),
                    securitySettings = mockSecuritySettings(
                        aesKeyLength = SecuritySettings.AESKeyLength.BITS_256,
                        pbeIterations = SecuritySettings.PBEIterations.NUMBER_2_16,
                        dsaKeyLength = SecuritySettings.DSAKeyLength.BITS_1024_2,
                        hasBiometric = hasBiometric,
                    ),
                    device = device,
                ),
                pathNames = pathNames,
                encrypted = mockEncrypted(
                    local = MockEncryptedLocalDataProvider(
                        appId = appId,
                        databaseId = databaseId,
                    ),
                ),
                security = {
                    MockSecurityProvider(
                        md = MockMessageDigestProvider(
                            listOf(pinBytes to pinBytesDigest),
                        ),
                        uuids = MockUUIDGenerator(uuid = databaseId),
                        base64 = MockBase64Provider(
                            mapOf(pinBytesDigestEncoded to pinBytesDigest),
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
                                )
                            ),
                        ),
                        secretKeyFactory = MockSecretKeyFactoryProvider(
                            values = mapOf(
                                pbeKeySpec to secretKey,
                            ),
                        ),
                        cipher = MockCipherProvider(
                            values = listOf(
                                Triple(dataBaseEncrypted, dataBaseDecrypted, secretKey),
                                Triple(privateKeyEncrypted, privateKey.encoded, secretKey),
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
                devices = MockDeviceProvider(
                    uuids = mapOf(device to deviceUUID),
                ),
                serializer = MockSerializer(
                    values = mapOf(
                        symmetric to symmetricDecrypted,
                        asymmetric to asymmetricDecrypted,
                        dataBase to dataBaseDecrypted,
                    ),
                ),
                time = MockTimeProvider(now = updated),
            )
            val viewModel = EnterViewModel(injection)
            viewModel
                .state
                .take(2)
                .collectIndexed { index, value ->
                    when (index) {
                        0 -> {
                            assertNull(value)
                            viewModel.requestState()
                        }
                        1 -> {
                            assertNotNull(value)
                            checkNotNull(value)
                            assertFalse(value.exists)
                            assertEquals(hasBiometric, value.hasBiometric)
                            files.forEach {
                                assertFalse("File $it exists!", injection.encrypted.files.exists(it))
                            }
                        }
                        else -> error("Unexpected index: $index!")
                    }
                }
            waitUntil(
                scope = this,
                block = {
                    viewModel
                        .broadcast
                        .collectFirst {
                            check(it is EnterViewModel.Broadcast.OnUnlock)
                            assertTrue("Keys are not equal!", secretKey.encoded.contentEquals(it.key.encoded))
                        }
                },
                action = {
                    viewModel.createNewFile(pin = pin, encrypt = null)
                },
            )
            files.forEach {
                assertTrue("File $it does not exist!", injection.encrypted.files.exists(it))
            }
        }
    }

    @Test
    fun createNewFileCipherTest() {
        runTest(timeout = 2.seconds) {
            val issuer = "EnterViewModelTest:createNewFileCipherTest"
            val pathNames = mockPathNames(issuer = issuer)
            val files = setOf(
                pathNames.symmetric,
                pathNames.asymmetric,
                pathNames.dataBase,
                pathNames.dataBaseSignature,
                pathNames.biometric,
            )
            val hasBiometric = true
            val device = mockDevice(issuer = issuer)
            val deviceUUID = mockUUID(number = 43)
            val appId = mockUUID(number = 41)
            val databaseId = mockUUID(number = 42)
            val initDatabaseId = mockUUID(number = 2)
            check(initDatabaseId != databaseId)
            val pin = "$issuer:pin"
            val pinBytes = listOf(
                pin,
                deviceUUID.toString(),
                appId.toString(),
                databaseId.toString(),
            ).joinToString(separator = "-").toByteArray()
            val pinBytesDigest = "$issuer:pin:digest".toByteArray()
            val pinBytesDigestEncoded = "$issuer:pin:digest:encoded"
            val secretKey = MockSecretKey(encoded = "$issuer:secret:key".toByteArray())
            val biometricEncryptData = mockEncryptData(issuer = issuer)
            val biometricMeta = mockBiometricMeta(
                password = biometricEncryptData.encrypted,
                iv = biometricEncryptData.iv,
            )
            val biometricMetaDecrypted = "$issuer:symmetric:decrypted".toByteArray()
            val symmetric = mockKeyMeta(
                saltSize = 32,
                ivDBSize = 16,
                ivPrivateSize = 16,
            )
            val symmetricDecrypted = "$issuer:symmetric:decrypted".toByteArray()
            val aesKeyLength = 256
            val algParams = MockAlgorithmParameters(issuer = issuer)
            val algParamsSpec = MockDSAParameterSpec(
                p = BigIntegerUtil.fromBits(2048),
                q = BigIntegerUtil.fromBits(aesKeyLength),
            )
            algParams.init(algParamsSpec)
            val publicKey = MockPublicKey(issuer = issuer)
            val privateKey = MockDSAPrivateKey(issuer = issuer, params = algParamsSpec)
            val privateKeyEncrypted = "$issuer:private:key:encrypted".toByteArray()
            val updated = 123.seconds
            val dataBase = mockDataBase(
                id = databaseId,
                updated = updated,
                secrets = emptyMap(),
            )
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
            val asymmetric = mockAsymmetricKey(
                publicDecrypted = publicKey.encoded,
                privateEncrypted = privateKeyEncrypted,
            )
            val asymmetricDecrypted = "$issuer:asymmetric:decrypted".toByteArray()
            val injection = mockInjection(
                local = MockLocalDataProvider(
                    services = mockSecurityServices(issuer = issuer),
                    securitySettings = mockSecuritySettings(
                        aesKeyLength = SecuritySettings.AESKeyLength.BITS_256,
                        pbeIterations = SecuritySettings.PBEIterations.NUMBER_2_16,
                        dsaKeyLength = SecuritySettings.DSAKeyLength.BITS_1024_2,
                        hasBiometric = hasBiometric,
                    ),
                    device = device,
                ),
                pathNames = pathNames,
                encrypted = mockEncrypted(
                    local = MockEncryptedLocalDataProvider(
                        appId = appId,
                        databaseId = initDatabaseId,
                    ),
                ),
                security = {
                    MockSecurityProvider(
                        md = MockMessageDigestProvider(
                            listOf(pinBytes to pinBytesDigest),
                        ),
                        base64 = MockBase64Provider(
                            mapOf(pinBytesDigestEncoded to pinBytesDigest),
                        ),
                        uuids = MockUUIDGenerator(uuid = databaseId),
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
                                )
                            ),
                        ),
                        secretKeyFactory = MockSecretKeyFactoryProvider(
                            values = mapOf(
                                pbeKeySpec to secretKey,
                            ),
                        ),
                        cipher = MockCipherProvider(
                            values = listOf(
                                Triple(dataBaseEncrypted, dataBaseDecrypted, secretKey),
                                Triple(privateKeyEncrypted, privateKey.encoded, secretKey),
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
                devices = MockDeviceProvider(
                    uuids = mapOf(device to deviceUUID)
                ),
                serializer = MockSerializer(
                    values = mapOf(
                        biometricMeta to biometricMetaDecrypted,
                        symmetric to symmetricDecrypted,
                        dataBase to dataBaseDecrypted,
                        asymmetric to asymmetricDecrypted,
                    ),
                ),
                time = MockTimeProvider(now = updated),
            )
            val viewModel = EnterViewModel(injection)
            viewModel
                .state
                .take(2)
                .collectIndexed { index, value ->
                    when (index) {
                        0 -> {
                            assertNull(value)
                            viewModel.requestState()
                        }
                        1 -> {
                            assertNotNull(value)
                            checkNotNull(value)
                            assertFalse(value.exists)
                            assertEquals(hasBiometric, value.hasBiometric)
                            files.forEach {
                                assertFalse("File $it exists!", injection.encrypted.files.exists(it))
                            }
                            assertEquals(initDatabaseId, injection.encrypted.local.databaseId)
                        }
                        else -> error("Unexpected index: $index!")
                    }
                }
            val biometricDecrypted = pinBytesDigestEncoded.toByteArray()
            val biometricEncrypt = MockEncrypt(
                values = listOf(
                    biometricDecrypted to biometricEncryptData,
                ),
            )
            waitUntil(
                scope = this,
                block = {
                    viewModel
                        .broadcast
                        .collectFirst {
                            check(it is EnterViewModel.Broadcast.OnUnlock)
                            assertTrue("Keys are not equal!", secretKey.encoded.contentEquals(it.key.encoded))
                        }
                },
                action = {
                    viewModel.createNewFile(pin = pin, encrypt = biometricEncrypt)
                },
            )
            files.forEach {
                assertTrue("File $it does not exist!", injection.encrypted.files.exists(it))
            }
            assertEquals(databaseId, injection.encrypted.local.databaseId)
            assertTrue(biometricMetaDecrypted.contentEquals(injection.encrypted.files.readBytes(pathNames.biometric)))
        }
    }

    @Test
    fun unlockFileTest() {
        runTest(timeout = 2.seconds) {
            val issuer = "EnterViewModelTest:unlockFile"
            val hasBiometric = false
            val pathNames = mockPathNames(issuer = issuer)
            val files = setOf(
                pathNames.symmetric,
                pathNames.asymmetric,
                pathNames.dataBase,
                pathNames.dataBaseSignature,
//                pathNames.biometric, // hasBiometric == false
            )
            val device = mockDevice(issuer = issuer)
            val deviceUUID = mockUUID()
            val appId = mockUUID()
            val databaseId = mockUUID()
            val pin = "$issuer:pin"
            val pinBytes = listOf(
                pin,
                deviceUUID.toString(),
                appId.toString(),
                databaseId.toString(),
            ).joinToString(separator = "-").toByteArray()
            val pinBytesDigest = "$issuer:pin:digest".toByteArray()
            val pinBytesDigestEncoded = "$issuer:pin:digest:encoded"
            val secretKey = MockSecretKey(issuer = issuer)
            val symmetric = mockKeyMeta(issuer = issuer)
            val symmetricDecrypted = "$issuer:symmetric:decrypted".toByteArray()
            val asymmetricDecrypted = "$issuer:asymmetric:decrypted".toByteArray()
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
            val dataBaseEncrypted = "$issuer:data:base:encrypted".toByteArray()
            val dataBaseDecrypted = "$issuer:data:base:decrypted".toByteArray()
            val dataBaseSignature = "$issuer:data:base:signature".toByteArray()
            val publicKey = MockPublicKey(issuer = issuer)
            val privateKey = MockPrivateKey(issuer = issuer)
            val privateKeyEncrypted = "$issuer:private:key:encrypted".toByteArray()
            val asymmetric = mockAsymmetricKey(
                publicDecrypted = publicKey.encoded,
                privateEncrypted = privateKeyEncrypted,
            )
            val injection = mockInjection(
                pathNames = pathNames,
                local = MockLocalDataProvider(
                    services = mockSecurityServices(issuer = issuer),
                    securitySettings = mockSecuritySettings(
                        aesKeyLength = SecuritySettings.AESKeyLength.BITS_256,
                        pbeIterations = SecuritySettings.PBEIterations.NUMBER_2_16,
                        dsaKeyLength = SecuritySettings.DSAKeyLength.BITS_1024_2,
                        hasBiometric = hasBiometric,
                    ),
                    device = device,
                ),
                encrypted = mockEncrypted(
                    files = MockEncryptedFileProvider(
                        exists = files,
                        inputs = mapOf(
                            pathNames.symmetric to symmetricDecrypted,
                            pathNames.dataBase to dataBaseEncrypted,
                            pathNames.asymmetric to asymmetricDecrypted,
                            pathNames.dataBaseSignature to dataBaseSignature,
                        ),
                    ),
                    local = MockEncryptedLocalDataProvider(
                        appId = appId,
                        databaseId = databaseId,
                    ),
                ),
                devices = MockDeviceProvider(
                    uuids = mapOf(device to deviceUUID),
                ),
                security = {
                    MockSecurityProvider(
                        md = MockMessageDigestProvider(
                            listOf(pinBytes to pinBytesDigest),
                        ),
                        base64 = MockBase64Provider(
                            mapOf(pinBytesDigestEncoded to pinBytesDigest),
                        ),
                        keyFactory = MockKeyFactoryProvider(
                            publicKey = publicKey,
                        ),
                        secretKeyFactory = MockSecretKeyFactoryProvider(
                            values = mapOf(
                                pbeKeySpec to secretKey,
                            ),
                        ),
                        cipher = MockCipherProvider(
                            values = listOf(
                                Triple(dataBaseEncrypted, dataBaseDecrypted, secretKey),
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
            )
            val viewModel = EnterViewModel(injection)
            viewModel
                .state
                .take(2)
                .collectIndexed { index, value ->
                    when (index) {
                        0 -> {
                            assertNull(value)
                            viewModel.requestState()
                        }
                        1 -> {
                            assertNotNull(value)
                            checkNotNull(value)
                            assertTrue("File \"${pathNames.dataBase}\" does not exist!", value.exists)
                            assertEquals("It has biometric == $hasBiometric!", hasBiometric, value.hasBiometric)
                            files.forEach {
                                assertTrue("File \"$it\" does not exist!", injection.encrypted.files.exists(it))
                            }
                        }
                        else -> error("Unexpected index: $index!")
                    }
                }
            waitUntil(
                scope = this,
                block = {
                    viewModel
                        .broadcast
                        .collectFirst {
                            check(it is EnterViewModel.Broadcast.OnUnlock) { "Broadcast is $it!" }
                            assertTrue("Keys are not equal!", secretKey.encoded.contentEquals(it.key.encoded))
                        }
                },
                action = {
                    viewModel.unlockFile(pin = pin)
                },
            )
        }
    }

    @Test
    fun unlockFileCipherErrorTest() {
        runTest(timeout = 2.seconds) {
            val issuer = "EnterViewModelTest:unlockFileCipherErrorTest"
            // todo
        }
    }

    @Test
    fun unlockFileCipherTest() {
        runTest(timeout = 2.seconds) {
            val issuer = "EnterViewModelTest:unlockFileCipherTest"
            // todo viewModel.unlockFile(cipher = ...)
        }
    }
}
