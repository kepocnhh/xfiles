package org.kepocnhh.xfiles.provider

import org.json.JSONObject
import org.kepocnhh.xfiles.entity.AsymmetricKey
import org.kepocnhh.xfiles.entity.DataBase
import org.kepocnhh.xfiles.entity.KeyMeta
import org.kepocnhh.xfiles.util.base64
import kotlin.time.Duration.Companion.milliseconds

internal object JsonSerializer : Serializer {
    override fun serialize(value: KeyMeta): ByteArray {
        return JSONObject()
            .put("salt", value.salt.base64())
            .put("ivDB", value.ivDB.base64())
            .put("ivPrivate", value.ivPrivate.base64())
            .toString()
            .toByteArray()
    }

    override fun toKeyMeta(bytes: ByteArray): KeyMeta {
        val json = JSONObject(String(bytes))
        return KeyMeta(
            salt = json.getString("salt").base64(),
            ivDB = json.getString("ivDB").base64(),
            ivPrivate = json.getString("ivPrivate").base64(),
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
        val updated = json.let {
            if (!it.has("updated")) error("No updated time!")
            it.getLong("updated").milliseconds
        }
        return DataBase(
            id = json.getString("id") ?: error("No id!"),
            updated = updated,
            secrets = secrets.keys().asSequence().associateWith { id ->
                val data = secrets.getJSONObject(id)
                val title = data.getString("title")
                val secret = data.getString("secret")
                title to secret
            },
        )
    }

    override fun serialize(value: DataBase): ByteArray {
        val secrets = JSONObject().also { json ->
            value.secrets.forEach { (id, pair) ->
                val (title, secret) = pair
                val data = JSONObject()
                    .put("title", title)
                    .put("secret", secret)
                json.put(id, data)
            }
        }
        return JSONObject()
            .put("id", value.id)
            .put("updated", value.updated.inWholeMilliseconds)
            .put("secrets", secrets)
            .toString()
            .toByteArray()
    }

    override fun serialize(value: AsymmetricKey): ByteArray {
        return JSONObject()
            .put("public", value.publicDecrypted.base64())
            .put("private", value.privateEncrypted.base64())
            .toString()
            .toByteArray()
    }

    override fun toAsymmetricKey(bytes: ByteArray): AsymmetricKey {
        val json = JSONObject(String(bytes))
        return AsymmetricKey(
            publicDecrypted = json.getString("public").base64(),
            privateEncrypted = json.getString("private").base64(),
        )
    }
}
