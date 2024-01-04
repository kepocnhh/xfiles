package org.kepocnhh.xfiles.entity

internal fun mockDevice(
    manufacturer: String = "foo",
    brand: String = "bar",
    model: String = "baz",
    name: String = "qux",
    supportedABIs: Set<String> = emptySet(),
): Device {
    return Device(
        manufacturer = manufacturer,
        brand = brand,
        model = model,
        name = name,
        supportedABIs = supportedABIs,
    )
}

internal fun mockDevice(issuer: String): Device {
    return mockDevice(
        manufacturer = "$issuer:manufacturer",
        brand = "$issuer:brand",
        model = "$issuer:model",
        name = "$issuer:name",
        supportedABIs = (1..4).map { "$issuer:supportedABI:$it" }.toSet(),
    )
}
