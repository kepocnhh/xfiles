package org.kepocnhh.xfiles.presentation.util.androidx.compose.ui.tooling.preview

import android.content.ContextWrapper
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import org.kepocnhh.xfiles.App
import java.security.KeyStore
import java.security.KeyStoreSpi
import java.security.Provider
import java.security.Security

@Composable
internal fun AppPreview(content: @Composable () -> Unit) {
    Security.addProvider(object : Provider("AndroidKeyStore", 1.0, "") {
        init {
            put("KeyStore", FakeKeyStore::class.java.name)
//            put("KeyGenerator.AES", FakeAesKeyGenerator::class.java.name)
        }
    })
    val application = App()
    val context = LocalContext.current.applicationContext
    ContextWrapper::class.java.getDeclaredField("mBase").also {
        it.isAccessible = true
        it.set(application, context)
    }
    application.onCreate()
    App.Theme.Composition {
        content()
    }
}
