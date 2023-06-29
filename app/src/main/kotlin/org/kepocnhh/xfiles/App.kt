package org.kepocnhh.xfiles

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Dispatchers
import org.kepocnhh.xfiles.provider.Contexts
import org.kepocnhh.xfiles.provider.FinalEncryptedFileProvider
import org.kepocnhh.xfiles.provider.FinalLoggerFactory
import org.kepocnhh.xfiles.provider.Injection
import org.kepocnhh.xfiles.provider.Logger
import org.kepocnhh.xfiles.provider.LoggerFactory
import org.kepocnhh.xfiles.util.compose.Colors
import org.kepocnhh.xfiles.util.compose.Dimensions
import org.kepocnhh.xfiles.util.compose.Sizes
import org.kepocnhh.xfiles.util.compose.toPaddings
import java.io.File

internal class App : Application() {
    companion object {
        private val _loggerFactory: LoggerFactory = FinalLoggerFactory

        private var _viewModelFactory: ViewModelProvider.Factory? = null

        @Composable
        inline fun <reified T : ViewModel> viewModel(): T {
            val key = remember { System.currentTimeMillis().toString() }
            return viewModel(key = key, factory = checkNotNull(_viewModelFactory))
        }

        @Composable
        fun newLogger(tag: String): Logger {
            return remember(tag) {
                _loggerFactory.newLogger(tag)
            }
        }
    }

    object Theme {
        private val localColors = staticCompositionLocalOf<Colors> { error("no colors") }
        private val localDimensions = staticCompositionLocalOf<Dimensions> { error("no dimensions") }

        val colors: Colors
            @Composable
            @ReadOnlyComposable
            get() = localColors.current

        val dimensions: Dimensions
            @Composable
            @ReadOnlyComposable
            get() = localDimensions.current

        @Composable
        fun Composition(content: @Composable () -> Unit) {
            val colors = Colors.Light
//            val dialogStyle = DialogStyle(
//                background = colors.background,
//                paddings = PaddingValues(),
//                minWidth = 128.dp,
//                corners = 16.dp,
//                button = DialogStyle.Button(
//                    paddings = PaddingValues(),
//                    corners = 16.dp,
//                ),
//                message = DialogStyle.Message(
//                    paddings = PaddingValues(),
//                )
//            )
            CompositionLocalProvider(
//                LocalDialogStyle provides dialogStyle,
                localColors provides colors,
                localDimensions provides Dimensions(
                    insets = LocalView.current.rootWindowInsets.toPaddings(),
                    sizes = Sizes(
                        xxxs = 2.dp,
                        xxs = 4.dp,
                        xs = 8.dp,
                        s = 16.dp,
                        m = 24.dp,
                        l = 32.dp,
                        xl = 48.dp,
                        xxl = 56.dp,
                        xxxl = 64.dp,
                    )
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
