package org.kepocnhh.xfiles.entity

import org.bouncycastle.crypto.BlockCipher
import org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher
import java.security.Key
import java.security.SecureRandom
import java.security.spec.AlgorithmParameterSpec
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec

internal class MockBaseBlockCipher
@Suppress("LongParameterList")
constructor(
    private val values: List<DataSet> = emptyList(),
    private var iv: ByteArray? = null,
    engine: BlockCipher = MockBlockCipher(),
    scheme: Int = 2,
    digest: Int = 4,
    keySizeInBits: Int = 256,
    ivLength: Int = 16,
) : BaseBlockCipher(engine, scheme, digest, keySizeInBits, ivLength) {
    class DataSet(
        val encrypted: ByteArray,
        val decrypted: ByteArray,
        val key: Key,
        val iv: ByteArray,
    )

    private var opmode: Int? = null
    private var key: Key? = null

    override fun engineInit(opmode: Int, key: Key?, params: AlgorithmParameterSpec?, random: SecureRandom?) {
        check(opmode == Cipher.ENCRYPT_MODE || opmode == Cipher.DECRYPT_MODE)
        this.opmode = opmode
        this.key = key
        if (params is IvParameterSpec) {
            iv = params.iv
        }
    }

    @Suppress("CyclomaticComplexMethod")
    override fun engineDoFinal(input: ByteArray?, inputOffset: Int, inputLen: Int): ByteArray {
        val opmode = opmode ?: error("No operation mode!")
        val key = key ?: error("No key!")
        if (input == null) error("No input!")
        if (input.isEmpty()) error("No input!")
        when (opmode) {
            Cipher.DECRYPT_MODE -> {
                for (it in values) {
                    if (!it.key.encoded.contentEquals(key.encoded)) continue
                    if (!it.iv.contentEquals(engineGetIV())) continue
                    if (input.contentEquals(it.encrypted)) return it.decrypted
                }
                error("No decrypted!")
            }
            Cipher.ENCRYPT_MODE -> {
                for (it in values) {
                    if (!it.key.encoded.contentEquals(key.encoded)) continue
                    if (!it.iv.contentEquals(engineGetIV())) continue
                    if (input.contentEquals(it.decrypted)) return it.encrypted
                }
                error("No encrypted!")
            }
            else -> error("Operation mode \"$opmode\" is not supported!")
        }
    }

    override fun engineGetIV(): ByteArray {
        return iv ?: error("No IV!")
    }
}
