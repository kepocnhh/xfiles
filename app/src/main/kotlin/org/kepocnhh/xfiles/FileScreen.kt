package org.kepocnhh.xfiles

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.time.Duration.Companion.seconds

@Composable
internal fun FileScreen(
    onDelete: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
        ) {
            val viewModel = viewModel<FileViewModel>()
            val state = viewModel.state.collectAsState().value
            val context = LocalContext.current
            val file = File(context.cacheDir, BuildConfig.APPLICATION_ID)
            when (state) {
                FileViewModel.State.Deleted -> {
                    onDelete()
                }
                is FileViewModel.State.Text -> {
                    Text(text = "file: ${file.absolutePath}", textAlign = TextAlign.Start)
                    Text(text = "data: ${state.value}", textAlign = TextAlign.Start)
                    Button(
                        text = "delete file",
                        onClick = {
                            viewModel.deleteFile(file)
                        }
                    )
                }
                FileViewModel.State.Intermediate -> {
                    Text(text = "loading...", textAlign = TextAlign.Center)
                }
                null -> {
                    viewModel.requestFile(file)
                }
            }
        }
    }
}

internal class FileViewModel : ViewModel() {
    sealed interface State {
        object Intermediate : State
        class Text(val value: String) : State
        object Deleted : State
    }

    private val _state = MutableStateFlow<State?>(null)
    val state = _state.asStateFlow()

    fun requestFile(file: File) {
        viewModelScope.launch {
            _state.value = State.Intermediate
            val text = withContext(Dispatchers.Default) {
                delay(2.seconds)
                file.readText()
            }
            _state.value = State.Text(text)
        }
    }

    fun deleteFile(file: File) {
        viewModelScope.launch {
            _state.value = State.Intermediate
            withContext(Dispatchers.Default) {
                delay(2.seconds)
                file.deleteRecursively()
            }
            _state.value = State.Deleted
        }
    }
}
