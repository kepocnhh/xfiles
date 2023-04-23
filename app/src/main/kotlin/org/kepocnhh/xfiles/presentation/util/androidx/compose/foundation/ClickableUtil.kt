package org.kepocnhh.xfiles.presentation.util.androidx.compose.foundation

import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Indication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType.Companion.KeyDown
import androidx.compose.ui.input.key.KeyEventType.Companion.KeyUp
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.modifier.ModifierLocalConsumer
import androidx.compose.ui.modifier.ModifierLocalReadScope
import androidx.compose.ui.modifier.modifierLocalOf
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.onLongClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.center
import androidx.compose.ui.unit.toOffset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal val NoneMutableInteractionSource = MutableInteractionSource()
private val ModifierLocalScrollableContainer = modifierLocalOf { false }

internal fun Modifier.onClick(
    interactionSource: MutableInteractionSource = NoneMutableInteractionSource,
    onClick: () -> Unit
): Modifier {
    return clickable(
        interactionSource = interactionSource,
        indication = null,
        onClick = onClick
    )
}

internal fun Modifier.catchClicks(): Modifier {
    return onClick(onClick = {})
}

private fun View.isInScrollableViewGroup(): Boolean {
    var p = parent
    while (p != null && p is ViewGroup) {
        if (p.shouldDelayChildPressedState()) {
            return true
        }
        p = p.parent
    }
    return false
}

internal fun Modifier.combinedClickable(
    interactionSource: MutableInteractionSource,
    indication: Indication?,
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    onLongClickLabel: String? = null,
    onLongClick: (() -> Unit)? = null,
    onDoubleClick: (() -> Unit)? = null,
    onClick: () -> Unit
) = composed {
    val onClickState = rememberUpdatedState(onClick)
    val onLongClickState = rememberUpdatedState(onLongClick)
    val onDoubleClickState = rememberUpdatedState(onDoubleClick)
    val hasLongClick = onLongClick != null
    val hasDoubleClick = onDoubleClick != null
    val pressedInteraction = remember { mutableStateOf<PressInteraction.Press?>(null) }
    val currentKeyPressInteractions = remember { mutableMapOf<Key, PressInteraction.Press>() }
    if (enabled) {
        // Handles the case where a long click causes a null onLongClick lambda to be passed,
        // so we can cancel the existing press.
        DisposableEffect(hasLongClick) {
            onDispose {
                pressedInteraction.value?.let { oldValue ->
                    val interaction = PressInteraction.Cancel(oldValue)
                    interactionSource.tryEmit(interaction)
                    pressedInteraction.value = null
                }
            }
        }
        DisposableEffect(interactionSource) {
            onDispose {
                pressedInteraction.value?.let { oldValue ->
                    val interaction = PressInteraction.Cancel(oldValue)
                    interactionSource.tryEmit(interaction)
                    pressedInteraction.value = null
                }
                currentKeyPressInteractions.values.forEach {
                    interactionSource.tryEmit(PressInteraction.Cancel(it))
                }
                currentKeyPressInteractions.clear()
            }
        }
    }
    val view = LocalView.current
    val isRootInScrollableContainer = {
        view.isInScrollableViewGroup()
    }
    val isClickableInScrollableContainer = remember { mutableStateOf(true) }
    val delayPressInteraction = rememberUpdatedState {
        isClickableInScrollableContainer.value || isRootInScrollableContainer()
    }
    val centreOffset = remember { mutableStateOf(Offset.Zero) }

    val gesture =
        Modifier.pointerInput(interactionSource, hasLongClick, hasDoubleClick, enabled) {
            centreOffset.value = size.center.toOffset()
            detectTapGestures(
                onDoubleTap = if (hasDoubleClick && enabled) {
                    { onDoubleClickState.value?.invoke() }
                } else {
                    null
                },
                onLongPress = if (hasLongClick && enabled) {
                    { onLongClickState.value?.invoke() }
                } else {
                    null
                },
                onPress = { offset ->
                    if (enabled) {
                        // todo
                    }
                },
                onTap = { if (enabled) onClickState.value.invoke() }
            )
        }
    Modifier
        .then(
            remember {
                object : ModifierLocalConsumer {
                    override fun onModifierLocalsUpdated(scope: ModifierLocalReadScope) {
                        with(scope) {
                            isClickableInScrollableContainer.value =
                                ModifierLocalScrollableContainer.current
                        }
                    }
                }
            }
        )
        .genericClickableWithoutGesture(
            gestureModifiers = gesture,
            interactionSource = interactionSource,
            indication = indication,
            indicationScope = rememberCoroutineScope(),
            currentKeyPressInteractions = currentKeyPressInteractions,
            keyClickOffset = centreOffset,
            enabled = enabled,
            onClickLabel = onClickLabel,
            role = role,
            onLongClickLabel = onLongClickLabel,
            onLongClick = onLongClick,
            onClick = onClick
        )
}

private fun Modifier.genericClickableWithoutGesture(
    gestureModifiers: Modifier,
    interactionSource: MutableInteractionSource,
    indication: Indication?,
    indicationScope: CoroutineScope,
    currentKeyPressInteractions: MutableMap<Key, PressInteraction.Press>,
    keyClickOffset: State<Offset>,
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    onLongClickLabel: String? = null,
    onLongClick: (() -> Unit)? = null,
    onClick: () -> Unit
): Modifier {
    fun Modifier.clickSemantics() = this.semantics(mergeDescendants = true) {
        if (role != null) {
            this.role = role
        }
        // b/156468846:  add long click semantics and double click if needed
        this.onClick(
            action = { onClick(); true },
            label = onClickLabel
        )
        if (onLongClick != null) {
            this.onLongClick(action = { onLongClick(); true }, label = onLongClickLabel)
        }
        if (!enabled) {
            disabled()
        }
    }

    fun Modifier.detectPressAndClickFromKey() = this.onKeyEvent { keyEvent ->
        when {
            enabled && keyEvent.isPress -> {
                // If the key already exists in the map, keyEvent is a repeat event.
                // We ignore it as we only want to emit an interaction for the initial key press.
                if (!currentKeyPressInteractions.containsKey(keyEvent.key)) {
                    val press = PressInteraction.Press(keyClickOffset.value)
                    currentKeyPressInteractions[keyEvent.key] = press
                    indicationScope.launch { interactionSource.emit(press) }
                    true
                } else {
                    false
                }
            }
            enabled && keyEvent.isClick -> {
                currentKeyPressInteractions.remove(keyEvent.key)?.let {
                    indicationScope.launch {
                        interactionSource.emit(PressInteraction.Release(it))
                    }
                }
                onClick()
                true
            }
            else -> false
        }
    }
    return this
        .clickSemantics()
        .detectPressAndClickFromKey()
        .indication(interactionSource, indication)
        .hoverable(enabled = enabled, interactionSource = interactionSource)
        .focusableInNonTouchMode(enabled = enabled, interactionSource = interactionSource)
        .then(gestureModifiers)
}

private fun Modifier.focusableInNonTouchMode(
    enabled: Boolean,
    interactionSource: MutableInteractionSource?
) = composed {
    val inputModeManager = LocalInputModeManager.current
    Modifier
        .focusProperties { canFocus = inputModeManager.inputMode != InputMode.Touch }
        .focusable(enabled, interactionSource)
}

private val KeyEvent.isPress: Boolean
    get() = type == KeyDown

private val KeyEvent.isClick: Boolean
    get() = type == KeyUp
