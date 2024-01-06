package org.kepocnhh.xfiles.entity

import java.security.PrivateKey
import java.security.PublicKey
import java.security.Signature

internal class MockSignature(
    algorithm: String = "MockSignature:algorithm",
    private val values: List<DataSet>,
) : Signature(algorithm) {
    class DataSet(
        val sign: ByteArray,
        val decrypted: ByteArray,
        val privateKey: PrivateKey,
    )

    private var privateKey: PrivateKey? = null
    private var decrypted: ByteArray? = null

    override fun engineInitVerify(publicKey: PublicKey?) {
        TODO("Not yet implemented: engineInitVerify")
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
            if (it.decrypted.contentEquals(decrypted)) return it.sign
        }
        error("No sign!")
    }

    override fun engineVerify(sigBytes: ByteArray?): Boolean {
        TODO("Not yet implemented: engineVerify")
    }

    override fun engineSetParameter(param: String?, value: Any?) {
        TODO("Not yet implemented: engineSetParameter")
    }

    override fun engineGetParameter(param: String?): Any {
        TODO("Not yet implemented: engineGetParameter")
    }
}
