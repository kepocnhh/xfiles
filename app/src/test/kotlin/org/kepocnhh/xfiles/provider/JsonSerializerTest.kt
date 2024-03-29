package org.kepocnhh.xfiles.provider

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.kepocnhh.xfiles.entity.DataBase
import org.kepocnhh.xfiles.entity.mockAsymmetricKey
import org.kepocnhh.xfiles.entity.mockBiometricMeta
import org.kepocnhh.xfiles.entity.mockKeyMeta
import org.kepocnhh.xfiles.entity.mockUUID
import org.kepocnhh.xfiles.provider.security.Base64Provider
import org.kepocnhh.xfiles.provider.security.MockBase64Provider
import org.robolectric.RobolectricTestRunner
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@RunWith(RobolectricTestRunner::class)
internal class JsonSerializerTest {
    @Test(timeout = 2_000)
    fun serializeKeyMetaTest() {
        val issuer = "JsonSerializerTest:serializeKeyMetaTest"
        val value = mockKeyMeta(issuer = issuer)
        val base64: Base64Provider = MockBase64Provider(
            values = mapOf(
                "foo" to value.salt,
                "bar" to value.ivDB,
                "baz" to value.ivPrivate,
            ),
        )
        val serializer: Serializer = JsonSerializer(base64 = base64)
        val bytes = serializer.serialize(value = value)
        val expected = """
            {
                "salt": "foo",
                "ivDB": "bar",
                "ivPrivate": "baz"
            }
        """.trimIndent()
            .replace(" ", "")
            .replace("\n", "")
        val actual = String(bytes)
        assertEquals(expected, actual)
    }

    @Test(timeout = 2_000)
    fun toKeyMetaTest() {
        val issuer = "JsonSerializerTest:toKeyMetaTest"
        val salt = "$issuer:salt"
        val ivDB = "$issuer:ivDB"
        val ivPrivate = "$issuer:ivPrivate"
        val base64: Base64Provider = MockBase64Provider(
            decoded = setOf(
                salt,
                ivDB,
                ivPrivate,
            ),
        )
        val serializer: Serializer = JsonSerializer(base64 = base64)
        val json = """
            {
                "salt": "$salt:base64",
                "ivDB": "$ivDB:base64",
                "ivPrivate": "$ivPrivate:base64"
            }
        """.trimIndent()
        val actual = serializer.toKeyMeta(json.toByteArray())
        val expected = mockKeyMeta(
            salt = salt,
            ivDB = ivDB,
            ivPrivate = ivPrivate,
        )
        assertEquals(expected, actual)
    }

    @Test(timeout = 2_000)
    fun serializeDataBaseTest() {
        val serializer: Serializer = JsonSerializer(base64 = MockBase64Provider())
        val id = mockUUID()
        val secrets = (1..2).associate {
            mockUUID() to ("foo:$it" to "bar:$it")
        }
        val value = DataBase(
            id = id,
            updated = 42.seconds,
            secrets = secrets,
        )
        check(value.updated > Duration.ZERO)
        check(value.secrets.size > 1)
        val bytes = serializer.serialize(value = value)
        val expected = """
            {
             "id": "$id",
             "updated": ${42 * 1_000},
             "secrets": {
              "${secrets.keys.toList()[0]}": {
               "title": "foo:1",
               "secret": "bar:1"
              },
              "${secrets.keys.toList()[1]}": {
               "title": "foo:2",
               "secret": "bar:2"
              }
             }
            }
        """.trimIndent()
            .replace(" ", "")
            .replace("\n", "")
        assertEquals(expected, String(bytes))
    }

    @Test(timeout = 2_000)
    fun toDataBaseTest() {
        val serializer: Serializer = JsonSerializer(base64 = MockBase64Provider())
        val id = mockUUID()
        val secrets = (1..2).associate {
            mockUUID() to ("baz:$it" to "qux:$it")
        }
        val json = """
            {
             "id": "$id",
             "updated": ${128 * 1_000},
             "secrets": {
              "${secrets.keys.toList()[0]}": {
               "title": "baz:1",
               "secret": "qux:1"
              },
              "${secrets.keys.toList()[1]}": {
               "title": "baz:2",
               "secret": "qux:2"
              }
             }
            }
        """.trimIndent()
        val actual = serializer.toDataBase(json.toByteArray())
        val expected = DataBase(
            id = id,
            updated = 128.seconds,
            secrets = secrets,
        )
        assertEquals(expected, actual)
    }

    @Test(timeout = 2_000)
    fun serializeAsymmetricKeyTest() {
        val issuer = "JsonSerializerTest:serializeAsymmetricKeyTest"
        val value = mockAsymmetricKey(issuer = issuer)
        val base64: Base64Provider = MockBase64Provider(
            values = mapOf(
                "foo" to value.publicKeyDecrypted,
                "bar" to value.privateKeyEncrypted,
            ),
        )
        val serializer: Serializer = JsonSerializer(base64 = base64)
        val bytes = serializer.serialize(value = value)
        val expected = """
            {
            "publicKeyDecrypted":"foo",
            "privateKeyEncrypted":"bar"
            }
        """.trimIndent()
            .replace(" ", "")
            .replace("\n", "")
        assertEquals(expected, String(bytes))
    }

    @Test(timeout = 2_000)
    fun toAsymmetricKeyTest() {
        val issuer = "JsonSerializerTest:toAsymmetricKeyTest"
        val expected = mockAsymmetricKey(issuer = issuer)
        val base64: Base64Provider = MockBase64Provider(
            values = mapOf(
                "foo" to expected.publicKeyDecrypted,
                "bar" to expected.privateKeyEncrypted,
            ),
        )
        val serializer: Serializer = JsonSerializer(base64 = base64)
        val json = """
            {
            "publicKeyDecrypted":"foo",
            "privateKeyEncrypted":"bar"
            }
        """.trimIndent()
        val actual = serializer.toAsymmetricKey(json.toByteArray())
        assertEquals(expected, actual)
    }

    @Test(timeout = 2_000)
    fun serializeBiometricMetaTest() {
        val issuer = "JsonSerializerTest:serializeBiometricMetaTest"
        val value = mockBiometricMeta(issuer = issuer)
        val base64: Base64Provider = MockBase64Provider(
            values = mapOf(
                "foo" to value.passwordEncrypted,
                "bar" to value.iv,
            ),
        )
        val serializer: Serializer = JsonSerializer(base64 = base64)
        val bytes = serializer.serialize(value = value)
        val expected = """
            {
            "passwordEncrypted":"foo",
            "iv":"bar"
            }
        """.trimIndent()
            .replace(" ", "")
            .replace("\n", "")
        assertEquals(expected, String(bytes))
    }

    @Test(timeout = 2_000)
    fun toBiometricMetaTest() {
        val issuer = "JsonSerializerTest:toBiometricMetaTest"
        val expected = mockBiometricMeta(issuer = issuer)
        val base64: Base64Provider = MockBase64Provider(
            values = mapOf(
                "foo" to expected.passwordEncrypted,
                "bar" to expected.iv,
            ),
        )
        val serializer: Serializer = JsonSerializer(base64 = base64)
        val json = """
            {
            "passwordEncrypted":"foo",
            "iv":"bar"
            }
        """.trimIndent()
        val actual = serializer.toBiometricMeta(json.toByteArray())
        assertEquals(expected, actual)
    }
}
