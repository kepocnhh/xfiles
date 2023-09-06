package org.kepocnhh.xfiles.entity

internal data class SecuritySettings(
    val aes: AES,
    val des: DES,
) {
    data class AES(val iterations: Iterations) {
        enum class Iterations {
            NUMBER_2_10,
            NUMBER_2_16,
            NUMBER_2_20,
        }
    }

    data class DES(val strength: Strength) {
        enum class Strength {
            NUMBER_1024_1,
            NUMBER_1024_2,
            NUMBER_1024_3,
        }
    }
}
