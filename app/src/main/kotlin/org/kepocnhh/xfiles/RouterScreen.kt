package org.kepocnhh.xfiles

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@Composable
internal fun RouterScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val viewModel = viewModel<RouterViewModel>()
        val fileState by viewModel.fileState.collectAsState()
        val context = LocalContext.current
        val file = File(context.cacheDir, BuildConfig.APPLICATION_ID)
        when (fileState) {
            false -> NoFileScreen(
                onCreate = {
                    viewModel.requestFile(file)
                }
            )
            true -> FileScreen(
                onDelete = {
                    viewModel.requestFile(file)
                }
            )
            null -> viewModel.requestFile(file)
        }
    }
}

internal class RouterViewModel : ViewModel() {
    private val _fileState = MutableStateFlow<Boolean?>(null)
    val fileState = _fileState.asStateFlow()

    fun requestFile(file: File) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.Default) {
                file.exists() && !file.isDirectory
            }
            _fileState.value = result
        }
    }
}
