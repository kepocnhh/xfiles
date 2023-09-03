package org.kepocnhh.xfiles.module.app

import androidx.compose.runtime.Immutable

@Immutable
internal interface Strings {
    val yes: String
    val databaseExists: String
    fun databaseDelete(tag: String): String
    val colors: String
    val dark: String
    val light: String
    val auto: String
}
