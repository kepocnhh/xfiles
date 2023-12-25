package org.kepocnhh.xfiles.entity

internal data class SecurityServices(
    val cipher: SecurityService,
    val symmetric: SecurityService,
    val asymmetric: SecurityService,
    val signature: SecurityService,
    // todo md5
    @Deprecated(message = "replace with sha512")
    val hash: SecurityService,
    val random: SecurityService,
)
