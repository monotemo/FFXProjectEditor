package io.github.osdanova.ffxprojecteditor.ffxlib.monster

import io.github.osdanova.ffxprojecteditor.binary.BinField
import io.github.osdanova.ffxprojecteditor.binary.BinaryMapping
import io.github.osdanova.ffxprojecteditor.ffxlib.common.ElementalWeaknessData
import io.github.osdanova.ffxprojecteditor.ffxlib.common.StatusByteList
import io.github.osdanova.ffxprojecteditor.ffxlib.encoding.FfxEncoding
import io.github.osdanova.ffxprojecteditor.util.BitFlag_Util
import java.nio.ByteBuffer
import java.nio.ByteOrder

/** Property flags (short, stored as UShort). */
enum class PropertyFlags(val mask: Int) {
    Armored(0x01),
    ImmunityFractionalDamage(0x02),
    ImmunityLife(0x04),
    ImmunitySensor(0x08),
    ImmunityScanAgain_Maybe(0x10),
    ImmunityPhysicalDamage(0x20),
    ImmunityMagicDamage(0x40),
    ImmunityAllDamage(0x80),
    ImmunityDelay(0x100),
    ImmunitySlice_Maybe(0x200),
    ImmunityBribe_Maybe(0x400),
}

/** Auto-status flags 1 (ushort). */
enum class AutoStatusFlags1(val mask: Int) {
    Death(0x01),
    Zombie(0x02),
    Petrify(0x04),
    Poison(0x08),
    BreakPower(0x10),
    BreakMagic(0x20),
    BreakArmor(0x40),
    BreakMental(0x80),
    Confuse(0x0100),
    Berserk(0x0200),
    Provoke(0x0400),
    Threaten(0x0800),
}

/** Auto-status flags 2 (ushort). */
enum class AutoStatusFlags2(val mask: Int) {
    Sleep(0x01),
    Silence(0x02),
    Darkness(0x04),
    Shell(0x08),
    Protect(0x10),
    Reflect(0x20),
    NulTide(0x40),
    NulBlaze(0x80),
    NulShock(0x0100),
    NulFrost(0x0200),
    Regen(0x0400),
    Haste(0x0800),
    Slow(0x1000),
}

/** Auto-status flags 3 (ushort). */
enum class AutoStatusFlags3(val mask: Int) {
    Scan(0x01),
    DistillPower(0x02),
    DistillMana(0x04),
    DistillSpeed(0x08),
    Unused3_04(0x10),
    DistillAbility(0x20),
    Shield(0x40),
    Boost(0x80),
    Eject(0x0100),
    AutoLife(0x0200),
    Curse(0x0400),
    Defend(0x0800),
    Guard(0x1000),
    Sentinel(0x2000),
    Doom(0x4000),
}

/** Extra immunity flags (ushort). */
enum class ExtraImmunitiesFlags(val mask: Int) {
    Scan(0x01),
    DistillPower(0x02),
    DistillMana(0x04),
    DistillSpeed(0x08),
    Unused3_04(0x10),
    DistillAbility(0x20),
    Shield(0x40),
    Boost(0x80),
    Eject(0x0100),
    AutoLife(0x0200),
    Curse(0x0400),
    Defend(0x0800),
    Guard(0x1000),
    Sentinel(0x2000),
    Doom(0x4000),
}

/**
 * Monster stat sheet payload. The on-disk layout follows the order of the
 * `@BinField`-annotated properties below. Boolean accessor properties expose
 * individual bits of the flag fields.
 */
class Monster_StatSheet {
    // Stats
    @BinField var hp: UInt = 0u
    @BinField var mp: UInt = 0u
    @BinField var hpOverkill: UInt = 0u
    @BinField var strength: UByte = 0u
    @BinField var defense: UByte = 0u
    @BinField var magic: UByte = 0u
    @BinField var magicDefense: UByte = 0u
    @BinField var agility: UByte = 0u
    @BinField var luck: UByte = 0u
    @BinField var evasion: UByte = 0u
    @BinField var accuracy: UByte = 0u

    @BinField var property_Flags: UShort = 0u
    @BinField var poisonDamage: UByte = 0u

    // Elements
    @BinField var elementalWeakness: ElementalWeaknessData = ElementalWeaknessData()

    // Status
    @BinField var statusResistance: StatusByteList = StatusByteList()

    @BinField var autoStatus_Flags1: UShort = 0u
    @BinField var autoStatus_Flags2: UShort = 0u
    @BinField var autoStatus_Flags3: UShort = 0u
    @BinField var extraImmunities_Flags: UShort = 0u

    // Abilities
    @BinField(count = 16) var abilities: Array<UShort> = Array(16) { 0u }

