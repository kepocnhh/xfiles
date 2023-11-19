package org.kepocnhh.xfiles.module.app.strings

import org.kepocnhh.xfiles.module.app.Strings

internal object Ru : Strings {
    override val yes: String = "Да"
    override val no: String = "Нет"

    override val noDatabase = "Базы данных пока нет. Введите пин-код, чтобы создать новую защищенную базу данных."
    override val databaseExists = "База данных существует. Введите пинкод чтобы разблокировать."
    override val databaseDelete = "Или вы можете [%s](удалить) базу данных."

    override val dark = "Тёмные"
    override val light = "Светлые"
    override val auto = "Автоматически"
    override val english = "Английский"
    override val russian = "Русский"

    override val dialogs = Strings.Dialogs(
        databaseDelete = "Хотите удалить базу данных? Это не может быть отменено.",
    )

    override val settings = Strings.Settings(
        colors = "Цвета",
        language = "Язык",
        cipher = "Шифр",
        aes = "AES",
        dsa = "DSA",
        pbe = "PBE",
        keyLength = "Длина ключа",
        iterations = "Количество итераций",
        hasBiometric = "Использовать биометрию",
    )

    override val enter = Strings.Enter(
        cantAuthWithDC = "Невозможно выполнить аутентификацию с использованием учетных данных устройства!",
        unrecoverableDC = "Учетные данные устройства больше не могут использоваться для аутентификации!",
    )
}
