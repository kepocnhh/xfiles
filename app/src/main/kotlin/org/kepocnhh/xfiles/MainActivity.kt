package org.kepocnhh.xfiles

import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import org.kepocnhh.xfiles.module.app.ColorsType
import org.kepocnhh.xfiles.module.app.Language
import org.kepocnhh.xfiles.module.app.ThemeState
import org.kepocnhh.xfiles.module.router.RouterScreen
import org.kepocnhh.xfiles.module.theme.ThemeViewModel
import java.security.Security

internal class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BackHandler {
                finish()
            }
            val themeViewModel = App.viewModel<ThemeViewModel>()
            when (val themeState = themeViewModel.state.collectAsState().value) {
                null -> themeViewModel.requestThemeState()
                else -> {
                    App.Theme.Composition(themeState = themeState) {
                        RouterScreen()
                    }
                }
            }
        }
    }
}
