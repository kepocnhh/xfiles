package org.kepocnhh.xfiles.module.unlocked

import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
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
        runTest(timeout = 10.seconds) {
            val dataBase = mapOf(
                "foo" to "f1",
                "bar" to "b1",
            )
            val dataBaseDecrypted = "dataBase:decrypted".toByteArray()
            val dataBaseEncrypted = "dataBase:encrypted".toByteArray()
            check(dataBase.isNotEmpty())
            val symmetric = mockKeyMeta()
            val key = MockSecretKey(encoded = symmetric.ivPrivate)
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
                            assertEquals(dataBase, value)
                        }
                        else -> error("Unexpected index: $index!")
                    }
                }
        }
    }
}
