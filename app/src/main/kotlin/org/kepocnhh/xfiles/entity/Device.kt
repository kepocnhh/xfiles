package org.kepocnhh.xfiles.entity

internal data class Device(
    val manufacturer: String,
    val brand: String,
    val model: String,
    val name: String,
    val supportedABIs: Set<String>,
)