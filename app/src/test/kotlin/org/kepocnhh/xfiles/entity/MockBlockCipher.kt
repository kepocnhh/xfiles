package org.kepocnhh.xfiles.entity

import org.bouncycastle.crypto.BlockCipher
import org.bouncycastle.crypto.CipherParameters

internal class MockBlockCipher(
    private val algorithmName: String = "MockBlockCipher:algorithmName",
    private val blockSize: Int = 16,
) : BlockCipher {
    override fun init(forEncryption: Boolean, params: CipherParameters?) {
        TODO("Not yet implemented: init")
    }

    override fun getAlgorithmName(): String {
        return algorithmName
    }

    override fun getBlockSize(): Int {
        return blockSize
    }

    override fun processBlock(input: ByteArray?, inOff: Int, output: ByteArray?, outOff: Int): Int {
        TODO("Not yet implemented: processBlock")
    }

    override fun reset() {
        // noop
    }
}
