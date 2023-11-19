package org.kepocnhh.xfiles.module.app.strings

import org.kepocnhh.xfiles.module.app.Strings

internal object En : Strings {
    override val yes: String = "Yes"
    override val no: String = "No"
    override val exit: String = "Exit"

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

    override val checks = Strings.Checks(
        checking = "Checking...",
        checkingType = "Checking of %s...",
        error = "Check of %s failed!",
        securityServices = "security services",
        ids = "identifiers",
    )

    override val unlocked = Strings.Unlocked(
        copied = "Copied.",
        noItems = "There are no entries yet.\nClick on [%s](+) to add a new one.",
        deleteItem = "Delete entry \"%s\"?"
    )

    override val addItem = Strings.AddItem(
        promptTitle = "Come up with a name for your secret:",
        promptSecret = "Enter your secret here:",
        hintTitle = "Title",
        hintSecret = "Secret",
        next = "next",
        done = "done",
    )

    override val keyboard = Strings.Keyboard(
        space = "space",
    )
}
