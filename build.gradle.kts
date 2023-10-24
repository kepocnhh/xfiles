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
    delete = setOf(layout.buildDirectory.get(), "buildSrc/build")
}

repositories.mavenCentral()

val ktlint: Configuration by configurations.creating

dependencies {
    ktlint("com.pinterest:ktlint:${Version.ktlint}") {
        attributes {
            attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.EXTERNAL))
        }
    }
}

task<JavaExec>("checkCodeStyle") {
    classpath = ktlint
    mainClass.set("com.pinterest.ktlint.Main")
    val output = layout.buildDirectory.file("reports/analysis/code/style/html/index.html").get()
    args(
        "app/build.gradle.kts",
        "app/src/debug/kotlin/**/*.kt",
        "app/src/main/kotlin/**/*.kt",
        "app/src/test/kotlin/**/*.kt",
        "build.gradle.kts",
        "buildSrc/build.gradle.kts",
        "buildSrc/src/main/kotlin/**/*.kt",
        "settings.gradle.kts",
        "--reporter=html,output=${output.asFile.absolutePath}",
    )
}
