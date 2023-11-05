package org.kepocnhh.xfiles.module.checks

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.kepocnhh.xfiles.App
import org.kepocnhh.xfiles.util.compose.Squares
import kotlin.time.Duration.Companion.seconds

@Composable
private fun BoxScope.OnError(
    type: ChecksViewModel.ChecksType,
    error: Throwable,
    onExit: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.Center),
    ) {
        BasicText(
            modifier = Modifier
                .fillMaxWidth()
                .height(App.Theme.sizes.xxxl)
                .wrapContentSize(),
            style = App.Theme.textStyle,
            text = "check type $type error: $error", // todo
        )
        BasicText(
            modifier = Modifier
                .fillMaxWidth()
                .height(App.Theme.sizes.xxxl)
                .clickable {
                    onExit()
                }
                .wrapContentSize(),
            style = App.Theme.textStyle.copy(color = App.Theme.colors.primary),
            text = "Exit", // todo
        )
    }
}

@Composable
private fun BoxScope.OnChecks(type: ChecksViewModel.ChecksType?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.Center),
    ) {
        Squares(
            modifier = { Modifier.size(it).align(Alignment.CenterHorizontally) },
            color = App.Theme.colors.foreground,
            width = App.Theme.sizes.large,
            padding = App.Theme.sizes.small,
            radius = App.Theme.sizes.xs,
        )
        val text = when (type) {
            null -> "Check..." // todo
            else -> "Check ${type.name}..." // todo
        }
        BasicText(
            modifier = Modifier
                .fillMaxWidth()
                .height(App.Theme.sizes.xxxl)
                .wrapContentSize(),
            style = App.Theme.textStyle,
            text = text,
        )
    }
}

@Composable
internal fun ChecksScreen(
    onComplete: () -> Unit,
    onExit: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(App.Theme.colors.background),
    ) {
        val delay = App.Theme.durations.animation
        val ready = remember { CompletableDeferred<Unit>() }
        LaunchedEffect(Unit) {
            withContext(Dispatchers.Default) {
                delay(delay)
            }
            ready.complete(Unit)
        }
        val viewModel = App.viewModel<ChecksViewModel>()
        LaunchedEffect(Unit) {
            viewModel
                .broadcast
                .collect {
                    when (it) {
                        ChecksViewModel.Broadcast.OnComplete -> {
                            withContext(Dispatchers.Default) {
                                ready.await()
                            }
                            onComplete()
                        }
                    }
                }
        }
        val state = viewModel.state.collectAsState().value
        LaunchedEffect(state) {
            if (state == null) {
                viewModel.runChecks()
            }
        }
        when (state) {
            is ChecksViewModel.State.OnChecks -> {
                OnChecks(type = state.type)
            }
            is ChecksViewModel.State.OnError -> {
                OnError(type = state.type, error = state.error, onExit = onExit)
            }
            null -> {
                OnChecks(type = null)
            }
        }
    }
}
