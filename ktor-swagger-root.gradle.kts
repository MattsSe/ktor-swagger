import org.jetbrains.kotlin.gradle.dsl.Coroutines

buildscript {
    repositories {
        mavenCentral()
        jcenter()
        maven {
            setUrl("https://plugins.gradle.org/m2/")
        }
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${property("kotlin.version")}")
    }
}
plugins {
    // https://github.com/diffplug/spotless/tree/master/plugin-gradle
    id("com.diffplug.gradle.spotless") version "3.10.0"
}

object Versions {
    /**
     * The version of KtLint to be used for linting the Kotlin and Kotlin Script files.
     */
    const val KTLINT = "0.23.1"
}

allprojects {
    apply {
        plugin("com.diffplug.gradle.spotless")
    }
    group = "de.nielsfalk.playground"
    version = "0.1-SNAPSHOT"

    repositories {
        mavenCentral()
        jcenter()
        maven { setUrl("https://dl.bintray.com/kotlin/ktor") }
    }
}

fun DependencyHandler.ktor(name: String) =
    create(group = "io.ktor", name = name, version = "0.9.2")

subprojects {
    apply {
        plugin("kotlin")
        plugin("java-library")
    }

    dependencies {
        "api"(kotlin(module = "stdlib", version = property("kotlin.version") as String))
        "api"(kotlin(module = "reflect", version = property("kotlin.version") as String))
        "api"(ktor("ktor-locations"))
        "api"(ktor("ktor-server-core"))

        "testImplementation"(ktor("ktor-server-test-host"))
        "testImplementation"(ktor("ktor-gson"))
        "testImplementation"(group = "com.winterbe", name = "expekt", version = "0.5.0")
    }

    kotlin {
        // Enable coroutines supports for Kotlin.
        experimental.coroutines = Coroutines.ENABLE
    }

    spotless {
        kotlin {
            ktlint(Versions.KTLINT)
            trimTrailingWhitespace()
            endWithNewline()
        }
    }
}

/**
 * Configures the [kotlin][org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension] project extension.
 */
fun Project.`kotlin`(configure: org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension.() -> Unit): Unit =
    extensions.configure("kotlin", configure)