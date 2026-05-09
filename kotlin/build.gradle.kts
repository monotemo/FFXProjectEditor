plugins {
    kotlin("jvm") version "2.3.21" apply false
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
            // Forward FFX_FIXTURES_DIR so opt-in fixture-backed tests can
            // locate the user's local extracted master folder.
            System.getenv("FFX_FIXTURES_DIR")?.let { environment("FFX_FIXTURES_DIR", it) }
            testLogging {
                events("skipped", "failed")
            }
        }
    }
}
