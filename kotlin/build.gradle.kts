plugins {
    kotlin("jvm") version "2.0.21" apply false
}

allprojects {
    group = "io.github.osdanova.ffxprojecteditor"
    version = "0.1.0-SNAPSHOT"
}

subprojects {
    repositories {
        mavenCentral()
    }

    plugins.withId("org.jetbrains.kotlin.jvm") {
        extensions.configure<org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension> {
            jvmToolchain(21)
        }

        dependencies {
            "testImplementation"(kotlin("test"))
        }

        tasks.withType<Test>().configureEach {
            useJUnitPlatform()
        }
    }
}
