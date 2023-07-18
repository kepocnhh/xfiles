package org.kepocnhh.xfiles

import android.app.Application
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Dispatchers
import org.kepocnhh.xfiles.module.app.Colors
import org.kepocnhh.xfiles.module.app.ColorsType
import org.kepocnhh.xfiles.module.app.Dimensions
import org.kepocnhh.xfiles.module.app.Durations
import org.kepocnhh.xfiles.module.app.Injection
import org.kepocnhh.xfiles.module.app.ThemeState
import org.kepocnhh.xfiles.provider.Contexts
import org.kepocnhh.xfiles.provider.FinalEncryptedFileProvider
import org.kepocnhh.xfiles.provider.FinalLoggerFactory
import org.kepocnhh.xfiles.provider.Logger
import org.kepocnhh.xfiles.provider.LoggerFactory
import org.kepocnhh.xfiles.util.compose.toPaddings
import kotlin.time.Duration.Companion.milliseconds

internal class App : Application() {
    object Theme {
        private val LocalColors = staticCompositionLocalOf<Colors> { error("no colors") }
        private val LocalDurations = staticCompositionLocalOf<Durations> { error("no durations") }
        private val LocalDimensions = staticCompositionLocalOf<Dimensions> { error("no dimensions") }

        val colors: Colors
            @Composable
            @ReadOnlyComposable
            get() = LocalColors.current

        val durations: Durations
            @Composable
            @ReadOnlyComposable
            get() = LocalDurations.current

        val dimensions: Dimensions
            @Composable
            @ReadOnlyComposable
            get() = LocalDimensions.current

        @Composable
        fun Composition(
            themeState: ThemeState,
            content: @Composable () -> Unit,
        ) {
            CompositionLocalProvider(
                LocalColors provides when (themeState.colorsType) {
                    ColorsType.DARK -> Colors.Dark
                    ColorsType.LIGHT -> Colors.Light
                    ColorsType.AUTO -> if (isSystemInDarkTheme()) Colors.Dark else Colors.Light
                },
                LocalDurations provides Durations(
                    animation = 250.milliseconds,
                ),
                LocalDimensions provides Dimensions(
                    insets = LocalView.current.rootWindowInsets.toPaddings(),
                ),
                content = content,
            )
        }
    }

    override fun onCreate() {
        super.onCreate()
        val injection = Injection(
            loggers = _loggerFactory,
            contexts = Contexts(
                main = Dispatchers.Main,
                default = Dispatchers.Default,
            ),
            files = FinalEncryptedFileProvider(context = this),
        )
        _viewModelFactory = object : ViewModelProvider.Factory {
            override fun <U : ViewModel> create(modelClass: Class<U>): U {
                return modelClass.getConstructor(Injection::class.java).newInstance(injection)
            }
        }
    }

    companion object {
        private val _loggerFactory: LoggerFactory = FinalLoggerFactory
        private var _viewModelFactory: ViewModelProvider.Factory? = null

        @Composable
        fun newLogger(tag: String): Logger {
            return remember(tag) {
                _loggerFactory.newLogger(tag)
            }
        }

        @Composable
        inline fun <reified T : ViewModel> viewModel(): T {
            return viewModel(factory = checkNotNull(_viewModelFactory))
        }
    }
}
