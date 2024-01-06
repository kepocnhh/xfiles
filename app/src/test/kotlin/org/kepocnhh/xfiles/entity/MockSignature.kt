package org.kepocnhh.xfiles.entity

import java.security.PrivateKey
import java.security.PublicKey
import java.security.Signature

internal class MockSignature(
    algorithm: String = "MockSignature:algorithm",
    private val values: List<DataSet>,
) : Signature(algorithm) {
    class DataSet(
        val sig: ByteArray,
        val decrypted: ByteArray,
        val privateKey: PrivateKey = MockPrivateKey(issuer = "MockSignature:DataSet:private:key"),
        val publicKey: PublicKey = MockPublicKey(issuer = "MockSignature:DataSet:public:key"),
    )

    private var privateKey: PrivateKey? = null
    private var publicKey: PublicKey? = null
    private var decrypted: ByteArray? = null

    override fun engineInitVerify(publicKey: PublicKey?) {
        this.publicKey = publicKey
    }

    override fun engineInitSign(privateKey: PrivateKey?) {
        this.privateKey = privateKey
    }

    override fun engineUpdate(b: Byte) {
        TODO("Not yet implemented: engineUpdate")
    }

    override fun engineUpdate(b: ByteArray?, off: Int, len: Int) {
        if (b == null) error("No bytes!")
        decrypted = b
    }

    override fun engineSign(): ByteArray {
        val decrypted = decrypted ?: error("No decrypted!")
        if (decrypted.isEmpty()) error("Empty decrypted!")
        val privateKey = privateKey ?: error("No private key!")
        if (privateKey.encoded.isEmpty()) error("Empty private key!")
        for (it in values) {
            if (!it.privateKey.encoded.contentEquals(privateKey.encoded)) continue
            if (it.decrypted.contentEquals(decrypted)) return it.sig
        }
        error("No sign!")
    }

    override fun engineVerify(sigBytes: ByteArray?): Boolean {
        if (sigBytes == null) error("No sig bytes!")
        if (sigBytes.isEmpty()) error("Empty sig bytes!")
        val decrypted = decrypted ?: error("No decrypted!")
        if (decrypted.isEmpty()) error("Empty decrypted!")
        val publicKey = publicKey ?: error("No public key!")
        if (publicKey.encoded.isEmpty()) error("Empty public key!")
        val filtered = values
            .filter { it.publicKey.encoded.contentEquals(publicKey.encoded) }
            .also {
                if (it.isEmpty()) error("Can not find public key!")
            }
            .filter { it.decrypted.contentEquals(decrypted) }
            .also {
                if (it.isEmpty()) error("Can not find decrypted!")
            }
        if (filtered.size != 1) error("Filtered ${filtered.size}!")
        val dataSet = filtered.single()
        return dataSet.sig.contentEquals(sigBytes)
    }

    override fun engineSetParameter(param: String?, value: Any?) {
        TODO("Not yet implemented: engineSetParameter")
    }

    override fun engineGetParameter(param: String?): Any {
        TODO("Not yet implemented: engineGetParameter")
    }
}
