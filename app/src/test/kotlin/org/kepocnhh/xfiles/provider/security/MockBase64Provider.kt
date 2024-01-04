package org.kepocnhh.xfiles.provider.security

internal class MockBase64Provider(
    private val values: Map<String, ByteArray> = emptyMap(),
) : Base64Provider {
    constructor(decoded: Set<String>) : this(
        values = decoded.associate { "$it:base64" to it.toByteArray() },
    )

    override fun encode(bytes: ByteArray): String {
        for ((encoded, decoded) in values) {
            if (bytes.contentEquals(decoded)) return encoded
        }
        error("No encoded!")
    }

    override fun decode(encoded: String): ByteArray {
        for ((e, decoded) in values) {
            if (encoded.contentEquals(e)) return decoded
        }
        error("No decoded by \"$encoded\"!")
    }
}
