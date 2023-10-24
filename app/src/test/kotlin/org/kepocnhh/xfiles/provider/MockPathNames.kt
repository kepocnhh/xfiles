package org.kepocnhh.xfiles.provider

internal fun mockPathNames(
    symmetric: String = "foo",
    asymmetric: String = "bar",
    dataBase: String = "baz",
    dataBaseSignature: String = "42",
): PathNames {
    return PathNames(
        symmetric = symmetric,
        asymmetric = asymmetric,
        dataBase = dataBase,
        dataBaseSignature = dataBaseSignature,
    )
}