    @BinField var forcedAction: UShort = 0u
    @BinField var monsterId: Short = 0
    @BinField var modelId: Short = 0
    @BinField var ctbIconId: UByte = 0u
    @BinField var doomCount: Byte = 0
    @BinField var arenaId: Byte = 0
    @BinField var arenaIdPadding: UByte = 0u
    @BinField var model2Id: Short = 0

    // Scratch fields populated from / written to the trailing text blob.
    var nameScriptBytes: ByteArray = ByteArray(0)
    var sensorScriptBytes: ByteArray = ByteArray(0)
    var unusedText1ScriptBytes: ByteArray = ByteArray(0)
    var scanScriptBytes: ByteArray = ByteArray(0)
    var unusedText2ScriptBytes: ByteArray = ByteArray(0)

    // Text ids kept to keep the original data. Ideally this would be calculated.
    var nameScriptId: UShort = 0u
    var sensorScriptId: UShort = 0u
    var unusedText1ScriptId: UShort = 0u
    var scanScriptId: UShort = 0u
    var unusedText2ScriptId: UShort = 0u

    // Recorded on read so byte-for-byte round-trips are possible. Real monster
    // stat-sheet text blobs commonly contain bytes that no TS entry points at
    // (alignment / unreferenced gaps); the writer can't reconstruct those from
    // script bytes alone, so we capture the original layout here and replay it
    // when nothing has been edited.
    var preservedNameOffset: UShort? = null
    var preservedSensorOffset: UShort? = null
    var preservedUnusedText1Offset: UShort? = null
    var preservedScanOffset: UShort? = null
    var preservedUnusedText2Offset: UShort? = null
    var preservedTextBlob: ByteArray? = null

    // ---- Property flag accessors ----
    var prop_Armored: Boolean
        get() = BitFlag_Util.isFlagSet(property_Flags, PropertyFlags.Armored.mask.toUShort())
        set(v) { property_Flags = BitFlag_Util.setFlag(property_Flags, PropertyFlags.Armored.mask.toUShort(), v) }
    var prop_ImmunityFractionalDamage: Boolean
        get() = BitFlag_Util.isFlagSet(property_Flags, PropertyFlags.ImmunityFractionalDamage.mask.toUShort())
        set(v) { property_Flags = BitFlag_Util.setFlag(property_Flags, PropertyFlags.ImmunityFractionalDamage.mask.toUShort(), v) }
    var prop_ImmunityLife: Boolean
        get() = BitFlag_Util.isFlagSet(property_Flags, PropertyFlags.ImmunityLife.mask.toUShort())
        set(v) { property_Flags = BitFlag_Util.setFlag(property_Flags, PropertyFlags.ImmunityLife.mask.toUShort(), v) }
    var prop_ImmunitySensor: Boolean
        get() = BitFlag_Util.isFlagSet(property_Flags, PropertyFlags.ImmunitySensor.mask.toUShort())
        set(v) { property_Flags = BitFlag_Util.setFlag(property_Flags, PropertyFlags.ImmunitySensor.mask.toUShort(), v) }
    var prop_ImmunityScanAgain_Maybe: Boolean
        get() = BitFlag_Util.isFlagSet(property_Flags, PropertyFlags.ImmunityScanAgain_Maybe.mask.toUShort())
        set(v) { property_Flags = BitFlag_Util.setFlag(property_Flags, PropertyFlags.ImmunityScanAgain_Maybe.mask.toUShort(), v) }
    var prop_ImmunityPhysicalDamage: Boolean
        get() = BitFlag_Util.isFlagSet(property_Flags, PropertyFlags.ImmunityPhysicalDamage.mask.toUShort())
        set(v) { property_Flags = BitFlag_Util.setFlag(property_Flags, PropertyFlags.ImmunityPhysicalDamage.mask.toUShort(), v) }
    var prop_ImmunityMagicDamage: Boolean
        get() = BitFlag_Util.isFlagSet(property_Flags, PropertyFlags.ImmunityMagicDamage.mask.toUShort())
        set(v) { property_Flags = BitFlag_Util.setFlag(property_Flags, PropertyFlags.ImmunityMagicDamage.mask.toUShort(), v) }
    var prop_ImmunityAllDamage: Boolean
        get() = BitFlag_Util.isFlagSet(property_Flags, PropertyFlags.ImmunityAllDamage.mask.toUShort())
        set(v) { property_Flags = BitFlag_Util.setFlag(property_Flags, PropertyFlags.ImmunityAllDamage.mask.toUShort(), v) }
    var prop_ImmunityDelay: Boolean
        get() = BitFlag_Util.isFlagSet(property_Flags, PropertyFlags.ImmunityDelay.mask.toUShort())
        set(v) { property_Flags = BitFlag_Util.setFlag(property_Flags, PropertyFlags.ImmunityDelay.mask.toUShort(), v) }
    var prop_ImmunitySlice_Maybe: Boolean
        get() = BitFlag_Util.isFlagSet(property_Flags, PropertyFlags.ImmunitySlice_Maybe.mask.toUShort())
        set(v) { property_Flags = BitFlag_Util.setFlag(property_Flags, PropertyFlags.ImmunitySlice_Maybe.mask.toUShort(), v) }
    var prop_ImmunityBribe_Maybe: Boolean
        get() = BitFlag_Util.isFlagSet(property_Flags, PropertyFlags.ImmunityBribe_Maybe.mask.toUShort())
        set(v) { property_Flags = BitFlag_Util.setFlag(property_Flags, PropertyFlags.ImmunityBribe_Maybe.mask.toUShort(), v) }

