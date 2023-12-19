package org.kepocnhh.xfiles.provider

internal fun mockPathNames(
    symmetric: String = "foo:symmetric",
    asymmetric: String = "bar:asymmetric",
    dataBase: String = "baz:dataBase",
    dataBaseSignature: String = "a1:dataBaseSignature",
    biometric: String = "a2:biometric",
): PathNames {
    return PathNames(
        symmetric = symmetric,
        asymmetric = asymmetric,
        dataBase = dataBase,
        dataBaseSignature = dataBaseSignature,
        biometric = biometric,
    )
}
