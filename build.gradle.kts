buildscript {
    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:8.1.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Version.kotlin}")
    }
}

task<Delete>("clean") {
    delete = setOf(buildDir, "buildSrc/build")
}

repositories.mavenCentral()

val kotlinLint: Configuration by configurations.creating

dependencies {
    kotlinLint("com.pinterest:ktlint:${Version.ktlint}") {
        attributes {
            attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.EXTERNAL))
        }
    }
}

task<JavaExec>("checkCodeStyle") {
    classpath = kotlinLint
    mainClass.set("com.pinterest.ktlint.Main")
    args(
        "build.gradle.kts",
        "settings.gradle.kts",
        "buildSrc/src/main/kotlin/**/*.kt",
        "buildSrc/build.gradle.kts",
        "app/src/debug/kotlin/**/*.kt",
        "app/src/main/kotlin/**/*.kt",
        "app/src/test/kotlin/**/*.kt",
        "app/build.gradle.kts",
        "--reporter=html,output=${File(buildDir, "reports/analysis/code/style/html/index.html")}",
    )
}
