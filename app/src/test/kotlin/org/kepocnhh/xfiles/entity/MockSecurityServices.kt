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
