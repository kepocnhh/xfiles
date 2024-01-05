package org.kepocnhh.xfiles.provider.security

import java.security.spec.KeySpec
import javax.crypto.SecretKey

internal interface SecretKeyFactoryProvider {
    fun generate(params: KeySpec): SecretKey
}
