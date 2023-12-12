package org.kepocnhh.xfiles.util

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Suppress("FunctionMinLength")
internal fun Float.ct(k: Float): Float {
    return (this % k + k) % k
}

@Suppress("FunctionMinLength")
internal fun Dp.ct(k: Dp): Dp {
    return ((value % k.value + k.value) % k.value).dp
}