    // ---- Auto-status flag accessors (group 1) ----
    var auto_Death: Boolean
        get() = BitFlag_Util.isFlagSet(autoStatus_Flags1, AutoStatusFlags1.Death.mask.toUShort())
        set(v) { autoStatus_Flags1 = BitFlag_Util.setFlag(autoStatus_Flags1, AutoStatusFlags1.Death.mask.toUShort(), v) }
    var auto_Zombie: Boolean
        get() = BitFlag_Util.isFlagSet(autoStatus_Flags1, AutoStatusFlags1.Zombie.mask.toUShort())
        set(v) { autoStatus_Flags1 = BitFlag_Util.setFlag(autoStatus_Flags1, AutoStatusFlags1.Zombie.mask.toUShort(), v) }
    var auto_Petrify: Boolean
        get() = BitFlag_Util.isFlagSet(autoStatus_Flags1, AutoStatusFlags1.Petrify.mask.toUShort())
        set(v) { autoStatus_Flags1 = BitFlag_Util.setFlag(autoStatus_Flags1, AutoStatusFlags1.Petrify.mask.toUShort(), v) }
    var auto_Poison: Boolean
        get() = BitFlag_Util.isFlagSet(autoStatus_Flags1, AutoStatusFlags1.Poison.mask.toUShort())
        set(v) { autoStatus_Flags1 = BitFlag_Util.setFlag(autoStatus_Flags1, AutoStatusFlags1.Poison.mask.toUShort(), v) }
    var auto_BreakPower: Boolean
        get() = BitFlag_Util.isFlagSet(autoStatus_Flags1, AutoStatusFlags1.BreakPower.mask.toUShort())
        set(v) { autoStatus_Flags1 = BitFlag_Util.setFlag(autoStatus_Flags1, AutoStatusFlags1.BreakPower.mask.toUShort(), v) }
    var auto_BreakMagic: Boolean
        get() = BitFlag_Util.isFlagSet(autoStatus_Flags1, AutoStatusFlags1.BreakMagic.mask.toUShort())
        set(v) { autoStatus_Flags1 = BitFlag_Util.setFlag(autoStatus_Flags1, AutoStatusFlags1.BreakMagic.mask.toUShort(), v) }
    var auto_BreakArmor: Boolean
        get() = BitFlag_Util.isFlagSet(autoStatus_Flags1, AutoStatusFlags1.BreakArmor.mask.toUShort())
        set(v) { autoStatus_Flags1 = BitFlag_Util.setFlag(autoStatus_Flags1, AutoStatusFlags1.BreakArmor.mask.toUShort(), v) }
    var auto_BreakMental: Boolean
        get() = BitFlag_Util.isFlagSet(autoStatus_Flags1, AutoStatusFlags1.BreakMental.mask.toUShort())
        set(v) { autoStatus_Flags1 = BitFlag_Util.setFlag(autoStatus_Flags1, AutoStatusFlags1.BreakMental.mask.toUShort(), v) }
    var auto_Confuse: Boolean
        get() = BitFlag_Util.isFlagSet(autoStatus_Flags1, AutoStatusFlags1.Confuse.mask.toUShort())
        set(v) { autoStatus_Flags1 = BitFlag_Util.setFlag(autoStatus_Flags1, AutoStatusFlags1.Confuse.mask.toUShort(), v) }
    var auto_Berserk: Boolean
        get() = BitFlag_Util.isFlagSet(autoStatus_Flags1, AutoStatusFlags1.Berserk.mask.toUShort())
        set(v) { autoStatus_Flags1 = BitFlag_Util.setFlag(autoStatus_Flags1, AutoStatusFlags1.Berserk.mask.toUShort(), v) }
    var auto_Provoke: Boolean
        get() = BitFlag_Util.isFlagSet(autoStatus_Flags1, AutoStatusFlags1.Provoke.mask.toUShort())
        set(v) { autoStatus_Flags1 = BitFlag_Util.setFlag(autoStatus_Flags1, AutoStatusFlags1.Provoke.mask.toUShort(), v) }
    var auto_Threaten: Boolean
        get() = BitFlag_Util.isFlagSet(autoStatus_Flags1, AutoStatusFlags1.Threaten.mask.toUShort())
        set(v) { autoStatus_Flags1 = BitFlag_Util.setFlag(autoStatus_Flags1, AutoStatusFlags1.Threaten.mask.toUShort(), v) }

