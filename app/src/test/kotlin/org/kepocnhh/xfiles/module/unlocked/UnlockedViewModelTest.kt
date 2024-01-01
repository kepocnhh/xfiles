package org.kepocnhh.xfiles.module.unlocked

import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.kepocnhh.xfiles.collectFirst
import org.kepocnhh.xfiles.entity.MockPrivateKey
import org.kepocnhh.xfiles.entity.MockPublicKey
import org.kepocnhh.xfiles.entity.MockSecretKey
import org.kepocnhh.xfiles.entity.mockAsymmetricKey
import org.kepocnhh.xfiles.entity.mockDataBase
import org.kepocnhh.xfiles.entity.mockKeyMeta
import org.kepocnhh.xfiles.entity.mockSecurityServices
import org.kepocnhh.xfiles.entity.mockUUID
import org.kepocnhh.xfiles.module.app.mockEncrypted
import org.kepocnhh.xfiles.module.app.mockInjection
import org.kepocnhh.xfiles.provider.MockEncryptedFileProvider
import org.kepocnhh.xfiles.provider.MockSerializer
import org.kepocnhh.xfiles.provider.MockTimeProvider
import org.kepocnhh.xfiles.provider.data.MockLocalDataProvider
import org.kepocnhh.xfiles.provider.mockPathNames
import org.kepocnhh.xfiles.provider.security.MockCipherProvider
import org.kepocnhh.xfiles.provider.security.MockKeyFactoryProvider
import org.kepocnhh.xfiles.provider.security.MockSecurityProvider
import org.kepocnhh.xfiles.provider.security.MockSignatureProvider
import org.kepocnhh.xfiles.provider.security.MockUUIDGenerator
import java.util.UUID
import java.util.concurrent.atomic.AtomicReference
import kotlin.time.Duration.Companion.seconds

