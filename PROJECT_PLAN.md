# FFX Project Editor — Java/Kotlin Port Plan

Living document tracking the port of the C# / Avalonia editor (`FFXProjectEditor/`) to a Kotlin / JavaFX / Panama stack (`kotlin/`).

**Branch**: `claude/plan-java-port-VLkng`
**Convention**: at the end of each phase, append a "Status" entry to that phase below summarizing what shipped and listing any deviations from the originally-planned scope (with reasoning).

---

## High-level architecture

Three modules, mirroring a clean UI/non-UI/native-deps seam:

```
kotlin/
├── ffx-lib/      Pure-Kotlin port of FfxLib + Files + Utils. No UI, no native.
├── ffx-memory/   Process-memory abstraction. Panama on Windows; no-op elsewhere.
├── ffx-app/      JavaFX UI port of Modules + Controls + Converters + Services.
└── build.gradle.kts (multi-module Gradle, JDK 21, Kotlin 2.0.21)
```

## Toolchain & dependency map

| C# / .NET | Kotlin / JVM |
|---|---|
| .NET 8 | JDK 21 (toolchain) — bump to 22 for Panama if/when needed |
| MSBuild + .csproj | Gradle Kotlin DSL |
| Avalonia 11 + AXAML | JavaFX 22 + FXML |
| `CommunityToolkit.Mvvm` | JavaFX `Property<T>` + Kotlin delegated properties |
| `Xe.BinaryMapper` | In-house `BinaryMapping` + `@BinField` (see phase 2) |
| `Binarysharp.MSharp` | Panama FFM bindings to `kernel32.dll` (phase 4) |
| `[Flags]` enum + `BitFlag_Util` | Kotlin enum + `mask: Int` + `BitFlag_Util` helpers |

---

## Phase 1 — Bootstrap

**Plan**:
1. Multi-module Gradle build (root + ffx-lib + ffx-memory + ffx-app).
2. JDK toolchain + Kotlin plugin.
3. JavaFX plugin wired into ffx-app.
4. Empty `App.kt` opens a blank stage.
5. `gradle assemble` succeeds end-to-end.

**Status: ✅ complete** (commit `acb9c44`)

Shipped:
- `kotlin/settings.gradle.kts` + root `kotlin/build.gradle.kts`.
- `ffx-lib`, `ffx-memory`, `ffx-app` skeletons.
- `App.kt` opens an 800×450 stage titled "FFX Project Editor — Kotlin port (phase 1)".
- `.gitignore`, README.

**Deviations**:
- **JDK 21 instead of JDK 22.** The local environment ships JDK 21 and Gradle's toolchain auto-provisioning can't reach the network from this sandbox. Panama's Foreign Function & Memory API was finalized in JDK 22 (JEP 454) but is preview in JDK 21. We'll either bump the toolchain or use `--enable-preview` only in `ffx-memory` when phase 4 lands.
- **Kotlin port lives in a `kotlin/` subdirectory** alongside the existing C# project rather than replacing it. Lets the two coexist while the port matures.

---

## Phase 2 — `ffx-lib` foundation

**Plan**:
1. Custom binary (de)serializer to replace `Xe.BinaryMapper` (annotation-driven).
2. Port `Utils/` (Bit_Util, BitFlag_Util, TimerTask).
3. Port `FfxLib/Common/` (CommonStructs, EntryListFile, status lists, ElementalWeaknessData, EquipmentStruct, FfxCommon_Util, EquipmentPrice_Util).
4. Port `FfxLib/Encoding/` (FfxEncoding incl. control codes + US/JP encoder/decoder maps).
5. Port `FfxLib/Dictionaries/` (all enums + dictionaries) and `FfxLib/Memory/MemoryMap`.
6. Round-trip tests for the binary mapper.

**Status: ✅ complete** (commits `7f...`, `6cb9fd1`, `07cca72` plus subagent commits)

