package org.kepocnhh.xfiles.entity

@Suppress("LongParameterList")
internal fun mockSecurityServices(
    cipher: SecurityService = mockSecurityService(algorithm = "mock:cipher"),
    symmetric: SecurityService = mockSecurityService(algorithm = "mock:symmetric"),
    asymmetric: SecurityService = mockSecurityService(algorithm = "mock:asymmetric"),
    signature: SecurityService = mockSecurityService(algorithm = "mock:signature"),
    sha512: SecurityService = mockSecurityService(algorithm = "mock:sha512"),
    md5: SecurityService = mockSecurityService(algorithm = "mock:md5"),
    random: SecurityService = mockSecurityService(algorithm = "mock:random"),
): SecurityServices {
    return SecurityServices(
        cipher = cipher,
        symmetric = symmetric,
        asymmetric = asymmetric,
        signature = signature,
        sha512 = sha512,
        md5 = md5,
        random = random,
    )
}

internal fun mockSecurityServices(issuer: String): SecurityServices {
    return mockSecurityServices(
        cipher = mockSecurityService(issuer = "$issuer:cipher"),
        symmetric = mockSecurityService(issuer = "$issuer:symmetric"),
        asymmetric = mockSecurityService(issuer = "$issuer:asymmetric"),
        signature = mockSecurityService(issuer = "$issuer:signature"),
        sha512 = mockSecurityService(issuer = "$issuer:sha512"),
        md5 = mockSecurityService(issuer = "$issuer:md5"),
        random = mockSecurityService(issuer = "$issuer:random"),
    )
}
