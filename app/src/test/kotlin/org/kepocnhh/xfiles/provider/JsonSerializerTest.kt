package org.kepocnhh.xfiles.provider

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.kepocnhh.xfiles.entity.KeyMeta
import org.kepocnhh.xfiles.provider.security.Base64Provider
import org.kepocnhh.xfiles.provider.security.FinalBase64Provider
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
internal class JsonSerializerTest {
    @Test(timeout = 2_000)
    fun serializeKeyMetaTest() {
        val base64: Base64Provider = FinalBase64Provider
        val serializer: Serializer = JsonSerializer(base64 = base64)
        val value = KeyMeta(
            salt = "foo:salt".toByteArray(),
            ivDB = "bar:ivDB".toByteArray(),
            ivPrivate = "baz:ivPrivate".toByteArray(),
        )
        val bytes = serializer.serialize(value = value)
        val expected = """
            {
                "salt": "Zm9vOnNhbHQ=",
                "ivDB": "YmFyOml2REI=",
                "ivPrivate": "YmF6Oml2UHJpdmF0ZQ=="
            }
        """.trimIndent()
            .replace(" ", "")
            .replace("\n", "")
        val actual = String(bytes)
        assertEquals(expected, actual)
    }
}
