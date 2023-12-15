package org.kepocnhh.xfiles

import java.security.Provider
import java.security.Provider.Service

internal fun Provider.testService(
    type: String,
    algorithm: String,
    className: String = "foo",
    aliases: List<String> = emptyList(),
    attributes: Map<String, String> = emptyMap(),
): Service {
    return Service(
        this,
        type,
        algorithm,
        className,
        aliases,
        attributes,
    )
}
