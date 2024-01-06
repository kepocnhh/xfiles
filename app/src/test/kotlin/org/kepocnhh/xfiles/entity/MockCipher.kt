package org.kepocnhh.xfiles.entity

import org.kepocnhh.xfiles.TestProvider
import java.security.Provider
import javax.crypto.Cipher
import javax.crypto.CipherSpi

internal class MockCipher(
    cipherSpi: CipherSpi = MockBaseBlockCipher(),
    provider: Provider = TestProvider("MockCipher:provider"),
    transformation: String = "MockCipher:transformation",
) : Cipher(
    cipherSpi,
    provider,
    transformation,
)
