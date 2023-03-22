package org.kepocnhh.xfiles

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.kepocnhh.xfiles.presentation.module.router.RouterScreen
import org.kepocnhh.xfiles.presentation.util.androidx.compose.Colors

internal class MainActivity : AppCompatActivity() {
    override fun onCreate(inState: Bundle?) {
        super.onCreate(inState)
        setContent {
            App.Theme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(App.Theme.colors.background),
                ) {
                    RouterScreen()
                }
            }
        }
    }
}
