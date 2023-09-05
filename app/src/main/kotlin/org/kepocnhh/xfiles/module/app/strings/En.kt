package org.kepocnhh.xfiles.module.app.strings

import org.kepocnhh.xfiles.module.app.Strings

internal object En : Strings {
    override val yes: String = "Yes"

    override val noDatabase = "There is no database yet. Enter the pin code to create a new secure database."
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

    override val dialogs = Strings.Dialogs(
        databaseDelete = "Do you want to delete the database? It cannot be undone.",
    )
}
