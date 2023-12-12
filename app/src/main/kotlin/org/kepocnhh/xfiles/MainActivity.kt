package org.kepocnhh.xfiles

import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import org.kepocnhh.xfiles.module.router.RouterScreen
import org.kepocnhh.xfiles.module.theme.ThemeViewModel

internal class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BackHandler {
                finish()
            }
            val themeViewModel = App.viewModel<ThemeViewModel>()
            val themeState = themeViewModel.state.collectAsState().value
            LaunchedEffect(Unit) {
                if (themeState == null) {
                    themeViewModel.requestThemeState()
                }
            }
            if (themeState != null) {
                App.Theme.Composition(themeState = themeState) {
                    RouterScreen(onBack = ::finish)
                }
            }
        }
    }
}
