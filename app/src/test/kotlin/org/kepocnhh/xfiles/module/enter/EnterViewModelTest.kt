package org.kepocnhh.xfiles.module.enter

import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.kepocnhh.xfiles.entity.mockSecuritySettings
import org.kepocnhh.xfiles.module.app.mockEncrypted
import org.kepocnhh.xfiles.module.app.mockInjection
import org.kepocnhh.xfiles.provider.MockEncryptedFileProvider
import org.kepocnhh.xfiles.provider.data.MockLocalDataProvider
import org.kepocnhh.xfiles.provider.mockPathNames
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
}
