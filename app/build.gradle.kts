import com.android.build.api.variant.ComponentIdentity
import sp.gx.core.GitHub
import sp.gx.core.camelCase
import sp.gx.core.existing
import sp.gx.core.file
import sp.gx.core.filled
import sp.gx.core.kebabCase

val gh = GitHub.Repository(
    owner = "kepocnhh",
    name = rootProject.name,
)

repositories {
    google()
    mavenCentral()
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots")
}

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("org.gradle.jacoco")
    id("io.gitlab.arturbosch.detekt") version Version.detekt
}

fun ComponentIdentity.getVersion(): String {
    check(flavorName!!.isEmpty())
    return when (buildType) {
        "debug", "examine" -> kebabCase(
            android.defaultConfig.versionName!!,
            name,
            android.defaultConfig.versionCode!!.toString(),
        )
        "release" -> kebabCase(
            android.defaultConfig.versionName!!,
            android.defaultConfig.versionCode!!.toString(),
        )
        else -> error("Build type \"${buildType}\" is not supported!")
    }
}

android {
    namespace = "org.kepocnhh.xfiles"
    compileSdk = Version.Android.compileSdk

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            all {
                // https://stackoverflow.com/a/71834475/4398606
                it.configure<JacocoTaskExtension> {
                    isIncludeNoLocationClasses = true
                    excludes = listOf("jdk.internal.*")
                }
            }
        }
    }

    defaultConfig {
        applicationId = namespace
        minSdk = Version.Android.minSdk
        targetSdk = Version.Android.targetSdk
        versionName = "0.6.0"
        versionCode = 28
        manifestPlaceholders["appName"] = "@string/app_name"
    }

    buildTypes {
        getByName("debug") {
            applicationIdSuffix = ".$name"
            versionNameSuffix = "-$name"
            isMinifyEnabled = false
            isShrinkResources = false
            manifestPlaceholders["buildType"] = name
        }
        getByName("release") {
            applicationIdSuffix = ""
            versionNameSuffix = ""
            manifestPlaceholders["buildType"] = name
            enableUnitTestCoverage = false
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
            signingConfig = signingConfigs.create(name) {
                storeFile = properties["STORE_FILE"]?.toString()?.let(::File)
                storePassword = properties["STORE_PASSWORD"]?.toString()
                keyPassword = storePassword
                keyAlias = name
            }
        }
        create("examine") {
            val parent = getByName("release")
            initWith(parent)
            sourceSets.getByName(name) {
                res.srcDir("src/${parent.name}/res")
                kotlin.srcDir("src/${parent.name}/kotlin")
            }
            applicationIdSuffix = ".$name"
            enableUnitTestCoverage = true
            testBuildType = name
            signingConfig = getByName("debug").signingConfig
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions.kotlinCompilerExtensionVersion = Version.Android.compose
}

jacoco.toolVersion = Version.jacoco

val ktlint: Configuration by configurations.creating

androidComponents.onVariants { variant ->
    val output = variant.outputs.single()
    check(output is com.android.build.api.variant.impl.VariantOutputImpl)
    val name = kebabCase(
        rootProject.name,
        android.defaultConfig.versionName!!,
        variant.name,
        android.defaultConfig.versionCode.toString(),
    )
    output.outputFileName.set("$name.apk")
    afterEvaluate {
        tasks.getByName<JavaCompile>(camelCase("compile", variant.name, "JavaWithJavac")) {
            targetCompatibility = Version.jvmTarget
        }
        tasks.getByName<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>(camelCase("compile", variant.name, "Kotlin")) {
            kotlinOptions.jvmTarget = Version.jvmTarget
        }
        tasks.getByName<JavaCompile>(camelCase("compile", variant.name, "UnitTest", "JavaWithJavac")) {
            targetCompatibility = Version.jvmTarget
        }
        tasks.getByName<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>(camelCase("compile", variant.name, "UnitTest", "Kotlin")) {
            kotlinOptions.jvmTarget = Version.jvmTarget
        }
        val checkManifestTask = task(camelCase("checkManifest", variant.name)) {
            dependsOn(camelCase("compile", variant.name, "Sources"))
            doLast {
                val file = "intermediates/merged_manifest/${variant.name}/AndroidManifest.xml"
                val manifest = groovy.xml.XmlParser().parse(layout.buildDirectory.file(file).get().asFile)
                val actual = manifest.getAt(groovy.namespace.QName("uses-permission")).map {
                    check(it is groovy.util.Node)
                    val attributes = it.attributes().mapKeys { (k, _) -> k.toString() }
                    val name = attributes["{http://schemas.android.com/apk/res/android}name"]
                    check(name is String && name.isNotEmpty())
                    name
                }
                val applicationId by variant.applicationId
                val expected = setOf(
//                    "android.permission.FOREGROUND_SERVICE",
//                    "android.permission.POST_NOTIFICATIONS",
                    "android.permission.USE_BIOMETRIC",
                    "android.permission.USE_FINGERPRINT",
                    "android.permission.VIBRATE",
                    "$applicationId.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION",
                )
                check(actual.sorted() == expected.sorted()) {
                    "Actual is:\n$actual\nbut expected is:\n$expected"
                }
            }
        }
        tasks.getByName(camelCase("assemble", variant.name)) {
            dependsOn(checkManifestTask)
        }
    }
}

dependencies {
    implementation("androidx.activity:activity-compose:1.8.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.biometric:biometric:1.1.0")
    implementation("androidx.compose.foundation:foundation:${Version.Android.compose}")
    implementation("androidx.security:security-crypto:1.0.0")
    implementation("com.github.kepocnhh:ComposeAnimations:0.0.2-SNAPSHOT")
    implementation("com.github.kepocnhh:ComposeClicks:0.2.3-SNAPSHOT")
    implementation("com.github.kepocnhh:ComposeDialogs:0.1.0-SNAPSHOT")
    debugImplementation("androidx.compose.ui:ui-tooling:${Version.Android.compose}")
    debugImplementation("androidx.compose.ui:ui-tooling-preview:${Version.Android.compose}")
    testImplementation("androidx.compose.ui:ui-test-junit4:${Version.Android.compose}")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("org.robolectric:robolectric:4.11.1")
    ktlint("com.pinterest:ktlint:${Version.ktlint}") {
        attributes {
            attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.EXTERNAL))
        }
    }
}
