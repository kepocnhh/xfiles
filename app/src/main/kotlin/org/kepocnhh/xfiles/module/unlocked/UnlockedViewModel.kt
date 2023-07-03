package org.kepocnhh.xfiles.module.unlocked

import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.kepocnhh.xfiles.util.security.decrypt
import java.io.File
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec

internal class UnlockedViewModel : ViewModel() {
    private val algorithm = "PBEWITHHMACSHA256ANDAES_256" // todo

    private val _data = MutableStateFlow<Map<String, String>?>(null)
    val data = _data.asStateFlow()

    fun requestData(parent: File, key: SecretKey) {
        viewModelScope.launch {
            val decrypted = withContext(Dispatchers.IO) {
                val jsonObject = JSONObject(parent.resolve("sym.json").readText())
                val iv = jsonObject.getString("iv").let { Base64.decode(it, Base64.DEFAULT) }
                val cipher = Cipher.getInstance(algorithm)
                val params = IvParameterSpec(iv)
                val encrypted = parent.resolve("db.json.enc").readBytes()
                cipher.decrypt(key, params, encrypted)
            }
            _data.value = mutableMapOf<String, String>().also {
                val json = JSONObject(decrypted.toString(Charsets.UTF_8))
                json.keys().forEach { key ->
                    it[key] = json.getString(key)
                }
            }
        }
    }
}