    // ---- Auto-status flag accessors (group 2) ----
    var auto_Sleep: Boolean
        get() = BitFlag_Util.isFlagSet(autoStatus_Flags2, AutoStatusFlags2.Sleep.mask.toUShort())
        set(v) { autoStatus_Flags2 = BitFlag_Util.setFlag(autoStatus_Flags2, AutoStatusFlags2.Sleep.mask.toUShort(), v) }
    var auto_Silence: Boolean
        get() = BitFlag_Util.isFlagSet(autoStatus_Flags2, AutoStatusFlags2.Silence.mask.toUShort())
        set(v) { autoStatus_Flags2 = BitFlag_Util.setFlag(autoStatus_Flags2, AutoStatusFlags2.Silence.mask.toUShort(), v) }
    var auto_Darkness: Boolean
        get() = BitFlag_Util.isFlagSet(autoStatus_Flags2, AutoStatusFlags2.Darkness.mask.toUShort())
        set(v) { autoStatus_Flags2 = BitFlag_Util.setFlag(autoStatus_Flags2, AutoStatusFlags2.Darkness.mask.toUShort(), v) }
    var auto_Shell: Boolean
        get() = BitFlag_Util.isFlagSet(autoStatus_Flags2, AutoStatusFlags2.Shell.mask.toUShort())
        set(v) { autoStatus_Flags2 = BitFlag_Util.setFlag(autoStatus_Flags2, AutoStatusFlags2.Shell.mask.toUShort(), v) }
    var auto_Protect: Boolean
        get() = BitFlag_Util.isFlagSet(autoStatus_Flags2, AutoStatusFlags2.Protect.mask.toUShort())
        set(v) { autoStatus_Flags2 = BitFlag_Util.setFlag(autoStatus_Flags2, AutoStatusFlags2.Protect.mask.toUShort(), v) }
    var auto_Reflect: Boolean
        get() = BitFlag_Util.isFlagSet(autoStatus_Flags2, AutoStatusFlags2.Reflect.mask.toUShort())
        set(v) { autoStatus_Flags2 = BitFlag_Util.setFlag(autoStatus_Flags2, AutoStatusFlags2.Reflect.mask.toUShort(), v) }
    var auto_NulTide: Boolean
        get() = BitFlag_Util.isFlagSet(autoStatus_Flags2, AutoStatusFlags2.NulTide.mask.toUShort())
        set(v) { autoStatus_Flags2 = BitFlag_Util.setFlag(autoStatus_Flags2, AutoStatusFlags2.NulTide.mask.toUShort(), v) }
    var auto_NulBlaze: Boolean
        get() = BitFlag_Util.isFlagSet(autoStatus_Flags2, AutoStatusFlags2.NulBlaze.mask.toUShort())
        set(v) { autoStatus_Flags2 = BitFlag_Util.setFlag(autoStatus_Flags2, AutoStatusFlags2.NulBlaze.mask.toUShort(), v) }
    var auto_NulShock: Boolean
        get() = BitFlag_Util.isFlagSet(autoStatus_Flags2, AutoStatusFlags2.NulShock.mask.toUShort())
        set(v) { autoStatus_Flags2 = BitFlag_Util.setFlag(autoStatus_Flags2, AutoStatusFlags2.NulShock.mask.toUShort(), v) }
    var auto_NulFrost: Boolean
        get() = BitFlag_Util.isFlagSet(autoStatus_Flags2, AutoStatusFlags2.NulFrost.mask.toUShort())
        set(v) { autoStatus_Flags2 = BitFlag_Util.setFlag(autoStatus_Flags2, AutoStatusFlags2.NulFrost.mask.toUShort(), v) }
    var auto_Regen: Boolean
        get() = BitFlag_Util.isFlagSet(autoStatus_Flags2, AutoStatusFlags2.Regen.mask.toUShort())
        set(v) { autoStatus_Flags2 = BitFlag_Util.setFlag(autoStatus_Flags2, AutoStatusFlags2.Regen.mask.toUShort(), v) }
    var auto_Haste: Boolean
        get() = BitFlag_Util.isFlagSet(autoStatus_Flags2, AutoStatusFlags2.Haste.mask.toUShort())
        set(v) { autoStatus_Flags2 = BitFlag_Util.setFlag(autoStatus_Flags2, AutoStatusFlags2.Haste.mask.toUShort(), v) }
    var auto_Slow: Boolean
        get() = BitFlag_Util.isFlagSet(autoStatus_Flags2, AutoStatusFlags2.Slow.mask.toUShort())
        set(v) { autoStatus_Flags2 = BitFlag_Util.setFlag(autoStatus_Flags2, AutoStatusFlags2.Slow.mask.toUShort(), v) }

