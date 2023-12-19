package org.kepocnhh.xfiles.provider

import org.json.JSONObject
import org.kepocnhh.xfiles.entity.KeyMeta
import org.kepocnhh.xfiles.util.base64

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
     *  "secrets": {
     *   "foo:id": {
     *    "title": "foo:title",
     *    "secret": "foo:secret"
     *   }
     *  }
     * }
     * ```
     */
    override fun toSecretTitles(bytes: ByteArray): Map<String, String> {
        val secrets = JSONObject(String(bytes))
            .getJSONObject("secrets")
        return secrets.keys().asSequence().associateWith { id ->
            secrets.getJSONObject(id).getString("title")
        }
    }

    override fun toSecretValues(bytes: ByteArray): Map<String, String> {
        val secrets = JSONObject(String(bytes))
            .getJSONObject("secrets")
        return secrets.keys().asSequence().associateWith { id ->
            secrets.getJSONObject(id).getString("secret")
        }
    }
}
