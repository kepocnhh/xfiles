package org.kepocnhh.xfiles.entity

internal data class SecurityServices(
    val cipher: SecurityService,
    val symmetric: SecurityService,
    val asymmetric: SecurityService,
    val signature: SecurityService,
    val hash: SecurityService,
    val random: SecurityService,
)
