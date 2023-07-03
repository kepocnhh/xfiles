package org.kepocnhh.xfiles

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.kepocnhh.xfiles.module.enter.EnterScreen
import org.kepocnhh.xfiles.module.unlocked.UnlockedScreen
import javax.crypto.SecretKey

internal class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
            ) {
                val key = remember { mutableStateOf<SecretKey?>(null) }
                val durationMillis = 250
//                val durationMillis = 2_000
                AnimatedVisibility(
                    visible = key.value == null,
                    enter = slideInHorizontally(tween(durationMillis), initialOffsetX = { -it })
                            + fadeIn(tween(durationMillis)),
                    exit = slideOutHorizontally(tween(durationMillis), targetOffsetX = { -it })
                            + fadeOut(tween(durationMillis)),
                ) {
                    EnterScreen(
                        broadcast = {
                            when (it) {
                                is EnterScreen.Broadcast.Unlock -> {
                                    key.value = it.key
                                }
                            }
                        }
                    )
                }
                AnimatedVisibility(
                    visible = key.value != null,
                    enter = slideInHorizontally(tween(durationMillis), initialOffsetX = { it })
                            + fadeIn(tween(durationMillis)),
                    exit = slideOutHorizontally(tween(durationMillis), targetOffsetX = { it })
                            + fadeOut(tween(durationMillis)),
                ) {
                    UnlockedScreen(
                        key = remember { mutableStateOf(key.value!!) }.value,
                        broadcast = {
                            when (it) {
                                UnlockedScreen.Broadcast.Lock -> {
                                    key.value = null
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}
