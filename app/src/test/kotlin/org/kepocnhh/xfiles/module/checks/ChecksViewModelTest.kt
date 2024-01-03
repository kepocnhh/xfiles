package org.kepocnhh.xfiles.module.checks

import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.kepocnhh.xfiles.TestProvider
import org.kepocnhh.xfiles.collectFirst
import org.kepocnhh.xfiles.entity.mockDevice
import org.kepocnhh.xfiles.entity.mockSecurityServices
import org.kepocnhh.xfiles.entity.mockUUID
import org.kepocnhh.xfiles.module.app.mockEncrypted
import org.kepocnhh.xfiles.module.app.mockInjection
import org.kepocnhh.xfiles.provider.MockDeviceProvider
import org.kepocnhh.xfiles.provider.data.MockEncryptedLocalDataProvider
import org.kepocnhh.xfiles.provider.data.MockLocalDataProvider
import org.kepocnhh.xfiles.provider.mockContexts
import org.kepocnhh.xfiles.util.security.SecurityUtil
import java.security.Security
import kotlin.time.Duration.Companion.seconds

@Suppress(
    "StringLiteralDuplication",
    "IgnoredReturnValue",
)
internal class ChecksViewModelTest {
    @After
    fun after() {
        Security.removeProvider("AndroidOpenSSL")
        Security.removeProvider("BC")
    }

    @Test
    fun runChecksTest() {
        runTest(timeout = 2.seconds) {
            val cipher = SecurityUtil.ciphers.firstOrNull() ?: error("No cipher!")
            TestProvider(
                name = "BC",
                services = listOf(
                    "Cipher" to cipher,
                    "SecretKeyFactory" to cipher,
                    "KeyPairGenerator" to "DSA",
                    "Signature" to "SHA256WithDSA",
                ),
            ).also {
                Security.insertProviderAt(it, 0)
            }
            TestProvider(
                name = "AndroidOpenSSL",
                services = listOf(
                    "MessageDigest" to "SHA-512",
                    "MessageDigest" to "MD5",
                    "SecureRandom" to "SHA1PRNG",
                ),
            ).also {
                Security.insertProviderAt(it, 0)
            }
            val device = mockDevice()
            val injection = mockInjection(
                contexts = mockContexts(main = coroutineContext),
                local = MockLocalDataProvider(
                    services = null,
                    device = null,
                ),
                encrypted = mockEncrypted(
                    local = MockEncryptedLocalDataProvider(
                        appId = null,
                    ),
                ),
                devices = MockDeviceProvider(device = device),
            )
            val viewModel = ChecksViewModel(injection)
            viewModel
                .state
                .collectFirst { state ->
                    assertNull(state)
                    assertNull(injection.local.services)
                    assertNull(injection.local.device)
                    assertNull(injection.encrypted.local.appId)
                    viewModel.runChecks()
                }
            viewModel
                .broadcast
                .collectFirst { broadcast ->
                    assertEquals(ChecksViewModel.Broadcast.OnComplete, broadcast)
                    assertNotNull(injection.local.services)
                    assertEquals(device, injection.local.device)
                    assertNotNull(injection.local.device)
                    assertNotNull(injection.encrypted.local.appId)
                }
        }
    }

    @Test
    fun runChecksSkippedTest() {
        runTest(timeout = 2.seconds) {
            val cipher = SecurityUtil.ciphers.firstOrNull() ?: error("No cipher!")
            TestProvider(
                name = "BC",
                services = listOf(
                    "Cipher" to cipher,
                    "SecretKeyFactory" to cipher,
                    "KeyPairGenerator" to "DSA",
                    "Signature" to "SHA256WithDSA",
                ),
            ).also {
                Security.insertProviderAt(it, 0)
            }
            TestProvider(
                name = "AndroidOpenSSL",
                services = listOf(
                    "MessageDigest" to "SHA-512",
                    "SecureRandom" to "SHA1PRNG",
                ),
            ).also {
                Security.insertProviderAt(it, 0)
            }
            val services = mockSecurityServices()
            val device = mockDevice()
            val appId = mockUUID()
            val injection = mockInjection(
                contexts = mockContexts(main = coroutineContext),
                local = MockLocalDataProvider(
                    services = services,
                    device = device,
                ),
                encrypted = mockEncrypted(
                    local = MockEncryptedLocalDataProvider(
                        appId = appId,
                    ),
                ),
                devices = MockDeviceProvider(device = device),
            )
            val viewModel = ChecksViewModel(injection)
            viewModel
                .state
                .collectFirst { state ->
                    assertNull(state)
                    assertEquals(services, injection.local.services)
                    assertEquals(device, injection.local.device)
                    assertEquals(appId, injection.encrypted.local.appId)
                    viewModel.runChecks()
                }
            viewModel
                .broadcast
                .collectFirst { broadcast ->
                    assertEquals(ChecksViewModel.Broadcast.OnComplete, broadcast)
                    assertEquals(services, injection.local.services)
                    assertEquals(device, injection.local.device)
                    assertEquals(appId, injection.encrypted.local.appId)
                }
        }
    }

    @Test
    fun runChecksErrorTest() {
        runTest(timeout = 2.seconds) {
            val injection = mockInjection(
                contexts = mockContexts(main = coroutineContext),
                local = MockLocalDataProvider(
                    services = null,
                    device = null,
                ),
                encrypted = mockEncrypted(
                    local = MockEncryptedLocalDataProvider(
                        appId = null,
                    ),
                ),
            )
            val viewModel = ChecksViewModel(injection)
            assertNull(viewModel.state.value)
            assertNull(injection.local.services)
            assertNull(injection.local.device)
            assertNull(injection.encrypted.local.appId)
            viewModel.runChecks()
            val state = viewModel
                .state
                .firstOrNull {
                    it is ChecksViewModel.State.OnError
                }
            assertTrue(state is ChecksViewModel.State.OnError)
            check(state is ChecksViewModel.State.OnError)
            assertEquals(ChecksViewModel.ChecksType.SECURITY_SERVICES, state.type)
        }
    }
}
