package org.kepocnhh.xfiles.entity

internal fun mockSecurityServices(
    cipher: SecurityService = mockSecurityService(algorithm = "cipher"),
    symmetric: SecurityService = mockSecurityService(algorithm = "symmetric"),
    asymmetric: SecurityService = mockSecurityService(algorithm = "asymmetric"),
    signature: SecurityService = mockSecurityService(algorithm = "signature"),
    hash: SecurityService = mockSecurityService(algorithm = "hash"),
    random: SecurityService = mockSecurityService(algorithm = "random"),
): SecurityServices {
    return SecurityServices(
        cipher = cipher,
        symmetric = symmetric,
        asymmetric = asymmetric,
        signature = signature,
        hash = hash,
        random = random,
    )
}

internal fun mockSecurityServices(issuer: String): SecurityServices {
    return mockSecurityServices(
        cipher = mockSecurityService(issuer = issuer),
        symmetric = mockSecurityService(issuer = issuer),
        asymmetric = mockSecurityService(issuer = issuer),
        signature = mockSecurityService(issuer = issuer),
        hash = mockSecurityService(issuer = issuer),
        random = mockSecurityService(issuer = issuer),
    )
}
