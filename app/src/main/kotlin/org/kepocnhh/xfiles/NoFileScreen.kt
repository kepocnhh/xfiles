package org.kepocnhh.xfiles

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
internal fun NoFileScreen(
    onCreate: () -> Unit
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
            val viewModel = viewModel<NoFileViewModel>()
            val state by viewModel.state.collectAsState()
            when (state) {
                NoFileViewModel.State.Intermediate -> {
                    Text(text = "loading...", TextAlign.Center)
                }
                NoFileViewModel.State.Success -> onCreate()
                null -> {
                    val context = LocalContext.current
                    Button(
                        text = "create file",
                        onClick = {
                            val file = File(context.cacheDir, BuildConfig.APPLICATION_ID)
                            viewModel.createFile(file)
                        }
                    )
                }
            }
        }
    }
}

internal class NoFileViewModel : ViewModel() {
    sealed interface State {
        object Intermediate : State
        object Success : State
    }

    private val _state = MutableStateFlow<State?>(null)
    val state = _state.asStateFlow()

    fun createFile(file: File) {
        viewModelScope.launch {
            _state.value = State.Intermediate
            withContext(Dispatchers.Default) {
                file.deleteRecursively()
                delay(2.seconds)
                file.writeText("foo bar")
            }
            _state.value = State.Success
        }
    }
}
