package org.kepocnhh.xfiles.entity

internal data class SecurityServices(
    val cipher: SecurityService,
    val symmetric: SecurityService,
    val asymmetric: SecurityService,
    val signature: SecurityService,
    val md5: SecurityService,
    val sha512: SecurityService,
    val random: SecurityService,
)
