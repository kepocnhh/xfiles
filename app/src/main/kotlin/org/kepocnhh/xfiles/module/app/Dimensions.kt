package org.kepocnhh.xfiles.module.app

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Immutable

@Deprecated("val insets = LocalView.current.rootWindowInsets.toPaddings()")
@Immutable
internal data class Dimensions(
    val insets: PaddingValues,
)
