package org.kepocnhh.xfiles.implementation.module.router

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withTimeout
import org.junit.Test
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.BeforeClass
import org.junit.Rule
import org.kepocnhh.xfiles.implementation.provider.mockInjection
import kotlin.coroutines.resume
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
}
