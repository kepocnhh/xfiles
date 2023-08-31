package org.kepocnhh.xfiles.module.app.strings

import org.kepocnhh.xfiles.module.app.Strings

internal object Ru : Strings {
    override val yes: String = "Да"
    override val databaseExists = "База данных существует. Введите пинкод чтобы разблокировать."
    override val databaseDelete = "Или вы можете [${Strings.Tags.DELETE}](удалить) базу данных."
}
