@file:Suppress("ForbiddenImport")

package org.kepocnhh.xfiles.provider

import org.json.JSONObject
import org.kepocnhh.xfiles.entity.AsymmetricKey
import org.kepocnhh.xfiles.entity.BiometricMeta
import org.kepocnhh.xfiles.entity.DataBase
import org.kepocnhh.xfiles.entity.KeyMeta
import org.kepocnhh.xfiles.provider.security.Base64Provider
import java.util.UUID
import kotlin.time.Duration.Companion.milliseconds

internal class JsonSerializer(
    private val base64: Base64Provider,
) : Serializer {
    private fun JSONObject.requireLong(key: String): Long {
        if (!has(key)) error("No value by \"$key\"!")
        if (isNull(key)) error("Value by \"$key\" is null!")
        return getLong(key)
    }

    private fun JSONObject.requireString(key: String): String {
        if (!has(key)) error("No value by \"$key\"!")
        if (isNull(key)) error("Value by \"$key\" is null!")
        return getString(key)
    }

    override fun serialize(value: KeyMeta): ByteArray {
        return JSONObject()
            .put("salt", base64.encode(value.salt))
            .put("ivDB", base64.encode(value.ivDB))
            .put("ivPrivate", base64.encode(value.ivPrivate))
            .toString()
            .toByteArray()
    }

    override fun toKeyMeta(bytes: ByteArray): KeyMeta {
        val json = JSONObject(String(bytes))
        return KeyMeta(
            salt = base64.decode(json.getString("salt")),
            ivDB = base64.decode(json.getString("ivDB")),
            ivPrivate = base64.decode(json.getString("ivPrivate")),
        )
    }

    /**
     * ```
     * {
     *  "id": "foo:db:id",
     *  "updated: 42,
     *  "secrets": {
     *   "foo:id": {
     *    "title": "foo:title",
     *    "secret": "foo:secret"
     *   }
     *  }
     * }
     * ```
     */
    override fun toDataBase(bytes: ByteArray): DataBase {
        val json = JSONObject(String(bytes))
        val secrets = json.getJSONObject("secrets")
        val updated = json.requireLong("updated").milliseconds
        return DataBase(
            id = json.requireString("id").let(UUID::fromString),
            updated = updated,
            secrets = secrets.keys().asSequence().map { id ->
                val data = secrets.getJSONObject(id)
                val title = data.getString("title")
                val secret = data.getString("secret")
                UUID.fromString(id) to (title to secret)
            }.toMap(),
        )
    }

    override fun serialize(value: DataBase): ByteArray {
        val secrets = JSONObject().also { json ->
            value.secrets.forEach { (uuid, pair) ->
                val (title, secret) = pair
                val data = JSONObject()
                    .put("title", title)
                    .put("secret", secret)
                json.put(uuid.toString(), data)
            }
        }
        return JSONObject()
            .put("id", value.id.toString())
            .put("updated", value.updated.inWholeMilliseconds)
            .put("secrets", secrets)
            .toString()
            .toByteArray()
    }

    override fun serialize(value: AsymmetricKey): ByteArray {
        return JSONObject()
            .put("publicKeyDecrypted", base64.encode(value.publicKeyDecrypted))
            .put("privateKeyEncrypted", base64.encode(value.privateKeyEncrypted))
            .toString()
            .toByteArray()
    }

    override fun toAsymmetricKey(bytes: ByteArray): AsymmetricKey {
        val json = JSONObject(String(bytes))
        return AsymmetricKey(
            publicKeyDecrypted = base64.decode(json.getString("publicKeyDecrypted")),
            privateKeyEncrypted = base64.decode(json.getString("privateKeyEncrypted")),
        )
    }

    override fun serialize(value: BiometricMeta): ByteArray {
        return JSONObject()
            .put("passwordEncrypted", base64.encode(value.passwordEncrypted))
            .put("iv", base64.encode(value.iv))
            .toString()
            .toByteArray()
    }

    override fun toBiometricMeta(bytes: ByteArray): BiometricMeta {
        val json = JSONObject(String(bytes))
        return BiometricMeta(
            passwordEncrypted = base64.decode(json.getString("passwordEncrypted")),
            iv = base64.decode(json.getString("iv")),
        )
    }
}
