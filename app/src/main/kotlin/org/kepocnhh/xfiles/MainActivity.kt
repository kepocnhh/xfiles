package org.kepocnhh.xfiles

import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import org.kepocnhh.xfiles.presentation.module.router.RouterScreen

internal class MainActivity : AppCompatActivity() {
    override fun onCreate(inState: Bundle?) {
        super.onCreate(inState)
        println("[Main|${hashCode()}]: on create...")
        setContent {
            App.Theme.Composition {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(App.Theme.colors.background),
                ) {
                    BackHandler {
                        finish()
                    }
                    RouterScreen()
                }
            }
        }
    }
}
