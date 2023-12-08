import com.android.build.api.variant.ComponentIdentity
import io.gitlab.arturbosch.detekt.Detekt
import sp.gx.core.Badge
import sp.gx.core.GitHub
import sp.gx.core.Markdown
import sp.gx.core.camelCase
import sp.gx.core.check
import sp.gx.core.existing
import sp.gx.core.file
import sp.gx.core.filled
import sp.gx.core.kebabCase
import sp.gx.core.slashCase
import sp.gx.core.resolve

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

fun checkCoverage(variant: ComponentIdentity) {
    val taskUnitTest = camelCase("test", variant.name, "UnitTest")
    val executionData = layout.buildDirectory.get()
        .dir("outputs/unit_test_code_coverage/${variant.name}UnitTest")
        .file("$taskUnitTest.exec")
    tasks.getByName<Test>(taskUnitTest) {
        doLast {
            executionData.existing().file().filled()
        }
    }
    val taskCoverageReport = task<JacocoReport>(camelCase("assemble", variant.name, "CoverageReport")) {
        dependsOn(taskUnitTest)
        reports {
            csv.required = false
            html.required = true
            xml.required = false
        }
        sourceDirectories.setFrom(file("src/main/kotlin"))
        val root = layout.buildDirectory.get()
            .dir("tmp/kotlin-classes")
            .dir(variant.name)
        val dirs = fileTree(root) {
            val rootPackage = android.namespace!!.replace('.', '/')
            val path = "**/$rootPackage/module/**"
            setOf("Screen", "ViewModel").forEach { name ->
                include(
                    "$path/*$name.class",
                    "$path/*${name}Kt.class",
                )
            }
        }
        classDirectories.setFrom(dirs)
        executionData(executionData)
        doLast {
            val report = layout.buildDirectory.get()
                .dir("reports/jacoco/$name/html")
                .file("index.html")
                .asFile
            if (report.exists()) {
                println("Coverage report: ${report.absolutePath}")
            }
        }
    }
    task<JacocoCoverageVerification>(camelCase("check", variant.name, "Coverage")) {
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

fun checkCodeQuality(variant: ComponentIdentity, configs: Iterable<File>, sources: Iterable<File>, postfix: String = "") {
    task<Detekt>(camelCase("check", variant.name, "CodeQuality", postfix)) {
        jvmTarget = Version.jvmTarget
        setSource(sources)
        config.setFrom(configs)
        val report = layout.buildDirectory.get()
            .dir("reports/analysis/code/quality")
            .dir(slashCase(variant.name, postfix, "html"))
            .file("index.html")
            .asFile
        reports {
            html {
                required = true
                outputLocation = report
            }
            md.required = false
            sarif.required = false
            txt.required = false
            xml.required = false
        }
        val detektTask = tasks.getByName<Detekt>(camelCase("detekt", variant.name, postfix))
        classpath.setFrom(detektTask.classpath)
        doFirst {
            println("Analysis report: ${report.absolutePath}")
        }
    }
}

fun getDetektConfigs(): Iterable<File> {
    return setOf(
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
}

fun getDetektUnitTestConfigs(): Iterable<File> {
    return setOf(
        "android/test",
        "test",
    ).map { config ->
        rootDir.resolve("buildSrc/src/main/resources/detekt/config/$config.yml")
            .existing()
            .file()
            .filled()
    }
}

fun checkReadme(variant: ComponentIdentity) {
    task(camelCase("check", variant.name, "Readme")) {
        doLast {
            val badge = Markdown.image(
                text = "version",
                url = Badge.url(
                    label = "version",
                    message = variant.getVersion(),
                    color = "2962ff",
                ),
            )
            val fileName = "${gh.name}-${variant.getVersion()}.apk"
            val releaseLink = Markdown.link("release", gh.url().resolve("releases", "tag", variant.getVersion()))
            val apkLink = Markdown.link("apk", gh.url().resolve("releases", "download", variant.getVersion(), fileName))
            val expected = setOf(
                badge,
                "GitHub $releaseLink",
                "Download $apkLink",
            )
            val report = layout.buildDirectory.get()
                .dir("reports/analysis/readme")
                .dir(variant.name)
                .file("index.html")
                .asFile
            rootDir.resolve("README.md").check(
                expected = expected,
                report = report,
            )
        }
    }
}

val ktlint: Configuration by configurations.creating

fun checkCodeStyle(variant: ComponentIdentity) {
    task<JavaExec>(camelCase("check", variant.name, "CodeStyle")) {
        classpath = ktlint
        mainClass = "com.pinterest.ktlint.Main"
        val reporter = "html"
        val output = layout.buildDirectory.get()
            .dir("reports/analysis/code/style")
            .dir("${variant.name}/html")
            .file("index.html")
            .asFile
        val files = setOf(
            "build.gradle.kts",
            "settings.gradle.kts",
            "buildSrc/src/main/kotlin/**/*.kt",
            "buildSrc/build.gradle.kts",
        ).map(rootDir::resolve) + setOf(
            "src/main/kotlin/**/*.kt",
            "src/test/kotlin/**/*.kt",
            "src/${variant.buildType!!}/kotlin/**/*.kt",
            "build.gradle.kts",
        ).map(::file)
        args(files + "--reporter=$reporter,output=${output.absolutePath}")
    }
}

androidComponents.onVariants { variant ->
    val output = variant.outputs.single()
    check(output is com.android.build.api.variant.impl.VariantOutputImpl)
    output.outputFileName = "${kebabCase(rootProject.name, variant.getVersion())}.apk"
    afterEvaluate {
        tasks.getByName<JavaCompile>(camelCase("compile", variant.name, "JavaWithJavac")) {
            targetCompatibility = Version.jvmTarget
        }
        tasks.getByName<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>(camelCase("compile", variant.name, "Kotlin")) {
            kotlinOptions.jvmTarget = Version.jvmTarget
        }
        if (variant.buildType == android.testBuildType) {
            tasks.getByName<JavaCompile>(camelCase("compile", variant.name, "UnitTest", "JavaWithJavac")) {
                targetCompatibility = Version.jvmTarget
            }
            tasks.getByName<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>(camelCase("compile", variant.name, "UnitTest", "Kotlin")) {
                kotlinOptions.jvmTarget = Version.jvmTarget
            }
            checkCoverage(variant)
            checkCodeQuality(
                variant = variant,
                configs = getDetektConfigs() + getDetektUnitTestConfigs(),
                sources = files("src/test/kotlin"),
                postfix = "UnitTest",
            )
        } else {
            checkCodeQuality(
                variant = variant,
                configs = getDetektConfigs(),
                sources = variant.sources.kotlin!!.all.get().map { it.asFile },
            )
        }
        checkReadme(variant)
        checkCodeStyle(variant)
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
