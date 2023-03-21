package org.kepocnhh.xfiles.implementation.module.router

import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.withIndex
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout
import org.junit.Test
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.kepocnhh.xfiles.implementation.provider.mockInjection
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

internal class RouterViewModelTest {
//    companion object {
//        @BeforeClass
//        fun setup() {
//            Dispatchers.setMain(TestCoroutineDispatcher())
//        }
//    }

//    @get:Rule
//    val rule = InstantTaskExecutorRule()

    @Test(timeout = 10_000)
    fun requestFileTest() {
        val injection = mockInjection()
        val viewModel = RouterViewModel(injection)
        assertNull(viewModel.state.value)
        viewModel.requestFile()
        runTest {
            val exists = withTimeout(10_000) {
                suspendCoroutine { continuation ->
                    launch {
                        val exists = viewModel.state.first { it != null }
                        continuation.resume(exists)
                    }
                }
            }
            checkNotNull(exists)
            assertFalse(exists)
        }
    }

    @Test(timeout = 10_000)
    fun foo() {
        val injection = mockInjection()
        val viewModel = RouterViewModel(injection)
        runTest {
            withTimeout(10_000) {
                suspendCoroutine { continuation ->
                    launch {
                        viewModel.state
                            .withIndex()
                            .onEach { (index, value) ->
                                when (index) {
                                    0 -> {
                                        assertNull(value)
                                        viewModel.requestFile()
                                    }
                                    1 -> {
                                        checkNotNull(value)
                                        assertFalse(value)
                                        continuation.resume(Unit)
                                        cancel()
                                    }
                                    else -> TODO()
                                }
                            }
                            .catch {
                                continuation.resumeWithException(it)
                            }.collect()
                    }
                }
            }
        }
    }

    @Test
    fun bar() {
        val injection = mockInjection()
        val viewModel = RouterViewModel(injection)
        runTest(dispatchTimeoutMs = 10_000) {
            viewModel.state
                .withIndex()
                .onEach { (index, value) ->
                    when (index) {
                        0 -> {
                            assertNull(value)
                            viewModel.requestFile()
                        }
                        1 -> {
                            val exists = checkNotNull(value)
                            assertFalse("File exists!", exists)
                        }
                        else -> TODO()
                    }
                }.take(2).collect()
        }
    }
}
