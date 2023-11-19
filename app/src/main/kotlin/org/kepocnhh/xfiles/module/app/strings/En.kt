package org.kepocnhh.xfiles.module.app.strings

import org.kepocnhh.xfiles.module.app.Strings

internal object En : Strings {
    override val yes: String = "Yes"
    override val no: String = "No"

    override val noDatabase = "There is no database yet. Enter the pin code to create a new secure database."
    override val databaseExists = "The database exists. Enter the pin code to unlock."
    override val databaseDelete = "Or you can [%s](delete) the base."

    override val dark = "Dark"
    override val light = "Light"
    override val auto = "Auto"
    override val english = "English"
    override val russian = "Russian"

    override val dialogs = Strings.Dialogs(
        databaseDelete = "Do you want to delete the database? It cannot be undone.",
    )

    override val settings = Strings.Settings(
        colors = "Colors",
        language = "Language",
        cipher = "Cipher",
        aes = "AES",
        dsa = "DSA",
        pbe = "PBE",
        keyLength = "Key length",
        iterations = "Iterations",
        hasBiometric = "Has biometric",
        version = "Version",
    )

    override val enter = Strings.Enter(
        cantAuthWithDC = "Cannot authenticate with device credentials!",
        unrecoverableDC = "Device credentials can no longer be used for authentication!",
    )
}
