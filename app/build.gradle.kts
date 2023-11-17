import sp.gx.core.camelCase
import sp.gx.core.existing
import sp.gx.core.file
import sp.gx.core.filled
import sp.gx.core.kebabCase

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

android {
    namespace = "org.kepocnhh.xfiles"
    compileSdk = Version.Android.compileSdk

    defaultConfig {
        applicationId = namespace
        minSdk = Version.Android.minSdk
        targetSdk = Version.Android.targetSdk
        versionName = "0.4.1"
        versionCode = 23
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
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions.kotlinCompilerExtensionVersion = Version.Android.compose
}

jacoco.toolVersion = Version.jacoco

fun setCoverage(variant: com.android.build.api.variant.ComponentIdentity) {
    val taskUnitTest = tasks.getByName<Test>(camelCase("test", variant.name, "UnitTest"))
    val taskCoverageReport = task<JacocoReport>(camelCase("test", variant.name, "CoverageReport")) {
        dependsOn(taskUnitTest)
        reports {
            csv.required.set(false)
            html.required.set(true)
            xml.required.set(false)
        }
        sourceDirectories.setFrom(file("src/main/kotlin"))
        val dirs = fileTree(layout.buildDirectory.file("tmp/kotlin-classes/" + variant.name)) {
            include("**/${android.defaultConfig.applicationId!!.replace('.', '/')}/module/**/*")
        }
        classDirectories.setFrom(dirs)
        executionData(taskUnitTest)
        doLast {
            val report = layout.buildDirectory.file("reports/jacoco/$name/html/index.html").get().asFile
            if (report.exists()) {
                println("Coverage report: ${report.absolutePath}")
            }
        }
    }
    task<JacocoCoverageVerification>(camelCase("test", variant.name, "CoverageVerification")) {
        dependsOn(taskCoverageReport)
        violationRules {
            rule {
                limit {
                    minimum = BigDecimal(0.96)
                }
            }
        }
        classDirectories.setFrom(taskCoverageReport.classDirectories)
        executionData(taskCoverageReport.executionData)
    }
}

fun setCodeQuality(variant: com.android.build.api.variant.ComponentIdentity) {
    val configs = setOf(
        "comments",
        "common",
        "complexity",
        "coroutines",
        "empty-blocks",
        "exceptions",
        "naming",
        "performance",
        "potential-bugs",
        "style",
    ).map { config ->
        rootDir.resolve("buildSrc/src/main/resources/detekt/config/$config.yml")
            .existing()
            .file()
            .filled()
    }
    setOf("main", "test").forEach { source ->
        task<io.gitlab.arturbosch.detekt.Detekt>(camelCase("checkCodeQuality", variant.name, source)) {
            jvmTarget = Version.jvmTarget
            setSource(files("src/$source/kotlin"))
            config.setFrom(configs)
            reports {
                html {
                    required.set(true)
                    outputLocation.set(layout.buildDirectory.file("reports/analysis/code/quality/${variant.name}/$source/html/index.html"))
                }
                md.required.set(false)
                sarif.required.set(false)
                txt.required.set(false)
                xml.required.set(false)
            }
            val postfix = when (source) {
                "main" -> ""
                "test" -> "UnitTest"
                else -> error("Source \"$source\" is not supported!")
            }
            val detektTask = tasks.getByName<io.gitlab.arturbosch.detekt.Detekt>(camelCase("detekt", variant.name, postfix))
            classpath.setFrom(detektTask.classpath)
        }
    }
}

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
        setCoverage(variant)
        setCodeQuality(variant)
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
                    "android.permission.FOREGROUND_SERVICE",
                    "android.permission.POST_NOTIFICATIONS",
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
    implementation("androidx.activity:activity-compose:1.6.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.biometric:biometric:1.1.0")
    implementation("androidx.compose.foundation:foundation:${Version.Android.compose}")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.5.1")
    implementation("androidx.security:security-crypto:1.0.0")
    implementation("com.github.kepocnhh:ComposeAnimations:0.0.2-SNAPSHOT")
    implementation("com.github.kepocnhh:ComposeClicks:0.2.2-SNAPSHOT")
    implementation("com.github.kepocnhh:ComposeDialogs:0.1.0-SNAPSHOT")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
}
