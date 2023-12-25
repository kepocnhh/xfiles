package org.kepocnhh.xfiles.provider.security

internal class MockBase64Provider(
    private val values: Map<String, ByteArray> = emptyMap(),
) : Base64Provider {
    override fun encode(bytes: ByteArray): String {
        for ((encoded, decoded) in values) {
            if (bytes.contentEquals(decoded)) return encoded
        }
        error("No encoded!")
    }
}
