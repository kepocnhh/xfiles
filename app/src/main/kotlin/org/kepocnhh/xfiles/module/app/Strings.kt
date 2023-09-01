package org.kepocnhh.xfiles.module.app

import androidx.compose.runtime.Immutable

@Immutable
internal interface Strings {
    val yes: String
    val databaseExists: String
    fun databaseDelete(tag: String): String
}
