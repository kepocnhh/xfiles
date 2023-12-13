package org.kepocnhh.xfiles.provider

internal fun mockPathNames(
    symmetric: String = "foo",
    asymmetric: String = "bar",
    dataBase: String = "baz",
    dataBaseSignature: String = "a1",
    biometric: String = "a2",
): PathNames {
    return PathNames(
        symmetric = symmetric,
        asymmetric = asymmetric,
        dataBase = dataBase,
        dataBaseSignature = dataBaseSignature,
        biometric = biometric,
    )
}
