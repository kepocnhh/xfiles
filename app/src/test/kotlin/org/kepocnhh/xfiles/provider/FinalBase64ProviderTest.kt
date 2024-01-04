package org.kepocnhh.xfiles.provider

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.kepocnhh.xfiles.provider.security.Base64Provider
import org.kepocnhh.xfiles.provider.security.FinalBase64Provider
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
internal class FinalBase64ProviderTest {
    @Test(timeout = 2_000)
    fun encodeTest() {
        val issuer = "FinalBase64ProviderTest:encodeTest"
        val provider: Base64Provider = FinalBase64Provider
        listOf(
            "foobar" to "Zm9vYmFy",
            "345442" to "MzQ1NDQy",
            issuer to "RmluYWxCYXNlNjRQcm92aWRlclRlc3Q6ZW5jb2RlVGVzdA==",
        ).forEach { (decoded, encoded) ->
            assertEncode(
                provider = provider,
                bytes = decoded.toByteArray(),
                expected = encoded,
            )
        }
    }

    @Test(timeout = 2_000)
    fun decodeTest() {
        val issuer = "FinalBase64ProviderTest:decodeTest"
        val provider: Base64Provider = FinalBase64Provider
        listOf(
            "foo:decode:1" to "Zm9vOmRlY29kZTox",
            "bar:decode:2" to "YmFyOmRlY29kZToy",
            issuer to "RmluYWxCYXNlNjRQcm92aWRlclRlc3Q6ZGVjb2RlVGVzdA==",
        ).forEach { (decoded, encoded) ->
            assertDecode(
                provider = provider,
                encoded = encoded,
                expected = decoded.toByteArray(),
            )
        }
    }

    companion object {
        private fun assertEncode(
            provider: Base64Provider,
            bytes: ByteArray,
            expected: String,
        ) {
            val actual = provider.encode(bytes = bytes)
            Assert.assertEquals(expected, actual)
        }

        private fun assertDecode(
            provider: Base64Provider,
            encoded: String,
            expected: ByteArray,
        ) {
            val actual = provider.decode(encoded = encoded)
            val message = """
                ---
                expected: [${expected.copyOfRange(0, kotlin.math.min(8, expected.size)).joinToString()}...]
                actual: [${actual.copyOfRange(0, kotlin.math.min(8, actual.size)).joinToString()}...]
                ---
            """.trimIndent()
            Assert.assertTrue(message, expected.contentEquals(actual))
        }
    }
}
