package org.kepocnhh.xfiles.entity

internal fun mockSecurityService(
    provider: String = "foo",
    algorithm: String = "bar",
): SecurityService {
    return SecurityService(
        provider = provider,
        algorithm = algorithm,
    )
}
