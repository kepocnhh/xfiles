package org.kepocnhh.xfiles.provider.security

import org.kepocnhh.xfiles.entity.MockPBEKeySpec
import java.security.spec.KeySpec
import javax.crypto.SecretKey
import javax.crypto.spec.PBEKeySpec

internal class MockSecretKeyFactoryProvider(
    private val values: Map<KeySpec, SecretKey> = emptyMap(),
) : SecretKeyFactoryProvider {
    override fun generate(params: KeySpec): SecretKey {
        val p = when (params) {
            is PBEKeySpec -> MockPBEKeySpec(params)
            else -> params
        }
        return values[p] ?: error("No key by $params!")
    }
}