    // ---- Auto-status flag accessors (group 3) ----
    var auto_Scan: Boolean
        get() = BitFlag_Util.isFlagSet(autoStatus_Flags3, AutoStatusFlags3.Scan.mask.toUShort())
        set(v) { autoStatus_Flags3 = BitFlag_Util.setFlag(autoStatus_Flags3, AutoStatusFlags3.Scan.mask.toUShort(), v) }
    var auto_DistillPower: Boolean
        get() = BitFlag_Util.isFlagSet(autoStatus_Flags3, AutoStatusFlags3.DistillPower.mask.toUShort())
        set(v) { autoStatus_Flags3 = BitFlag_Util.setFlag(autoStatus_Flags3, AutoStatusFlags3.DistillPower.mask.toUShort(), v) }
    var auto_DistillMana: Boolean
        get() = BitFlag_Util.isFlagSet(autoStatus_Flags3, AutoStatusFlags3.DistillMana.mask.toUShort())
        set(v) { autoStatus_Flags3 = BitFlag_Util.setFlag(autoStatus_Flags3, AutoStatusFlags3.DistillMana.mask.toUShort(), v) }
    var auto_DistillSpeed: Boolean
        get() = BitFlag_Util.isFlagSet(autoStatus_Flags3, AutoStatusFlags3.DistillSpeed.mask.toUShort())
        set(v) { autoStatus_Flags3 = BitFlag_Util.setFlag(autoStatus_Flags3, AutoStatusFlags3.DistillSpeed.mask.toUShort(), v) }
    var auto_Unused3_04: Boolean
        get() = BitFlag_Util.isFlagSet(autoStatus_Flags3, AutoStatusFlags3.Unused3_04.mask.toUShort())
        set(v) { autoStatus_Flags3 = BitFlag_Util.setFlag(autoStatus_Flags3, AutoStatusFlags3.Unused3_04.mask.toUShort(), v) }
    var auto_DistillAbility: Boolean
        get() = BitFlag_Util.isFlagSet(autoStatus_Flags3, AutoStatusFlags3.DistillAbility.mask.toUShort())
        set(v) { autoStatus_Flags3 = BitFlag_Util.setFlag(autoStatus_Flags3, AutoStatusFlags3.DistillAbility.mask.toUShort(), v) }
    var auto_Shield: Boolean
        get() = BitFlag_Util.isFlagSet(autoStatus_Flags3, AutoStatusFlags3.Shield.mask.toUShort())
        set(v) { autoStatus_Flags3 = BitFlag_Util.setFlag(autoStatus_Flags3, AutoStatusFlags3.Shield.mask.toUShort(), v) }
    var auto_Boost: Boolean
        get() = BitFlag_Util.isFlagSet(autoStatus_Flags3, AutoStatusFlags3.Boost.mask.toUShort())
        set(v) { autoStatus_Flags3 = BitFlag_Util.setFlag(autoStatus_Flags3, AutoStatusFlags3.Boost.mask.toUShort(), v) }
    var auto_Eject: Boolean
        get() = BitFlag_Util.isFlagSet(autoStatus_Flags3, AutoStatusFlags3.Eject.mask.toUShort())
        set(v) { autoStatus_Flags3 = BitFlag_Util.setFlag(autoStatus_Flags3, AutoStatusFlags3.Eject.mask.toUShort(), v) }
    var auto_AutoLife: Boolean
        get() = BitFlag_Util.isFlagSet(autoStatus_Flags3, AutoStatusFlags3.AutoLife.mask.toUShort())
        set(v) { autoStatus_Flags3 = BitFlag_Util.setFlag(autoStatus_Flags3, AutoStatusFlags3.AutoLife.mask.toUShort(), v) }
    var auto_Curse: Boolean
        get() = BitFlag_Util.isFlagSet(autoStatus_Flags3, AutoStatusFlags3.Curse.mask.toUShort())
        set(v) { autoStatus_Flags3 = BitFlag_Util.setFlag(autoStatus_Flags3, AutoStatusFlags3.Curse.mask.toUShort(), v) }
    var auto_Defend: Boolean
        get() = BitFlag_Util.isFlagSet(autoStatus_Flags3, AutoStatusFlags3.Defend.mask.toUShort())
        set(v) { autoStatus_Flags3 = BitFlag_Util.setFlag(autoStatus_Flags3, AutoStatusFlags3.Defend.mask.toUShort(), v) }
    var auto_Guard: Boolean
        get() = BitFlag_Util.isFlagSet(autoStatus_Flags3, AutoStatusFlags3.Guard.mask.toUShort())
        set(v) { autoStatus_Flags3 = BitFlag_Util.setFlag(autoStatus_Flags3, AutoStatusFlags3.Guard.mask.toUShort(), v) }
    var auto_Sentinel: Boolean
        get() = BitFlag_Util.isFlagSet(autoStatus_Flags3, AutoStatusFlags3.Sentinel.mask.toUShort())
        set(v) { autoStatus_Flags3 = BitFlag_Util.setFlag(autoStatus_Flags3, AutoStatusFlags3.Sentinel.mask.toUShort(), v) }
    var auto_Doom: Boolean
        get() = BitFlag_Util.isFlagSet(autoStatus_Flags3, AutoStatusFlags3.Doom.mask.toUShort())
        set(v) { autoStatus_Flags3 = BitFlag_Util.setFlag(autoStatus_Flags3, AutoStatusFlags3.Doom.mask.toUShort(), v) }

