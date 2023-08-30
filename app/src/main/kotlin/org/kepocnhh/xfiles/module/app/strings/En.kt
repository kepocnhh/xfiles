package org.kepocnhh.xfiles.module.app.strings

import org.kepocnhh.xfiles.module.app.Strings

internal object En : Strings {
    override val yes: String = "Yes"
    override val databaseExists: Strings.Annotated = Strings.Annotated(
        texts = listOf(
            "The database exists. Enter the pin code to unlock.\nOr you can ",
            "delete",
            " the base.",
        ),
        tags = mapOf(1 to Strings.Tags.DELETE)
    )
}
