package org.kepocnhh.xfiles.entity

internal fun mockSecurityService(
    provider: String = "mock:security:service:provider",
    algorithm: String = "mock:security:service:algorithm",
): SecurityService {
    return SecurityService(
        provider = provider,
        algorithm = algorithm,
    )
}

internal fun mockSecurityService(issuer: String): SecurityService {
    return mockSecurityService(
        provider = "$issuer:mock:security:service:provider",
        algorithm = "$issuer:mock:security:service:algorithm",
    )
}
