package org.kepocnhh.xfiles.module.app

import androidx.compose.runtime.Immutable

@Immutable
internal interface Strings {
    val yes: String
    val noDatabase: String
    val databaseExists: String
    val databaseDelete: String
    val dark: String
    val light: String
    val auto: String
    val english: String
    val russian: String

    val dialogs: Dialogs

    data class Dialogs(
        val databaseDelete: String,
    )

    val settings: Settings

    data class Settings(
        val colors: String,
        val language: String,
        val cipher: String,
        val aes: String,
        val pbe: String,
        val dsa: String,
        val keyLength: String,
        val iterations: String,
    )

    val enter: Enter

    data class Enter(
        val cantAuthWithDC: String,
        val unrecoverableDC: String,
    )
}