Shipped:
- `binary/BinField.kt` + `binary/BinaryMapping.kt` — supports `Byte/UByte/Short/UShort/Int/UInt/Long/ULong/Boolean`, `ByteArray`/`ShortArray`/`IntArray` + typed `Array<T>` (with `count`), and recursive nested objects.
- `binary/BinaryMappingTest.kt` — 4 tests (primitives, arrays, nested, explicit offsets) all green.
- `util/` — Bit_Util (extension fns), BitFlag_Util (object with overloads for Int/UInt/UShort/UByte), TimerTask (ScheduledExecutorService-backed).
- `ffxlib/common/` — FileHeader, TextScriptInfo, EntryListFile, StatusByteList, StatusDurationByteList, AbilityStatusList, ElementalWeaknessData (20 boolean accessors), EquipmentStruct (offset-based with WeaponFlags + EquipmentType_Enum), FfxCommon_Util, EquipmentPrice_Util.
- `ffxlib/dictionaries/` — 14 files: Element/Character/DamageFormula/AssetCategory/GameCategory enums, Element_Flags, plus Item/Monster/Character/Command*/AutoAbility(/Price) lookup tables.
- `ffxlib/encoding/` — FfxEncoding object + nested `TextScript` class + ControlDecoder/FormatCodes/CharacterNameCodes maps + UsDecoder/UsEncoder + JpDecoder/JpEncoder.
- `ffxlib/memory/MemoryMap.kt` — all FFX in-memory address constants.
- `gradle build` succeeds across all three modules.

**Deviations**:
- **Mapper does not support enums** (consciously). Kotlin enums lack arbitrary backing values, so a direct port of C# `[Data] EnumType field` would force either a per-enum size annotation or a `BinEnum<T>` interface. Both are heavier than the alternative we chose: store the raw primitive (`UByte`/`UShort`/etc.) on annotated fields, and expose typed enum accessors via `var x: MyEnum get()/set()` or the `BitFlag_Util` helpers. Every enum class still ships a `fromValue(Int): Enum?` companion so callers get type-safe conversion. Net effect: a small amount of accessor boilerplate at use sites in exchange for a much smaller mapper.
- **Three subagents ran in parallel** for the bulk porting (~3000 LOC of mostly-mechanical translation: dictionaries+memory map, encoding, utils+common). Token cost stays in the subagent contexts; the foundational mapper was written in main context for correctness.
- **`kernel/*.bin` kdoc bug surfaced and fixed** in `ffxlib/common/CommonStructs.kt` — the `*.bin` glob was being parsed as a nested block-comment opener, breaking compilation. Caught by the dictionaries subagent during its compile-verify step.
- **`SingletonBase<T>` not ported.** Kotlin's `object` keyword is the idiomatic singleton; all C# usages were translated to `object` declarations directly.

---

## Phase 3 — `ffx-lib` file IO

**Plan**:
1. Port Monster: Monster_Structs, Monster_StatSheet, Monster_Loot, Monster_File.
2. Port Ability: Ability_Structs, Ability_Gear, Ability_Command (incl. read/write list helpers).
3. Port Arm: Arms_Rate.
4. Add **on-disk round-trip tests** against sample game files where available.

**Status: ✅ complete** (commits `ca7f41e`, `ed9ce8c`)

