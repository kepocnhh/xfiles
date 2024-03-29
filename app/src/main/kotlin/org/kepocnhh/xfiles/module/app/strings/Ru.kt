package org.kepocnhh.xfiles.module.app.strings

import org.kepocnhh.xfiles.module.app.Strings

internal object Ru : Strings {
    override val yes: String = "Да"
    override val no: String = "Нет"
    override val exit: String = "Выход"

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
        version = "Версия",
    )

    override val enter = Strings.Enter(
        cantAuthWithDC = "Невозможно выполнить аутентификацию с использованием учетных данных устройства!",
        unrecoverableDC = "Учетные данные устройства больше не могут использоваться для аутентификации!",
    )

    override val checks = Strings.Checks(
        checking = "Идёт проверка...",
        checkingType = "Идёт проверка %s...",
        error = "Проверка %s закончилась ошибкой!",
        securityServices = "служб безопасности",
        ids = "идентификаторов",
    )

    override val unlocked = Strings.Unlocked(
        copied = "Скопировано.",
        noItems = "Тут ещё нет записей.\nНажмите на [%s](+) чтобы добавить новую.",
        deleteItem = "Удалить запись \"%s\"?",
    )

    override val addItem = Strings.AddItem(
        promptTitle = "Придумайте название своему секрету:",
        promptSecret = "Введите свой секрет сюда:",
        hintTitle = "Название",
        hintSecret = "Секрет",
        next = "далее",
        done = "сохранить",
    )

    override val keyboard = Strings.Keyboard(
        space = "пробел",
    )

    override val biometric = Strings.Biometric(
        promptTitle = "Подтвердите, чтобы разблокировать базу данных.",
    )
}
