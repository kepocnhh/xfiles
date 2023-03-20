package org.kepocnhh.xfiles

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Dispatchers
import org.kepocnhh.xfiles.foundation.provider.Injection
import org.kepocnhh.xfiles.foundation.provider.coroutine.Contexts
import org.kepocnhh.xfiles.foundation.provider.logger.Logger
import org.kepocnhh.xfiles.foundation.provider.logger.LoggerFactory
import org.kepocnhh.xfiles.implementation.provider.encrypted.FinalEncryptedFileProvider
import org.kepocnhh.xfiles.implementation.provider.logger.FinalLoggerFactory
import java.io.File

internal class App : Application() {
    companion object {
        private val _loggerFactory: LoggerFactory = FinalLoggerFactory()

        private var _viewModelFactory: ViewModelProvider.Factory? = null

        @Composable
        inline fun <reified T : ViewModel> viewModel(): T {
            return viewModel(factory = checkNotNull(_viewModelFactory))
        }

        @Composable
        fun newLogger(tag: String): Logger {
            return remember(tag) {
                _loggerFactory.newLogger(tag)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        val injection = Injection(
            contexts = Contexts(
                main = Dispatchers.Main,
                io = Dispatchers.IO
            ),
            file = FinalEncryptedFileProvider(
                context = this,
                file = File(cacheDir, BuildConfig.APPLICATION_ID)
            )
        )
        _viewModelFactory = object : ViewModelProvider.Factory {
            override fun <U : ViewModel> create(modelClass: Class<U>): U {
                return modelClass.getConstructor(Injection::class.java).newInstance(injection)
            }
        }
    }
}
