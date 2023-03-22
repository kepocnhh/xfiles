object Version {
    const val detekt = "1.22.0"
    const val jacoco = "0.8.8"
    const val jvmTarget = "11"
    const val kotlin = "1.7.10"
    const val ktlint = "0.48.2"

    object Android {
        const val compileSdk = 33
        const val minSdk = 24
        const val targetSdk = compileSdk
        const val compose = "1.3.1"
    }

    object Application {
        const val code = 1
        const val name = "0.0.$code"
    }
}