    // ---- Extra immunity flag accessors ----
    var immunity_Scan: Boolean
        get() = BitFlag_Util.isFlagSet(extraImmunities_Flags, ExtraImmunitiesFlags.Scan.mask.toUShort())
        set(v) { extraImmunities_Flags = BitFlag_Util.setFlag(extraImmunities_Flags, ExtraImmunitiesFlags.Scan.mask.toUShort(), v) }
    var immunity_DistillPower: Boolean
        get() = BitFlag_Util.isFlagSet(extraImmunities_Flags, ExtraImmunitiesFlags.DistillPower.mask.toUShort())
        set(v) { extraImmunities_Flags = BitFlag_Util.setFlag(extraImmunities_Flags, ExtraImmunitiesFlags.DistillPower.mask.toUShort(), v) }
    var immunity_DistillMana: Boolean
        get() = BitFlag_Util.isFlagSet(extraImmunities_Flags, ExtraImmunitiesFlags.DistillMana.mask.toUShort())
        set(v) { extraImmunities_Flags = BitFlag_Util.setFlag(extraImmunities_Flags, ExtraImmunitiesFlags.DistillMana.mask.toUShort(), v) }
    var immunity_DistillSpeed: Boolean
        get() = BitFlag_Util.isFlagSet(extraImmunities_Flags, ExtraImmunitiesFlags.DistillSpeed.mask.toUShort())
        set(v) { extraImmunities_Flags = BitFlag_Util.setFlag(extraImmunities_Flags, ExtraImmunitiesFlags.DistillSpeed.mask.toUShort(), v) }
    var immunity_Unused3_04: Boolean
        get() = BitFlag_Util.isFlagSet(extraImmunities_Flags, ExtraImmunitiesFlags.Unused3_04.mask.toUShort())
        set(v) { extraImmunities_Flags = BitFlag_Util.setFlag(extraImmunities_Flags, ExtraImmunitiesFlags.Unused3_04.mask.toUShort(), v) }
    var immunity_DistillAbility: Boolean
        get() = BitFlag_Util.isFlagSet(extraImmunities_Flags, ExtraImmunitiesFlags.DistillAbility.mask.toUShort())
        set(v) { extraImmunities_Flags = BitFlag_Util.setFlag(extraImmunities_Flags, ExtraImmunitiesFlags.DistillAbility.mask.toUShort(), v) }
    var immunity_Shield: Boolean
        get() = BitFlag_Util.isFlagSet(extraImmunities_Flags, ExtraImmunitiesFlags.Shield.mask.toUShort())
        set(v) { extraImmunities_Flags = BitFlag_Util.setFlag(extraImmunities_Flags, ExtraImmunitiesFlags.Shield.mask.toUShort(), v) }
    var immunity_Boost: Boolean
        get() = BitFlag_Util.isFlagSet(extraImmunities_Flags, ExtraImmunitiesFlags.Boost.mask.toUShort())
        set(v) { extraImmunities_Flags = BitFlag_Util.setFlag(extraImmunities_Flags, ExtraImmunitiesFlags.Boost.mask.toUShort(), v) }
    var immunity_Eject: Boolean
        get() = BitFlag_Util.isFlagSet(extraImmunities_Flags, ExtraImmunitiesFlags.Eject.mask.toUShort())
        set(v) { extraImmunities_Flags = BitFlag_Util.setFlag(extraImmunities_Flags, ExtraImmunitiesFlags.Eject.mask.toUShort(), v) }
    var immunity_AutoLife: Boolean
        get() = BitFlag_Util.isFlagSet(extraImmunities_Flags, ExtraImmunitiesFlags.AutoLife.mask.toUShort())
        set(v) { extraImmunities_Flags = BitFlag_Util.setFlag(extraImmunities_Flags, ExtraImmunitiesFlags.AutoLife.mask.toUShort(), v) }
    var immunity_Curse: Boolean
        get() = BitFlag_Util.isFlagSet(extraImmunities_Flags, ExtraImmunitiesFlags.Curse.mask.toUShort())
        set(v) { extraImmunities_Flags = BitFlag_Util.setFlag(extraImmunities_Flags, ExtraImmunitiesFlags.Curse.mask.toUShort(), v) }
    var immunity_Defend: Boolean
        get() = BitFlag_Util.isFlagSet(extraImmunities_Flags, ExtraImmunitiesFlags.Defend.mask.toUShort())
        set(v) { extraImmunities_Flags = BitFlag_Util.setFlag(extraImmunities_Flags, ExtraImmunitiesFlags.Defend.mask.toUShort(), v) }
    var immunity_Guard: Boolean
        get() = BitFlag_Util.isFlagSet(extraImmunities_Flags, ExtraImmunitiesFlags.Guard.mask.toUShort())
        set(v) { extraImmunities_Flags = BitFlag_Util.setFlag(extraImmunities_Flags, ExtraImmunitiesFlags.Guard.mask.toUShort(), v) }
    var immunity_Sentinel: Boolean
        get() = BitFlag_Util.isFlagSet(extraImmunities_Flags, ExtraImmunitiesFlags.Sentinel.mask.toUShort())
        set(v) { extraImmunities_Flags = BitFlag_Util.setFlag(extraImmunities_Flags, ExtraImmunitiesFlags.Sentinel.mask.toUShort(), v) }
    var immunity_Doom: Boolean
        get() = BitFlag_Util.isFlagSet(extraImmunities_Flags, ExtraImmunitiesFlags.Doom.mask.toUShort())
        set(v) { extraImmunities_Flags = BitFlag_Util.setFlag(extraImmunities_Flags, ExtraImmunitiesFlags.Doom.mask.toUShort(), v) }