Shipped:
- `ffxlib/monster/` — Monster_Structs (MonsterHeaderFile, MonsterStatSheetStruct), Monster_StatSheet (4 top-level flag enums + ~150 boolean accessors + readSingle/writeSingle + text-blob handling), Monster_Loot (incl. nested LootGearAbilities), Monster_File (multi-section file IO with reverse-pointer walk for read, sequential append + offset-recording for write).
- `ffxlib/ability/` — Ability_Structs (Ability_CommandStruct, Ability_GearStruct), Ability_Gear, Ability_Command (13 top-level `[Flags]` enums + HitCalcType + ExtraCommandInfo + ~80 boolean accessors + readSingle/readList/writeSingle/writeList with optional ExtraInfo + the bit-packed flagMisc1HitCalcType accessor).
- `ffxlib/arm/Arms_Rate.kt` — readList + parity writeList helper.
- `test/Fixtures.kt` — opt-in fixture loader keyed off `FFX_FIXTURES_DIR` env var.
- `MonsterRoundTripTest`: 4 tests (synthetic stat sheet, synthetic loot, synthetic monster file, real m###.bin byte-for-byte). Real-binary test auto-skips when fixtures unset.
- `AbilityRoundTripTest`: 7 tests (synthetic command without/with ExtraInfo, command list, hit-calc bit field, arms rate list, real command/item/monmagic byte-for-byte, real arms_rate byte-for-byte). Real-binary tests auto-skip when fixtures unset.
- 15 tests total; 12 pass without fixtures, 3 fixture-skipped.
- Gradle test task forwards `FFX_FIXTURES_DIR` to the test JVM.

**Deviations**:
- **`MonsterHeaderFile.padding` reduced from 16 bytes to 12.** The C# source declared `[Data(Count=16)]` (header = 0x34 = 52 bytes) but its writer positions the body at 0x30 (= 48 bytes). On every save, the C# code silently corrupts the first 4 bytes of the AI section. The `// Aligns the header to 16 bytes` comment combined with the 0x30 body offset shows the intent was 12 bytes of padding (header = exactly 0x30). Fixed in our port to make round-trips byte-faithful; documented inline. Worth checking against real fixtures to confirm — if real game files actually do have 0x34 headers we'll need to revisit.
- **Two subagents in parallel** for the bulk ports: Monster (~860 LOC) and Ability+Arm (~970 LOC). Test suite written in main context.
- **Text-blob handling kept as 1:1 port.** Each text owner (StatSheet, Command) carries `*ScriptBytes: ByteArray` and `*ScriptId: UShort` fields plus a `TextScriptInfo` block in its on-disk struct. The text blob is appended after the struct block and indexed via the offsets — exactly as in C#. Considered consolidating to a `TextSection` helper class but the savings were small and the parallel structure between classes makes the C# layout easier to verify.

---

## Phase 4 — `ffx-memory` (Panama Windows bindings) — pending

**Plan**:
1. `ProcessMemory` interface mirroring the C# `MemSharp_Service` surface (`isAvailable`, `read<T>`, `readBytes`, `write`, `writeString`, `readString`).
2. `WindowsPanamaProcessMemory` implementation: `Linker.nativeLinker()` + `SymbolLookup.libraryLookup("kernel32", arena)` for `OpenProcess`, `CloseHandle`, `ReadProcessMemory`, `WriteProcessMemory`, `CreateToolhelp32Snapshot`, `Process32FirstW`/`Process32NextW`. Reproduce MemorySharp's `isRelative` math (`base + offset`).
3. `NoopProcessMemory` for non-Windows platforms (`isAvailable` returns false; matches today's degraded behavior on Linux).
4. Selection at startup based on `System.getProperty("os.name")`.
5. Toolchain decision: bump to JDK 22 (Panama final) **or** keep JDK 21 and add `--enable-preview` to ffx-memory only.

**Status**: not started.

---

## Phase 5 — App services (cross-platform) — pending

**Plan**:
1. `ProjectService` — load/validate the master folder + path constants (port from `Project_Service.cs`).
2. `ProcessService` — autodetect FFX.exe via `ProcessHandle` (cross-platform).
3. `MemoryService` — wraps `ffx-memory` + project-aware helpers.

---

## Phase 6 — Main window + project loading — pending

**Plan**: Bottom-bar project picker, "Hook" indicator, top menu wiring, end-to-end project load.

---

## Phase 7 — Editors — pending

**Plan** (simple → complex):
1. `BattleKernel/Commands` (shared schema across Items/Commands/MonMagic1/MonMagic2 — biggest leverage).
2. `MonEditor`.
3. `DebugMenu`.
4. Trackers: `InventoryTracker`, `ArenaTracker`, `BattleTracker`, `SaveTracker`.

---

## Phase 8 — Polish & packaging — pending

**Plan**:
1. App icon + manifest equivalents.
2. `jpackage` installer for win-x64 + linux-x64 (replaces `IncludeAllContentForSelfExtract`).
3. Update root README pointing at the Kotlin port.

---

## Test running

```sh
cd kotlin
gradle build              # compile everything + run all tests

# To activate the opt-in fixture-backed tests against a local extracted master folder:
FFX_FIXTURES_DIR=/path/to/master gradle :ffx-lib:test
```

Tests assume the user's `FFX_FIXTURES_DIR` mirrors the layout the editor itself expects:
```
$FFX_FIXTURES_DIR/jppc/battle/mon/_m###/m###.bin
$FFX_FIXTURES_DIR/jppc/battle/kernel/arms_rate.bin
$FFX_FIXTURES_DIR/new_uspc/battle/kernel/{command,item,monmagic1,monmagic2}.bin
```

## Risks & gotchas (from initial scoping, still applicable)

- **Endianness**: `Xe.BinaryMapper` is little-endian; our `BinaryMapping` pins `LITTLE_ENDIAN` everywhere. Easy to forget; would silently corrupt saves.
- **Unsigned types**: Kotlin's `UByte`/`UShort`/`UInt` are well-supported but `ByteBuffer` returns signed types. The mapper handles the conversion in one place; downstream code should prefer the unsigned types where the C# uses `byte`/`ushort`/`uint`.
- **Property change propagation**: Avalonia's `[NotifyPropertyChangedFor]` will translate to JavaFX `Bindings.createBooleanBinding(...)` in phase 5/6.
- **Panama API drift**: pin JDK in toolchain to avoid surprises.
- **String encoding**: FFX uses a custom encoding (`FfxEncoding.us` / `.jp`), not UTF-8. Memory reads use UTF-8 (matches C# `MemSharp_Service._defaultEncoding`). Don't conflate the two.
