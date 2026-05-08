plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":ffx-lib"))
}

// The Panama FFM API is finalized in JDK 22 (JEP 454). On JDK 21 it is a
// preview API; the Windows-backed implementation will require --enable-preview
// when we land it in a later phase. Phase 1 only ships the abstraction.
