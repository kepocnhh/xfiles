package org.kepocnhh.xfiles.module.app

import androidx.compose.runtime.Immutable

@Immutable
internal interface Strings {
    object Tags {
        const val DELETE = "delete"
    }

    val yes: String
    val databaseExists: Annotated

    data class Annotated(val texts: List<String>, val tags: Map<Int, String>)
}