    companion object {
        /**
         * Decode a stat-sheet sub-file (struct + trailing text blob) into a
         * [Monster_StatSheet] populated with both the binary stats and the
         * decoded script bytes for each text entry.
         */
        fun readSingle(byteFile: ByteArray): Monster_StatSheet {
            val buffer = ByteBuffer.wrap(byteFile).order(ByteOrder.LITTLE_ENDIAN)
            val statSheetStruct = BinaryMapping.read<MonsterStatSheetStruct>(buffer)

            val remaining = byteFile.size - buffer.position()
            val textFile = ByteArray(remaining)
            buffer.get(textFile)

            val statSheet = statSheetStruct.statSheet
            statSheet.nameScriptBytes =
                FfxEncoding.getScriptBytesFromTextFile(textFile, statSheetStruct.nameTSInfo.offset.toInt())
            statSheet.sensorScriptBytes =
                FfxEncoding.getScriptBytesFromTextFile(textFile, statSheetStruct.sensorTSInfo.offset.toInt())
            statSheet.unusedText1ScriptBytes =
                FfxEncoding.getScriptBytesFromTextFile(textFile, statSheetStruct.unusedText1TSInfo.offset.toInt())
            statSheet.scanScriptBytes =
                FfxEncoding.getScriptBytesFromTextFile(textFile, statSheetStruct.scanTSInfo.offset.toInt())
            statSheet.unusedText2ScriptBytes =
                FfxEncoding.getScriptBytesFromTextFile(textFile, statSheetStruct.unusedText2TSInfo.offset.toInt())

            statSheet.nameScriptId = statSheetStruct.nameTSInfo.scriptId
            statSheet.sensorScriptId = statSheetStruct.sensorTSInfo.scriptId
            statSheet.unusedText1ScriptId = statSheetStruct.unusedText1TSInfo.scriptId
            statSheet.scanScriptId = statSheetStruct.scanTSInfo.scriptId
            statSheet.unusedText2ScriptId = statSheetStruct.unusedText2TSInfo.scriptId

            statSheet.preservedNameOffset = statSheetStruct.nameTSInfo.offset
            statSheet.preservedSensorOffset = statSheetStruct.sensorTSInfo.offset
            statSheet.preservedUnusedText1Offset = statSheetStruct.unusedText1TSInfo.offset
            statSheet.preservedScanOffset = statSheetStruct.scanTSInfo.offset
            statSheet.preservedUnusedText2Offset = statSheetStruct.unusedText2TSInfo.offset
            statSheet.preservedTextBlob = textFile

            return statSheet
        }

        /**
         * Returns the captured trailing blob if [statSheet] still resolves the
         * same script bytes via the recorded offsets. Returning null forces a
         * full rebuild on write.
         */
        private fun canReusePreservedBlob(statSheet: Monster_StatSheet): ByteArray? {
            val blob = statSheet.preservedTextBlob ?: return null
            val n = statSheet.preservedNameOffset ?: return null
            val s = statSheet.preservedSensorOffset ?: return null
            val u1 = statSheet.preservedUnusedText1Offset ?: return null
            val sc = statSheet.preservedScanOffset ?: return null
            val u2 = statSheet.preservedUnusedText2Offset ?: return null
            if (!statSheet.nameScriptBytes.contentEquals(
                    FfxEncoding.getScriptBytesFromTextFile(blob, n.toInt()))) return null
            if (!statSheet.sensorScriptBytes.contentEquals(
                    FfxEncoding.getScriptBytesFromTextFile(blob, s.toInt()))) return null
            if (!statSheet.unusedText1ScriptBytes.contentEquals(
                    FfxEncoding.getScriptBytesFromTextFile(blob, u1.toInt()))) return null
            if (!statSheet.scanScriptBytes.contentEquals(
                    FfxEncoding.getScriptBytesFromTextFile(blob, sc.toInt()))) return null
            if (!statSheet.unusedText2ScriptBytes.contentEquals(
                    FfxEncoding.getScriptBytesFromTextFile(blob, u2.toInt()))) return null
            return blob
        }
    }

