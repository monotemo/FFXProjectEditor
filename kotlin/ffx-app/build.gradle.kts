plugins {
    kotlin("jvm")
    application
    id("org.openjfx.javafxplugin") version "0.1.0"
}

dependencies {
    implementation(project(":ffx-lib"))
    implementation(project(":ffx-memory"))
}

javafx {
    version = "22.0.2"
    modules = listOf("javafx.controls", "javafx.fxml")
}

application {
    mainClass.set("io.github.osdanova.ffxprojecteditor.AppKt")
}
