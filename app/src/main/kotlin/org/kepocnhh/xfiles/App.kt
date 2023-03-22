package org.kepocnhh.xfiles

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
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
import org.kepocnhh.xfiles.presentation.util.androidx.compose.Colors
import java.io.File

internal class App : Application() {
    companion object {
        private val _loggerFactory: LoggerFactory = FinalLoggerFactory()

        private var _viewModelFactory: ViewModelProvider.Factory? = null

        private val localColors = staticCompositionLocalOf<Colors> { Colors.Dark }

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

        @Composable
        fun Theme(content: @Composable () -> Unit) {
            CompositionLocalProvider(
                localColors provides Colors.Dark,
                content = content,
            )
        }
    }

    object Theme {
        val colors: Colors
            @Composable
            @ReadOnlyComposable
            get() = localColors.current
    }

    override fun onCreate() {
        super.onCreate()
        val injection = Injection(
            contexts = Contexts(
                main = Dispatchers.Main,
                io = Dispatchers.IO,
            ),
            file = FinalEncryptedFileProvider(
                context = this,
                file = File(cacheDir, BuildConfig.APPLICATION_ID),
            ),
        )
        _viewModelFactory = object : ViewModelProvider.Factory {
            override fun <U : ViewModel> create(modelClass: Class<U>): U {
                return modelClass.getConstructor(Injection::class.java).newInstance(injection)
            }
        }
    }
}
