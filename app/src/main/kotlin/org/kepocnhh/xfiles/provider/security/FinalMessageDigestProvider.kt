package org.kepocnhh.xfiles.provider.security

import java.security.MessageDigest

internal class FinalMessageDigestProvider(
    private val delegate: MessageDigest,
) : MessageDigestProvider {
    override fun digest(bytes: ByteArray): ByteArray {
        return delegate.digest(bytes)
    }
}
