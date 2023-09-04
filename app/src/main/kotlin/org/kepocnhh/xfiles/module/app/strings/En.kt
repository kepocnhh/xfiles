package org.kepocnhh.xfiles.module.app.strings

import org.kepocnhh.xfiles.module.app.Strings

internal object En : Strings {
    override val yes: String = "Yes"
    override val databaseExists = "The database exists. Enter the pin code to unlock."

    override fun databaseDelete(tag: String): String {
        return "Or you can [$tag](delete) the base."
    }

    override val colors = "Colors"
    override val dark = "Dark"
    override val light = "Light"
    override val auto = "Auto"
    override val language = "Language"
    override val english = "English"
    override val russian = "Russian"
}
