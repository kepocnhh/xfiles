package org.kepocnhh.xfiles.entity

import org.bouncycastle.crypto.BlockCipher
import org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher
import java.security.Key
import java.security.SecureRandom
import java.security.spec.AlgorithmParameterSpec
import javax.crypto.Cipher

internal class MockBaseBlockCipher(
    private val values: List<Pair<ByteArray, ByteArray>> = emptyList(),
    engine: BlockCipher = MockBlockCipher(),
    scheme: Int = 2,
    digest: Int = 4,
    keySizeInBits: Int = 256,
    ivLength: Int = 16,
) : BaseBlockCipher(engine, scheme, digest, keySizeInBits, ivLength) {
    private var opmode: Int? = null
    private var key: Key? = null

    override fun engineInit(opmode: Int, key: Key?, params: AlgorithmParameterSpec?, random: SecureRandom?) {
        check(opmode == Cipher.ENCRYPT_MODE || opmode == Cipher.DECRYPT_MODE)
        this.opmode = opmode
        this.key = key
    }

    override fun engineDoFinal(input: ByteArray?, inputOffset: Int, inputLen: Int): ByteArray {
        val opmode = opmode ?: error("No operation mode!")
        if (key == null) error("No key!")
        if (input == null) error("No input!")
        if (input.isEmpty()) error("No input!")
        when (opmode) {
            Cipher.DECRYPT_MODE -> {
                for ((encrypted, decrypted) in values) {
                    if (input.contentEquals(encrypted)) return decrypted
                }
                error("No decrypted!")
            }
            Cipher.ENCRYPT_MODE -> TODO("ENCRYPT_MODE")
            else -> error("Operation mode $opmode!")
        }
    }
}
