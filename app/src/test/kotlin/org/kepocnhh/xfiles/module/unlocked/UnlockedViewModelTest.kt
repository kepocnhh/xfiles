package org.kepocnhh.xfiles.module.unlocked

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.kepocnhh.xfiles.collectFirst
import org.kepocnhh.xfiles.entity.MockSecretKey
import org.kepocnhh.xfiles.entity.mockKeyMeta
import org.kepocnhh.xfiles.entity.mockSecurityServices
import org.kepocnhh.xfiles.module.app.mockEncrypted
import org.kepocnhh.xfiles.module.app.mockInjection
import org.kepocnhh.xfiles.provider.MockEncryptedFileProvider
import org.kepocnhh.xfiles.provider.MockSerializer
import org.kepocnhh.xfiles.provider.data.MockLocalDataProvider
import org.kepocnhh.xfiles.provider.mockPathNames
import org.kepocnhh.xfiles.provider.security.MockCipherProvider
import org.kepocnhh.xfiles.provider.security.MockSecurityProvider
import kotlin.time.Duration.Companion.seconds

internal class UnlockedViewModelTest {
    @Test
    fun requestValuesTest() {
        runTest(timeout = 5.seconds) {
            val dataBase = mapOf(
                "foo:id" to ("foo:title" to "foo:secret"),
                "bar:id" to ("bar:title" to "bar:secret"),
            )
            check(dataBase.isNotEmpty())
            val dataBaseDecrypted = "dataBase:decrypted".toByteArray()
            val dataBaseEncrypted = "dataBase:encrypted".toByteArray()
            val symmetric = mockKeyMeta()
            val key = MockSecretKey()
            val pathNames = mockPathNames()
            val injection = mockInjection(
                pathNames = pathNames,
                local = MockLocalDataProvider(services = mockSecurityServices()),
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
                            viewModel.requestValues(key)
                        }
                        1 -> {
                            assertNotNull(value)
                            checkNotNull(value)
                            val titles = dataBase.mapValues { (_, pair) ->
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
        val id = "foo:id"
        val secret = "requestToCopyTest:secret"
        val dataBase = mapOf(
            id to ("foo:title" to secret),
        )
        val dataBaseDecrypted = "dataBase:decrypted".toByteArray()
        val dataBaseEncrypted = "dataBase:encrypted".toByteArray()
        val symmetric = mockKeyMeta()
        val symmetricDecrypted = "symmetric:decrypted".toByteArray()
        val key = MockSecretKey()
        val pathNames = mockPathNames()
        runTest(timeout = 5.seconds) {
            val injection = mockInjection(
                pathNames = pathNames,
                local = MockLocalDataProvider(services = mockSecurityServices()),
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
            val job = launch(Dispatchers.Default) {
                viewModel
                    .broadcast
                    .collectFirst {
                        check(it is UnlockedViewModel.Broadcast.OnCopy)
                        assertEquals(secret, it.secret)
                    }
            }
            viewModel.requestToCopy(key = key, id = id)
            job.join()
        }
    }

    @Test
    fun requestToShowTest() {
        val id = "foo:id"
        val secret = "requestToShowTest:secret"
        val dataBase = mapOf(
            id to ("foo:title" to secret),
        )
        val dataBaseDecrypted = "dataBase:decrypted".toByteArray()
        val dataBaseEncrypted = "dataBase:encrypted".toByteArray()
        val symmetric = mockKeyMeta()
        val symmetricDecrypted = "symmetric:decrypted".toByteArray()
        val key = MockSecretKey()
        val pathNames = mockPathNames()
        runTest(timeout = 5.seconds) {
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
            val job = launch(Dispatchers.Default) {
                viewModel
                    .broadcast
                    .collectFirst {
                        check(it is UnlockedViewModel.Broadcast.OnShow)
                        assertEquals(secret, it.secret)
                    }
            }
            viewModel.requestToShow(key = key, id = id)
            job.join()
        }
    }
}
