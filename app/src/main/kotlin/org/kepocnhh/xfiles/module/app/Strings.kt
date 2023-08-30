package org.kepocnhh.xfiles.module.app

import androidx.compose.runtime.Immutable

@Immutable
internal interface Strings {
    object Tags {
        const val DELETE = "delete"
    }

    val yes: String
    val databaseExists: AnnotatedText

    data class AnnotatedText(
        val text: String,
        val tags: Map<IntRange, String>,
    )
}
