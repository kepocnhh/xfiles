package org.kepocnhh.xfiles

import android.app.Application
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Dispatchers
import org.kepocnhh.xfiles.entity.Defaults
import org.kepocnhh.xfiles.entity.SecuritySettings
import org.kepocnhh.xfiles.module.app.Colors
import org.kepocnhh.xfiles.module.app.ColorsType
import org.kepocnhh.xfiles.module.app.Dimensions
import org.kepocnhh.xfiles.module.app.Durations
import org.kepocnhh.xfiles.module.app.Injection
import org.kepocnhh.xfiles.module.app.Language
import org.kepocnhh.xfiles.module.app.Sizes
import org.kepocnhh.xfiles.module.app.Strings
import org.kepocnhh.xfiles.module.app.ThemeState
import org.kepocnhh.xfiles.module.app.strings.En
import org.kepocnhh.xfiles.module.app.strings.Ru
import org.kepocnhh.xfiles.provider.Contexts
import org.kepocnhh.xfiles.provider.FinalEncryptedFileProvider
import org.kepocnhh.xfiles.provider.FinalLoggerFactory
import org.kepocnhh.xfiles.provider.Logger
import org.kepocnhh.xfiles.provider.PathNames
import org.kepocnhh.xfiles.provider.data.FinalLocalDataProvider
import org.kepocnhh.xfiles.provider.security.FinalSecurityProvider
import org.kepocnhh.xfiles.util.compose.toPaddings
import org.kepocnhh.xfiles.util.lifecycle.AbstractViewModel
import sp.ax.jc.dialogs.DialogStyle
import sp.ax.jc.dialogs.LocalDialogStyle
import kotlin.time.Duration.Companion.milliseconds

internal class App : Application() {
    object Theme {
        private val LocalColors = staticCompositionLocalOf<Colors> { error("no colors") }
        private val LocalDurations = staticCompositionLocalOf<Durations> { error("no durations") }
        private val LocalDimensions = staticCompositionLocalOf<Dimensions> { error("no dimensions") }
        private val LocalSizes = staticCompositionLocalOf<Sizes> { error("no sizes") }
        private val LocalStrings = staticCompositionLocalOf<Strings> { error("no strings") }

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

        val sizes: Sizes
            @Composable
            @ReadOnlyComposable
            get() = LocalSizes.current

        val strings: Strings
            @Composable
            @ReadOnlyComposable
            get() = LocalStrings.current

        @Composable
        fun Composition(
            themeState: ThemeState,
            content: @Composable () -> Unit,
        ) {
            val colors = when (themeState.colorsType) {
                ColorsType.DARK -> Colors.Dark
                ColorsType.LIGHT -> Colors.Light
                ColorsType.AUTO -> if (isSystemInDarkTheme()) Colors.Dark else Colors.Light
            }
            CompositionLocalProvider(
                LocalColors provides colors,
                LocalDurations provides Durations(
                    animation = 250.milliseconds,
                ),
                LocalDimensions provides Dimensions(
                    insets = LocalView.current.rootWindowInsets.toPaddings(),
                ),
                LocalSizes provides Sizes(
                    xxxs = 2.dp,
                    xxs = 4.dp,
                    xs = 8.dp,
                    small = 16.dp,
                    medium = 24.dp,
                    large = 32.dp,
                    xl = 48.dp,
                    xxl = 56.dp,
                    xxxl = 64.dp,
                ),
                LocalStrings provides when (themeState.language) {
                    Language.ENGLISH -> En
                    Language.RUSSIAN -> Ru
                    Language.AUTO -> {
                        val locale = LocalConfiguration.current.locales.get(0)
                        when (locale?.language) {
                            "ru" -> Ru
                            else -> En
                        }
                    }
                },
                LocalDialogStyle provides DialogStyle(
                    background = colors.background,
                    foreground = colors.foreground,
                ),
                content = content,
            )
        }
    }

    override fun onCreate() {
        super.onCreate()
        val pbeIterations = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            SecuritySettings.PBEIterations.NUMBER_2_16
        } else {
            SecuritySettings.PBEIterations.NUMBER_2_20
        }
        _injection = Injection(
            loggers = FinalLoggerFactory,
            contexts = Contexts(
                main = Dispatchers.Main,
                default = Dispatchers.Default,
            ),
            files = FinalEncryptedFileProvider(context = this),
            local = FinalLocalDataProvider(
                context = this,
                defaults = Defaults(
                    themeState = ThemeState(
                        colorsType = ColorsType.AUTO,
                        language = Language.AUTO,
                    ),
                    securitySettings = SecuritySettings(
                        pbeIterations = pbeIterations,
                        aesKeyLength = SecuritySettings.AESKeyLength.BITS_256,
                        dsaKeyLength = SecuritySettings.DSAKeyLength.BITS_1024_2,
                    ),
                ),
            ),
            security = ::FinalSecurityProvider,
            pathNames = PathNames(
                symmetric = "sym.json",
                asymmetric = "asym.json",
                dataBase = "db.json.enc",
                dataBaseSignature = "db.json.sig",
            )
        )
    }

    companion object {
        private var _injection: Injection? = null
        private val _viewModelFactory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return modelClass
                    .getConstructor(Injection::class.java)
                    .newInstance(checkNotNull(_injection))
            }
        }

        @Composable
        fun newLogger(tag: String): Logger {
            return remember(tag) {
                checkNotNull(_injection).loggers.newLogger(tag)
            }
        }

        private val vmStores = mutableMapOf<String, ViewModelStore>()

        @Composable
        inline fun <reified T : AbstractViewModel> viewModel(): T {
            val key = T::class.java.simpleName
            val (dispose, store) = synchronized(App::class.java) {
                remember { !vmStores.containsKey(key) } to vmStores.getOrPut(key, ::ViewModelStore)
            }
            DisposableEffect(Unit) {
                onDispose {
                    synchronized(App::class.java) {
                        if (dispose) {
                            vmStores.remove(key)
                            store.clear()
                        }
                    }
                }
            }
            return ViewModelProvider(store, _viewModelFactory)[T::class.java]
        }
    }
}
