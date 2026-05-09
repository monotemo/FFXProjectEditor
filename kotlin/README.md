# FFX Project Editor — Kotlin port

In-progress Kotlin/JavaFX port of the C# Avalonia editor in `../FFXProjectEditor/`.

## Modules

- **ffx-lib** — pure-Kotlin port of `FfxLib/`, `Files/`, `Utils/` (binary file IO, no UI, no native deps).
- **ffx-memory** — abstraction over the running FFX.exe process. Windows impl will use the JDK Foreign Function & Memory API (Project Panama). No-op on other platforms.
- **ffx-app** — JavaFX UI port of `Modules/`, `Controls/`, `Converters/`, `Services/`.

## Requirements

- JDK 21 (toolchain pinned in `build.gradle.kts`).
- The JavaFX 22 runtime is pulled in via the `org.openjfx.javafxplugin` Gradle plugin.

## Building

```sh
cd kotlin
gradle build           # compile + test all modules
gradle :ffx-app:run    # launch the (currently empty) JavaFX shell
```

## Status

Phase 1: project skeleton only — empty stage opens. See the port plan in chat history for the full phase breakdown.
