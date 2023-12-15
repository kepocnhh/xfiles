package org.kepocnhh.xfiles

import java.security.Provider

internal class TestProvider(
    name: String,
    info: String = "TestProvider:$name",
    services: List<Pair<String, String>> = emptyList(),
) : Provider(name, 1.0, info) {
    init {
        services.forEach { (type, algorithm) ->
            putService(testService(type = type, algorithm = algorithm))
        }
    }
}
