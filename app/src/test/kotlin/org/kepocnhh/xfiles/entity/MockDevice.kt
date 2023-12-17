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