internal class UnlockedViewModelTest {
    @Test
    fun requestValuesTest() {
        runTest(timeout = 2.seconds) {
            val issuer = "UnlockedViewModelTest:requestValuesTest"
            val dataBase = mockDataBase(
                secrets = mapOf(
                    mockUUID() to ("foo:title" to "foo:secret"),
                    mockUUID() to ("bar:title" to "bar:secret"),
                ),
            )
            check(dataBase.secrets.isNotEmpty())
            val dataBaseDecrypted = "dataBase:decrypted".toByteArray()
            val dataBaseEncrypted = "dataBase:encrypted".toByteArray()
            val symmetric = mockKeyMeta()
            val secretKey = MockSecretKey(issuer = issuer)
            val pathNames = mockPathNames()
            val injection = mockInjection(
                pathNames = pathNames,
                local = MockLocalDataProvider(services = mockSecurityServices()),
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
                            pathNames.dataBase to dataBaseEncrypted,
                            pathNames.symmetric to "symmetric".toByteArray(),
                        ),
                    ),
                ),
                serializer = MockSerializer(
                    values = mapOf(
                        symmetric to "symmetric".toByteArray(),
                        dataBase to dataBaseDecrypted,
                    ),
                ),
            )
            val viewModel = UnlockedViewModel(injection)
            viewModel
                .encrypteds
                .take(2)
                .collectIndexed { index, value ->
                    when (index) {
                        0 -> {
                            assertNull(value)
                            viewModel.requestValues(key = secretKey)
                        }
                        1 -> {
                            assertNotNull(value)
                            checkNotNull(value)
                            val titles = dataBase.secrets.mapValues { (_, pair) ->
                                val (title, _) = pair
                                title
                            }
                            assertEquals(titles, value)
                        }
                        else -> error("Unexpected index: $index!")
                    }
                }
        }
    }

    @Test
    fun requestToCopyTest() {
        runTest(timeout = 2.seconds) {
            val issuer = "UnlockedViewModelTest:requestToCopyTest"
            val id = mockUUID()
            val secret = "requestToCopyTest:secret"
            val dataBase = mockDataBase(
                secrets = mapOf(
                    id to ("foo:title" to secret),
                ),
            )
            val dataBaseDecrypted = "dataBase:decrypted".toByteArray()
            val dataBaseEncrypted = "dataBase:encrypted".toByteArray()
            val symmetric = mockKeyMeta()
            val symmetricDecrypted = "symmetric:decrypted".toByteArray()
            val secretKey = MockSecretKey(issuer = issuer)
            val pathNames = mockPathNames()
            val injection = mockInjection(
                pathNames = pathNames,
                local = MockLocalDataProvider(services = mockSecurityServices()),
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
                            pathNames.dataBase to dataBaseEncrypted,
                            pathNames.symmetric to symmetricDecrypted,
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
            val viewModel = UnlockedViewModel(injection)
            val job = launch(UnconfinedTestDispatcher()) {
                viewModel
                    .broadcast
                    .collectFirst {
                        check(it is UnlockedViewModel.Broadcast.OnCopy)
                        assertEquals(secret, it.secret)
                    }
            }
            viewModel.requestToCopy(key = secretKey, id = id)
            job.join()
        }
    }

    @Test
    fun requestToShowTest() {
        runTest(timeout = 2.seconds) {
            val issuer = "UnlockedViewModelTest:requestToShowTest"
            val id = mockUUID()
            val secret = "requestToShowTest:secret"
            val dataBase = mockDataBase(
                secrets = mapOf(
                    id to ("foo:title" to secret),
                ),
            )
            val dataBaseDecrypted = "dataBase:decrypted".toByteArray()
            val dataBaseEncrypted = "dataBase:encrypted".toByteArray()
            val symmetric = mockKeyMeta()
            val symmetricDecrypted = "symmetric:decrypted".toByteArray()
            val secretKey = MockSecretKey(issuer = issuer)
            val pathNames = mockPathNames()
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
                            pathNames.dataBase to dataBaseEncrypted,
                            pathNames.symmetric to symmetricDecrypted,
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
            val viewModel = UnlockedViewModel(injection)
            val job = launch(UnconfinedTestDispatcher()) {
                viewModel
                    .broadcast
                    .collectFirst {
                        check(it is UnlockedViewModel.Broadcast.OnShow)
                        assertEquals(secret, it.secret)
                    }
            }
            viewModel.requestToShow(key = secretKey, id = id)
            job.join()
        }
    }

    @Test
    fun addValueTest() {
        val issuer = "UnlockedViewModelTest:addValueTest"
        val id = mockUUID()
        val title = "addValueTest:title"
        val secret = "addValueTest:secret"
        val initDataBase = mockDataBase()
        val initDataBaseDecrypted = "initDataBase:decrypted".toByteArray()
        val initDataBaseEncrypted = "initDataBase:encrypted".toByteArray()
        val dataBaseRef = AtomicReference(initDataBaseEncrypted)
        val updated = 42.seconds
        val editedDataBase = initDataBase.copy(
            updated = updated,
            secrets = mapOf(id to (title to secret)),
        )
        val editedDataBaseDecrypted = "editedDataBase:decrypted".toByteArray()
        val editedDataBaseEncrypted = "editedDataBase:encrypted".toByteArray()
        val editedDataBaseSignature = "editedDataBase:signature".toByteArray()
        val symmetric = mockKeyMeta()
        val symmetricDecrypted = "symmetric:decrypted".toByteArray()
        val asymmetric = mockAsymmetricKey(issuer = issuer)
        val asymmetricDecrypted = "asymmetric:decrypted".toByteArray()
        val privateKey = MockPrivateKey("UnlockedViewModelTest:addValueTest:privateKey".toByteArray())
        val publicKey = MockPublicKey("UnlockedViewModelTest:addValueTest:publicKey".toByteArray())
        val secretKey = MockSecretKey(issuer = issuer)
        runTest(timeout = 2.seconds) {
            val pathNames = mockPathNames()
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
                        uuids = MockUUIDGenerator(uuid = id),
                        keyFactory = MockKeyFactoryProvider(
                            privateKey = privateKey,
                        ),
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
                time = MockTimeProvider(
                    now = updated,
                ),
            )
            val viewModel = UnlockedViewModel(injection)
            viewModel
                .encrypteds
                .take(3)
                .collectIndexed { index, value ->
                    when (index) {
                        0 -> {
                            assertNull(value)
                            viewModel.requestValues(key = secretKey)
                        }
                        1 -> {
                            assertNotNull(value)
                            checkNotNull(value)
                            assertTrue(value.isEmpty())
                            viewModel.addValue(key = secretKey, title = title, secret = secret)
                        }
                        2 -> {
                            assertNotNull(value)
                            checkNotNull(value)
                            assertTrue(value.size == 1)
                            val (actualId, actualTitle) = value.entries.firstOrNull() ?: error("No entries!")
                            assertEquals("Id error!", id, actualId)
                            assertEquals(title, actualTitle)
                        }
                        else -> error("Unexpected index: $index!")
                    }
                }
            val job = launch(UnconfinedTestDispatcher()) {
                viewModel
                    .broadcast
                    .collectFirst {
                        check(it is UnlockedViewModel.Broadcast.OnShow)
                        assertEquals(secret, it.secret)
                    }
            }
            dataBaseRef.set(editedDataBaseEncrypted)
            viewModel.requestToShow(key = secretKey, id = id)
            job.join()
        }
    }

    @Test
    fun deleteValueTest() {
        runTest(timeout = 2.seconds) {
            val issuer = "UnlockedViewModelTest:deleteValueTest"
            val id = mockUUID()
            val title = "deleteValueTest:title"
            val secret = "deleteValueTest:secret"
            val idForDelete = mockUUID()
            check(id != idForDelete)
            val initDataBase = mockDataBase(
                secrets = mapOf(
                    id to (title to secret),
                    idForDelete to ("bar:title" to "bar:secret"),
                ),
            )
            check(initDataBase.secrets.size == 2)
            val initDataBaseDecrypted = "initDataBase:decrypted".toByteArray()
            val initDataBaseEncrypted = "initDataBase:encrypted".toByteArray()
            val dataBaseRef = AtomicReference(initDataBaseEncrypted)
            val updated = 128.seconds
            val editedDataBase = initDataBase.copy(
                updated = updated,
                secrets = mapOf(id to (title to secret)),
            )
            check(editedDataBase.secrets.size == 1)
            val editedDataBaseDecrypted = "editedDataBase:decrypted".toByteArray()
            val editedDataBaseEncrypted = "editedDataBase:encrypted".toByteArray()
            val editedDataBaseSignature = "editedDataBase:signature".toByteArray()
            check(initDataBase.updated.inWholeMilliseconds < editedDataBase.updated.inWholeMilliseconds)
            val symmetric = mockKeyMeta()
            val symmetricDecrypted = "symmetric:decrypted".toByteArray()
            val asymmetric = mockAsymmetricKey(issuer = issuer)
            val asymmetricDecrypted = "asymmetric:decrypted".toByteArray()
            val privateKey = MockPrivateKey("UnlockedViewModelTest:deleteValueTest:privateKey".toByteArray())
            val publicKey = MockPublicKey("UnlockedViewModelTest:deleteValueTest:publicKey".toByteArray())
            val secretKey = MockSecretKey(issuer = issuer)
            val pathNames = mockPathNames()
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
            val viewModel = UnlockedViewModel(injection)
            viewModel
                .encrypteds
                .take(3)
                .collectIndexed { index, value ->
                    when (index) {
                        0 -> {
                            assertNull(value)
                            viewModel.requestValues(key = secretKey)
                        }
                        1 -> {
                            assertNotNull(value)
                            checkNotNull(value)
                            val titles = initDataBase.secrets.mapValues { (_, pair) ->
                                val (t, _) = pair
                                t
                            }
                            assertEquals(titles, value)
                            viewModel.deleteValue(key = secretKey, id = idForDelete)
                        }
                        2 -> {
                            assertNotNull(value)
                            checkNotNull(value)
                            assertTrue(value.size == 1)
                            val titles = editedDataBase.secrets.mapValues { (_, pair) ->
                                val (t, _) = pair
                                t
                            }
                            assertEquals(titles, value)
                        }
                        else -> error("Unexpected index: $index!")
                    }
                }
            val job = launch(UnconfinedTestDispatcher()) {
                viewModel
                    .broadcast
                    .collectFirst {
                        check(it is UnlockedViewModel.Broadcast.OnShow)
                        assertEquals(secret, it.secret)
                    }
            }
            dataBaseRef.set(editedDataBaseEncrypted)
            viewModel.requestToShow(key = secretKey, id = id)
            job.join()
        }
    }
}
