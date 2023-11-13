package org.kepocnhh.xfiles.entity

internal data class SecuritySettings(
    val aesKeyLength: AESKeyLength,
    val pbeIterations: PBEIterations,
    val dsaKeyLength: DSAKeyLength,
    val hasBiometric: Boolean,
) {
    enum class AESKeyLength {
        BITS_256,
    }

    enum class PBEIterations {
        NUMBER_2_10,
        NUMBER_2_16,
        NUMBER_2_20,
    }

    /**
     * L = 1024, N = 160
     * L = 2048, N = 224
     * L = 2048, N = 256
     * L = 3072, N = 256
    */
    enum class DSAKeyLength {
        BITS_1024_1,
        BITS_1024_2,
        BITS_1024_3,
    }
}
