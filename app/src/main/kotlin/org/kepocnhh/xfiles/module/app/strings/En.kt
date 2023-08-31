package org.kepocnhh.xfiles.module.app.strings

import org.kepocnhh.xfiles.module.app.Strings

internal object En : Strings {
    override val yes: String = "Yes"
    override val databaseExists = "The database exists. Enter the pin code to unlock."
    override val databaseDelete = "Or you can [${Strings.Tags.DELETE}](delete) the base."
}
