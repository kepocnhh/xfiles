repositories {
    google()
    mavenCentral()
}

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("org.gradle.jacoco")
    id("io.gitlab.arturbosch.detekt") version Version.detekt
}

val appId = "org.kepocnhh.xfiles"

android {
    namespace = appId
    compileSdk = Version.Android.compileSdk

    defaultConfig {
        applicationId = appId
        minSdk = Version.Android.minSdk
        targetSdk = Version.Android.targetSdk
        versionName = Version.Application.name
        versionCode = Version.Application.code
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

    buildFeatures.compose = true

    composeOptions.kotlinCompilerExtensionVersion = Version.Android.compose
}

jacoco {
    toolVersion = Version.jacoco
}

fun setCoverage(variant: com.android.build.api.variant.ComponentIdentity) {
    val capitalize = variant.name.capitalize()
    val taskUnitTest = tasks.getByName<Test>("test${capitalize}UnitTest")
    val taskCoverageReport = task<JacocoReport>("test${capitalize}CoverageReport") {
        dependsOn(taskUnitTest)
        reports {
            csv.required.set(false)
            html.required.set(true)
            xml.required.set(false)
        }
        sourceDirectories.setFrom(file("src/main/kotlin"))
        val dirs = fileTree(File(buildDir, "tmp/kotlin-classes/" + variant.name)) {
            include("**/${appId.replace('.', '/')}/implementation/module/**/*")
        }
        classDirectories.setFrom(dirs)
        executionData(taskUnitTest)
    }
    task<JacocoCoverageVerification>("test${capitalize}CoverageVerification") {
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
    val capitalize = variant.name.capitalize()
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
        File(rootDir, "buildSrc/src/main/resources/detekt/config/$config.yml").also {
            check(it.exists() && !it.isDirectory)
        }
    }
    setOf("main", "test").forEach { source ->
        task<io.gitlab.arturbosch.detekt.Detekt>("checkCodeQuality$capitalize${source.capitalize()}") {
            jvmTarget = Version.jvmTarget
            setSource(files("src/$source/kotlin"))
            config.setFrom(configs)
            reports {
                html {
                    required.set(true)
                    outputLocation.set(File(buildDir, "reports/analysis/code/quality/${variant.name}/$source/html/index.html"))
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
            val detektTask = tasks.getByName<io.gitlab.arturbosch.detekt.Detekt>("detekt$capitalize$postfix")
            classpath.setFrom(detektTask.classpath)
        }
    }
}

androidComponents.onVariants { variant ->
    val output = variant.outputs.single()
    check(output is com.android.build.api.variant.impl.VariantOutputImpl)
    output.outputFileName.set("${rootProject.name}-${Version.Application.name}-${variant.name}-${Version.Application.code}.apk")
    afterEvaluate {
        setCoverage(variant)
        setCodeQuality(variant)
        tasks.getByName<JavaCompile>("compile${variant.name.capitalize()}JavaWithJavac") {
            targetCompatibility = Version.jvmTarget
        }
        tasks.getByName<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>("compile${variant.name.capitalize()}Kotlin") {
            kotlinOptions.jvmTarget = Version.jvmTarget
        }
        val checkManifestTask = task("checkManifest${variant.name.capitalize()}") {
            dependsOn("compile${variant.name.capitalize()}Sources")
            doLast {
                val file = "intermediates/merged_manifest/${variant.name}/AndroidManifest.xml"
                val manifest = groovy.xml.XmlParser().parse(File(buildDir, file))
                val actual = manifest.getAt(groovy.namespace.QName("uses-permission")).map {
                    check(it is groovy.util.Node)
                    val attributes = it.attributes().mapKeys { (k, _) -> k.toString() }
                    val name = attributes["{http://schemas.android.com/apk/res/android}name"]
                    check(name is String && name.isNotEmpty())
                    name
                }
                val applicationId by variant.applicationId
                val expected = setOf(
                    "$applicationId.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION",
                )
                check(actual.sorted() == expected.sorted()) {
                    "Actual is:\n$actual\nbut expected is:\n$expected"
                }
            }
        }
        tasks.getByName("assemble${variant.name.capitalize()}") {
            dependsOn(checkManifestTask)
        }
    }
}

dependencies {
    implementation("androidx.activity:activity-compose:1.6.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.compose.foundation:foundation:${Version.Android.compose}")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.5.1")
    implementation("androidx.security:security-crypto:1.0.0")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
}
