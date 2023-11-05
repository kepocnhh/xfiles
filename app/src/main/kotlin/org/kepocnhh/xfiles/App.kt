package org.kepocnhh.xfiles

import android.app.Application
import android.os.Build
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import kotlinx.coroutines.Dispatchers
import org.kepocnhh.xfiles.entity.Defaults
import org.kepocnhh.xfiles.entity.SecuritySettings
import org.kepocnhh.xfiles.module.app.Colors
import org.kepocnhh.xfiles.module.app.ColorsType
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
import org.kepocnhh.xfiles.util.compose.ColorIndication
import org.kepocnhh.xfiles.util.lifecycle.AbstractViewModel
import sp.ax.jc.animations.style.LocalTweenStyle
import sp.ax.jc.animations.style.TweenStyle
import sp.ax.jc.dialogs.DialogStyle
import sp.ax.jc.dialogs.LocalDialogStyle
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

internal class App : Application() {
    object Theme {
        private val LocalColors = staticCompositionLocalOf<Colors> { error("no colors") }
        private val LocalDurations = staticCompositionLocalOf<Durations> { error("no durations") }
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

        val sizes: Sizes
            @Composable
            @ReadOnlyComposable
            get() = LocalSizes.current

        val strings: Strings
            @Composable
            @ReadOnlyComposable
            get() = LocalStrings.current

        private var _textStyle: TextStyle? = null
        val textStyle: TextStyle get() = checkNotNull(_textStyle)

        @Composable
        fun Composition(
            themeState: ThemeState,
            content: @Composable () -> Unit,
        ) {
            val logger = newLogger("[Composition]")
            val colors = when (themeState.colorsType) {
                ColorsType.DARK -> Colors.dark
                ColorsType.LIGHT -> Colors.light
                ColorsType.AUTO -> if (isSystemInDarkTheme()) Colors.dark else Colors.light
            }
            logger.debug("colors: $colors")
            val durations = Durations(
                animation = 250.milliseconds,
//                animation = 500.milliseconds,
//                animation = 2.seconds, // todo
            )
            CompositionLocalProvider(
                LocalColors provides colors,
                LocalDurations provides durations,
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
                LocalIndication provides ColorIndication.create(colors.foreground),
                LocalTweenStyle provides TweenStyle(
                    duration = durations.animation,
                    delay = Duration.ZERO,
                    easing = FastOutSlowInEasing,
                ),
                content = {
                    _textStyle = TextStyle(
                        color = colors.text,
                        fontFamily = FontFamily.Default,
                        fontSize = 14.sp, // todo
                    )
                    content()
                },
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
