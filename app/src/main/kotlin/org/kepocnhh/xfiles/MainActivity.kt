package org.kepocnhh.xfiles

import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import org.kepocnhh.xfiles.module.app.ColorsType
import org.kepocnhh.xfiles.module.app.Language
import org.kepocnhh.xfiles.module.app.ThemeState
import org.kepocnhh.xfiles.module.router.RouterScreen
import java.security.Security

internal class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val algs = Security.getAlgorithms("Cipher")
        println("algs: $algs")
        setContent {
            BackHandler {
                finish()
            }
            // todo theme state
            App.Theme.Composition(
                themeState = ThemeState(
                    colorsType = ColorsType.AUTO,
                    language = Language.AUTO,
                ),
            ) {
                RouterScreen()
            }
        }
    }
}
