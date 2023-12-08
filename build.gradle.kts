import sp.gx.core.check

buildscript {
    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:8.1.4")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Version.kotlin}")
    }
}

task<Delete>("clean") {
    delete = setOf(layout.buildDirectory.get(), "buildSrc/build")
}

task("checkLicense") {
    doLast {
        val author = "Stanley Wintergreen" // todo
        val report = layout.buildDirectory.get()
            .dir("reports/analysis/license")
            .file("index.html")
            .asFile
        rootDir.resolve("LICENSE").check(
            expected = emptySet(),
            regexes = setOf("^Copyright 2\\d{3} $author${'$'}".toRegex()),
            report = report,
        )
    }
}