    /**
     * Encode this stat-sheet (and its associated script bytes) into the on-disk
     * sub-file layout: the [MonsterStatSheetStruct] header followed by the
     * concatenated text blob.
     */
    fun writeSingle(): ByteArray {
        val statSheetStruct = MonsterStatSheetStruct()
        statSheetStruct.statSheet = this

        val preservedBlob = canReusePreservedBlob(this)
        if (preservedBlob != null) {
            statSheetStruct.nameTSInfo.offset = preservedNameOffset!!
            statSheetStruct.nameTSInfo.scriptId = nameScriptId
            statSheetStruct.sensorTSInfo.offset = preservedSensorOffset!!
            statSheetStruct.sensorTSInfo.scriptId = sensorScriptId
            statSheetStruct.unusedText1TSInfo.offset = preservedUnusedText1Offset!!
            statSheetStruct.unusedText1TSInfo.scriptId = unusedText1ScriptId
            statSheetStruct.scanTSInfo.offset = preservedScanOffset!!
            statSheetStruct.scanTSInfo.scriptId = scanScriptId
            statSheetStruct.unusedText2TSInfo.offset = preservedUnusedText2Offset!!
            statSheetStruct.unusedText2TSInfo.scriptId = unusedText2ScriptId
            return BinaryMapping.toByteArray(statSheetStruct, sizeHint = 512) + preservedBlob
        }

        // Build the trailing text file, recording each entry's offset/id back
        // into the wrapping struct.
        var textFile = ByteArray(0)

        statSheetStruct.nameTSInfo.offset = textFile.size.toUShort()
        statSheetStruct.nameTSInfo.scriptId = nameScriptId
        textFile = FfxEncoding.writeBytesIntoTextFile(textFile, nameScriptBytes)

        statSheetStruct.sensorTSInfo.offset = textFile.size.toUShort()
        statSheetStruct.sensorTSInfo.scriptId = sensorScriptId
        textFile = FfxEncoding.writeBytesIntoTextFile(textFile, sensorScriptBytes)

        statSheetStruct.unusedText1TSInfo.offset = textFile.size.toUShort()
        statSheetStruct.unusedText1TSInfo.scriptId = unusedText1ScriptId
        textFile = FfxEncoding.writeBytesIntoTextFile(textFile, unusedText1ScriptBytes)

        statSheetStruct.scanTSInfo.offset = textFile.size.toUShort()
        statSheetStruct.scanTSInfo.scriptId = scanScriptId
        textFile = FfxEncoding.writeBytesIntoTextFile(textFile, scanScriptBytes)

        statSheetStruct.unusedText2TSInfo.offset = textFile.size.toUShort()
        statSheetStruct.unusedText2TSInfo.scriptId = unusedText2ScriptId
        textFile = FfxEncoding.writeBytesIntoTextFile(textFile, unusedText2ScriptBytes)

        val structBytes = BinaryMapping.toByteArray(statSheetStruct, sizeHint = 512)
        return structBytes + textFile
    }
}
