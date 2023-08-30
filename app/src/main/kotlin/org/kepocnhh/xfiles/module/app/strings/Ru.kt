package org.kepocnhh.xfiles.module.app.strings

import org.kepocnhh.xfiles.module.app.Strings

internal object Ru : Strings {
    override val yes: String = "Да"
    override val databaseExists: Strings.Annotated = Strings.Annotated(
        texts = listOf(
            "База данных существует. Введите пинкод чтобы разблокировать.\nИли вы можете ",
            "удалить",
            " базу данных.",
        ),
        tags = mapOf(1 to Strings.Tags.DELETE)
    )
}
