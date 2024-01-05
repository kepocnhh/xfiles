package org.kepocnhh.xfiles.provider.security

internal class MockMessageDigestProvider(
    private val digests: List<Pair<ByteArray, ByteArray>> = emptyList(),
) : MessageDigestProvider {
    override fun digest(bytes: ByteArray): ByteArray {
        for ((raw, dig) in digests) {
            if (bytes.contentEquals(raw)) return dig
        }
        error("No digest!")
    }
}
