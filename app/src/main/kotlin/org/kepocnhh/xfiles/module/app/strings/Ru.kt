package org.kepocnhh.xfiles.module.app.strings

import org.kepocnhh.xfiles.module.app.Strings

internal object Ru : Strings {
    override val yes: String = "Да"
    override val databaseExists = "База данных существует. Введите пинкод чтобы разблокировать."

    override fun databaseDelete(tag: String): String {
        return "Или вы можете [$tag](удалить) базу данных."
    }

    override val colors = "Цвета"
    override val dark = "Тёмные"
    override val light = "Светлые"
    override val auto = "Автоматически"
}
