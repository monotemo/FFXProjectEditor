package io.github.osdanova.ffxprojecteditor.ffxlib.ability

import io.github.osdanova.ffxprojecteditor.binary.BinField
import io.github.osdanova.ffxprojecteditor.binary.BinaryMapping
import io.github.osdanova.ffxprojecteditor.ffxlib.common.EntryListFile
import io.github.osdanova.ffxprojecteditor.ffxlib.common.StatusByteList
import io.github.osdanova.ffxprojecteditor.ffxlib.common.StatusDurationByteList
import io.github.osdanova.ffxprojecteditor.ffxlib.encoding.FfxEncoding
import io.github.osdanova.ffxprojecteditor.util.BitFlag_Util
import java.nio.ByteBuffer
import java.nio.ByteOrder

// region Flag enums (top-level — Kotlin has no `[Flags]` equivalent, masks are stored in `mask`)

enum class MenuFlags(val mask: Int) {
    MainMenu(0x01),
    OpenCommandMenu(0x08),
    OpenSpecialMenu(0x10),
}

enum class TargetFlags(val mask: Int) {
    Enabled(0x01),
    Enemies(0x02),
    Multi(0x04),
    SelfOnly(0x08),
    Unk10(0x10),
    EitherTeam(0x20),
    Dead(0x40),
    LongRange(0x80),
}

enum class Misc1Flags(val mask: Int) {
    UseOutsideCombat(0x01),
    UseInCombat(0x02),
    DisplayMoveName(0x04),
    AffectedByDarkness(0x40),
    AffectedByReflect(0x80),
}

enum class Misc2Flags(val mask: Int) {
    AbsorbDamage(0x01),
    StealItem(0x02),
    MenuUse(0x04),
    MenuRight(0x08),
    MenuLeft(0x10),
    DelayS(0x20),
    DelayL(0x40),
    RandomTargets(0x80),
}

enum class Misc3Flags(val mask: Int) {
    Piercing(0x01),
    AffectedBySilence(0x02),
    UseWeaponProps(0x04),
    TriggerCommand(0x08),
    CastAnimS(0x10),
    CastAnimL(0x20),
    DestroyCaster(0x40),
    MissToAlive(0x80),
}

enum class Misc4Flags(val mask: Int) {
    ChargeWarriorHealer(0x01),
    EmptyOverdrive(0x02),
    ShowSpellcastAura(0x04),
    RunOffScreen(0x08),
    CopycatEnabled(0x10),
    Unk20(0x20),
    AeonOverdrive(0x40),
    Bribe(0x80),
}

enum class DamageFlags(val mask: Int) {
    Physical(0x01),
    Magical(0x02),
    CanCrit(0x04),
    GearCritBonus(0x08),
    Heals(0x10),
    CleansesStatuses(0x20),
    SupressBreakDamageLimit(0x40),
    BreaksDamageLimit(0x80),
}

enum class PreviewFlags(val mask: Int) {
    Active(0x01),
    HealMp(0x02),
    HealStatuses(0x04),
    IsMap(0x08),
    IsRenameCard(0x10),
    IsSphere(0x20),
    HealHp(0x40),
    IsRenameCard2(0x80),
}

enum class DamageTypeFlags(val mask: Int) {
    Hp(0x01),
    Mp(0x02),
    Ctb(0x04),
}

enum class ElementFlags(val mask: Int) {
    Fire(0x01),
    Blizzard(0x02),
    Thunder(0x04),
    Water(0x08),
    Holy(0x10),
}

enum class StatusFlags(val mask: Int) {
    Scan(0x01),
    DistillPower(0x02),
    DistillMana(0x04),
    DistillSpeed(0x08),
    DistillUnused(0x10),
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

enum class StatBuffFlags(val mask: Int) {
    Cheer(0x01),
    Aim(0x02),
    Focus(0x04),
    Reflex(0x08),
    Luck(0x10),
    Jinx(0x20),
}

enum class SpecialBuffFlags(val mask: Int) {
    DoubleHp(0x01),
    DoubleMp(0x02),
    MpCost0(0x04),
    Quartet(0x08),
    AlwaysCrit(0x10),
    Overdrive150(0x20),
    Overdrive200(0x40),
}

// endregion

/** Sub-byte enum stored in bits 3..5 of [Ability_Command.misc1Flgs]. */
enum class HitCalcType(val value: Int) {
    Always(0),
    AttackAccuracy(1),
    AttackAccuracy_2(2),
    Accuracy(3),
    Accuracy_2(4),
    Accuracy25(5),
    Accuracy15(6),
    Accuracy05(7);

    companion object {
        fun fromValue(value: Int): HitCalcType? = entries.firstOrNull { it.value == value }
    }
}

/**
 * Optional 4-byte trailer present on some Ability_Command payloads
 * (e.g. command / item files), absent on monster-magic.
 */
class ExtraCommandInfo {
    @BinField var orderingIndexInMenu: UByte = 0u

    /** sbyte in C# — keep as signed Byte. */
    @BinField var sphereTypeForSphereGrid: Byte = 0
    @BinField var unk1: UByte = 0u
    @BinField var unk2: UByte = 0u
}

/**
 * Length: 0x5C without [extraInfo], 0x60 with.
 *
 * - Command file: has ExtraInfo
 * - Item file:    has ExtraInfo
 * - MonMagic:     no ExtraInfo
 */
class Ability_Command {
    @BinField var anim1Id: Short = 0
    @BinField var anim2Id: Short = 0
    @BinField var iconId: UByte = 0u
    @BinField var casterAnimId: UByte = 0u

    /** [MenuFlags] mask. */
    @BinField var menuFlgs: UByte = 0u
    @BinField var subSubMenuCategorization: UByte = 0u
    @BinField var subMenuCategorization: UByte = 0u

    /** [Character_Enum] (sbyte; -1 = None). */
    @BinField var characterUser: Byte = -1

    /** [TargetFlags] mask. */
    @BinField var targetFlgs: UByte = 0u
    @BinField var targetsAllowed: UByte = 0u

    /** [Misc1Flags] mask + [HitCalcType] in bits 3..5. */
    @BinField var misc1Flgs: UByte = 0u

    /** [Misc2Flags] mask. */
    @BinField var misc2Flgs: UByte = 0u

    /** [Misc3Flags] mask. */
    @BinField var misc3Flgs: UByte = 0u

    /** [Misc4Flags] mask. */
    @BinField var misc4Flgs: UByte = 0u

    /** [DamageFlags] mask. */
    @BinField var damageFlgs: UByte = 0u
    @BinField var stealGil: Boolean = false

    /** [PreviewFlags] mask. */
    @BinField var previewFlgs: UByte = 0u

    /** [DamageTypeFlags] mask. */
    @BinField var damageTypeFlgs: UByte = 0u
    @BinField var moveRank: UByte = 0u
    @BinField var costMp: UByte = 0u
    @BinField var costOverdrive: UByte = 0u
    @BinField var attackCritBonus: UByte = 0u

    /** [DamageFormula_Enum]. */
    @BinField var damageFormula: UByte = 0u
    @BinField var attackAccuracy: UByte = 0u
    @BinField var attackPower: UByte = 0u
    @BinField var hitCount: UByte = 0u
    @BinField var shatterChance: UByte = 0u

    /** [ElementFlags] mask. */
    @BinField var elementFlgs: UByte = 0u
    @BinField var statusChance: StatusByteList = StatusByteList()
    @BinField var statusDuration: StatusDurationByteList = StatusDurationByteList()

    /** [StatusFlags] mask (ushort). */
    @BinField var statusFlgs: UShort = 0u

    /** [StatBuffFlags] mask (ushort). */
    @BinField var statBuffFlgs: UShort = 0u
    @BinField var overdriveCategory: UByte = 0u
    @BinField var statBuffValue: UByte = 0u

    /** [SpecialBuffFlags] mask (ushort). */
    @BinField var specialBuffFlgs: UShort = 0u

    // --- Non-serialized companions (kept on the command for convenience) ---

    var extraInfo: ExtraCommandInfo? = null
    var nameScriptBytes: ByteArray = ByteArray(0)
    var unusedText1ScriptBytes: ByteArray = ByteArray(0)
    var descriptionScriptBytes: ByteArray = ByteArray(0)
    var unusedText2ScriptBytes: ByteArray = ByteArray(0)

    // Text ids kept to preserve original data. Ideally these would be calculated.
    var nameScriptId: UShort = 0u
    var unusedText1ScriptId: UShort = 0u
    var descriptionScriptId: UShort = 0u
    var unusedText2ScriptId: UShort = 0u

    // region Flag accessors

    // -- MenuFlags
    var flagMenuMainMenu: Boolean
        get() = BitFlag_Util.isFlagSet(menuFlgs, MenuFlags.MainMenu.mask.toUByte())
        set(v) { menuFlgs = BitFlag_Util.setFlag(menuFlgs, MenuFlags.MainMenu.mask.toUByte(), v) }
    var flagMenuOpenCommandMenu: Boolean
        get() = BitFlag_Util.isFlagSet(menuFlgs, MenuFlags.OpenCommandMenu.mask.toUByte())
        set(v) { menuFlgs = BitFlag_Util.setFlag(menuFlgs, MenuFlags.OpenCommandMenu.mask.toUByte(), v) }
    var flagMenuOpenSpecialMenu: Boolean
        get() = BitFlag_Util.isFlagSet(menuFlgs, MenuFlags.OpenSpecialMenu.mask.toUByte())
        set(v) { menuFlgs = BitFlag_Util.setFlag(menuFlgs, MenuFlags.OpenSpecialMenu.mask.toUByte(), v) }

    // -- TargetFlags
    var flagTargetEnabled: Boolean
        get() = BitFlag_Util.isFlagSet(targetFlgs, TargetFlags.Enabled.mask.toUByte())
        set(v) { targetFlgs = BitFlag_Util.setFlag(targetFlgs, TargetFlags.Enabled.mask.toUByte(), v) }
    var flagTargetEnemies: Boolean
        get() = BitFlag_Util.isFlagSet(targetFlgs, TargetFlags.Enemies.mask.toUByte())
        set(v) { targetFlgs = BitFlag_Util.setFlag(targetFlgs, TargetFlags.Enemies.mask.toUByte(), v) }
    var flagTargetMulti: Boolean
        get() = BitFlag_Util.isFlagSet(targetFlgs, TargetFlags.Multi.mask.toUByte())
        set(v) { targetFlgs = BitFlag_Util.setFlag(targetFlgs, TargetFlags.Multi.mask.toUByte(), v) }
    var flagTargetSelfOnly: Boolean
        get() = BitFlag_Util.isFlagSet(targetFlgs, TargetFlags.SelfOnly.mask.toUByte())
        set(v) { targetFlgs = BitFlag_Util.setFlag(targetFlgs, TargetFlags.SelfOnly.mask.toUByte(), v) }
    var flagTargetUnk10: Boolean
        get() = BitFlag_Util.isFlagSet(targetFlgs, TargetFlags.Unk10.mask.toUByte())
        set(v) { targetFlgs = BitFlag_Util.setFlag(targetFlgs, TargetFlags.Unk10.mask.toUByte(), v) }
    var flagTargetEitherTeam: Boolean
        get() = BitFlag_Util.isFlagSet(targetFlgs, TargetFlags.EitherTeam.mask.toUByte())
        set(v) { targetFlgs = BitFlag_Util.setFlag(targetFlgs, TargetFlags.EitherTeam.mask.toUByte(), v) }
    var flagTargetDead: Boolean
        get() = BitFlag_Util.isFlagSet(targetFlgs, TargetFlags.Dead.mask.toUByte())
        set(v) { targetFlgs = BitFlag_Util.setFlag(targetFlgs, TargetFlags.Dead.mask.toUByte(), v) }
    var flagTargetLongRange: Boolean
        get() = BitFlag_Util.isFlagSet(targetFlgs, TargetFlags.LongRange.mask.toUByte())
        set(v) { targetFlgs = BitFlag_Util.setFlag(targetFlgs, TargetFlags.LongRange.mask.toUByte(), v) }

    // -- Misc1Flags
    var flagMisc1UseOutsideCombat: Boolean
        get() = BitFlag_Util.isFlagSet(misc1Flgs, Misc1Flags.UseOutsideCombat.mask.toUByte())
        set(v) { misc1Flgs = BitFlag_Util.setFlag(misc1Flgs, Misc1Flags.UseOutsideCombat.mask.toUByte(), v) }
    var flagMisc1UseInCombat: Boolean
        get() = BitFlag_Util.isFlagSet(misc1Flgs, Misc1Flags.UseInCombat.mask.toUByte())
        set(v) { misc1Flgs = BitFlag_Util.setFlag(misc1Flgs, Misc1Flags.UseInCombat.mask.toUByte(), v) }
    var flagMisc1DisplayMoveName: Boolean
        get() = BitFlag_Util.isFlagSet(misc1Flgs, Misc1Flags.DisplayMoveName.mask.toUByte())
        set(v) { misc1Flgs = BitFlag_Util.setFlag(misc1Flgs, Misc1Flags.DisplayMoveName.mask.toUByte(), v) }
    var flagMisc1AffectedByDarkness: Boolean
        get() = BitFlag_Util.isFlagSet(misc1Flgs, Misc1Flags.AffectedByDarkness.mask.toUByte())
        set(v) { misc1Flgs = BitFlag_Util.setFlag(misc1Flgs, Misc1Flags.AffectedByDarkness.mask.toUByte(), v) }
    var flagMisc1AffectedByReflect: Boolean
        get() = BitFlag_Util.isFlagSet(misc1Flgs, Misc1Flags.AffectedByReflect.mask.toUByte())
        set(v) { misc1Flgs = BitFlag_Util.setFlag(misc1Flgs, Misc1Flags.AffectedByReflect.mask.toUByte(), v) }

    /** Bits 3..5 of [misc1Flgs] encode a [HitCalcType]. */
    var flagMisc1HitCalcType: HitCalcType
        get() = HitCalcType.fromValue((misc1Flgs.toInt() shr 3) and 0b111) ?: HitCalcType.Always
        set(v) {
            val masked = misc1Flgs.toInt() and 0b11000111
            misc1Flgs = (masked or ((v.value and 0b111) shl 3)).toUByte()
        }

    // -- Misc2Flags
    var flagMisc2AbsorbDamage: Boolean
        get() = BitFlag_Util.isFlagSet(misc2Flgs, Misc2Flags.AbsorbDamage.mask.toUByte())
        set(v) { misc2Flgs = BitFlag_Util.setFlag(misc2Flgs, Misc2Flags.AbsorbDamage.mask.toUByte(), v) }
    var flagMisc2StealItem: Boolean
        get() = BitFlag_Util.isFlagSet(misc2Flgs, Misc2Flags.StealItem.mask.toUByte())
        set(v) { misc2Flgs = BitFlag_Util.setFlag(misc2Flgs, Misc2Flags.StealItem.mask.toUByte(), v) }
    var flagMisc2MenuUse: Boolean
        get() = BitFlag_Util.isFlagSet(misc2Flgs, Misc2Flags.MenuUse.mask.toUByte())
        set(v) { misc2Flgs = BitFlag_Util.setFlag(misc2Flgs, Misc2Flags.MenuUse.mask.toUByte(), v) }
    var flagMisc2MenuRight: Boolean
        get() = BitFlag_Util.isFlagSet(misc2Flgs, Misc2Flags.MenuRight.mask.toUByte())
        set(v) { misc2Flgs = BitFlag_Util.setFlag(misc2Flgs, Misc2Flags.MenuRight.mask.toUByte(), v) }
    var flagMisc2MenuLeft: Boolean
        get() = BitFlag_Util.isFlagSet(misc2Flgs, Misc2Flags.MenuLeft.mask.toUByte())
        set(v) { misc2Flgs = BitFlag_Util.setFlag(misc2Flgs, Misc2Flags.MenuLeft.mask.toUByte(), v) }
    var flagMisc2DelayS: Boolean
        get() = BitFlag_Util.isFlagSet(misc2Flgs, Misc2Flags.DelayS.mask.toUByte())
        set(v) { misc2Flgs = BitFlag_Util.setFlag(misc2Flgs, Misc2Flags.DelayS.mask.toUByte(), v) }
    var flagMisc2DelayL: Boolean
        get() = BitFlag_Util.isFlagSet(misc2Flgs, Misc2Flags.DelayL.mask.toUByte())
        set(v) { misc2Flgs = BitFlag_Util.setFlag(misc2Flgs, Misc2Flags.DelayL.mask.toUByte(), v) }
    var flagMisc2RandomTargets: Boolean
        get() = BitFlag_Util.isFlagSet(misc2Flgs, Misc2Flags.RandomTargets.mask.toUByte())
        set(v) { misc2Flgs = BitFlag_Util.setFlag(misc2Flgs, Misc2Flags.RandomTargets.mask.toUByte(), v) }

    // -- Misc3Flags
    var flagMisc3Piercing: Boolean
        get() = BitFlag_Util.isFlagSet(misc3Flgs, Misc3Flags.Piercing.mask.toUByte())
        set(v) { misc3Flgs = BitFlag_Util.setFlag(misc3Flgs, Misc3Flags.Piercing.mask.toUByte(), v) }
    var flagMisc3AffectedBySilence: Boolean
        get() = BitFlag_Util.isFlagSet(misc3Flgs, Misc3Flags.AffectedBySilence.mask.toUByte())
        set(v) { misc3Flgs = BitFlag_Util.setFlag(misc3Flgs, Misc3Flags.AffectedBySilence.mask.toUByte(), v) }
    var flagMisc3UseWeaponProps: Boolean
        get() = BitFlag_Util.isFlagSet(misc3Flgs, Misc3Flags.UseWeaponProps.mask.toUByte())
        set(v) { misc3Flgs = BitFlag_Util.setFlag(misc3Flgs, Misc3Flags.UseWeaponProps.mask.toUByte(), v) }
    var flagMisc3TriggerCommand: Boolean
        get() = BitFlag_Util.isFlagSet(misc3Flgs, Misc3Flags.TriggerCommand.mask.toUByte())
        set(v) { misc3Flgs = BitFlag_Util.setFlag(misc3Flgs, Misc3Flags.TriggerCommand.mask.toUByte(), v) }
    var flagMisc3CastAnimS: Boolean
        get() = BitFlag_Util.isFlagSet(misc3Flgs, Misc3Flags.CastAnimS.mask.toUByte())
        set(v) { misc3Flgs = BitFlag_Util.setFlag(misc3Flgs, Misc3Flags.CastAnimS.mask.toUByte(), v) }
    var flagMisc3CastAnimL: Boolean
        get() = BitFlag_Util.isFlagSet(misc3Flgs, Misc3Flags.CastAnimL.mask.toUByte())
        set(v) { misc3Flgs = BitFlag_Util.setFlag(misc3Flgs, Misc3Flags.CastAnimL.mask.toUByte(), v) }
    var flagMisc3DestroyCaster: Boolean
        get() = BitFlag_Util.isFlagSet(misc3Flgs, Misc3Flags.DestroyCaster.mask.toUByte())
        set(v) { misc3Flgs = BitFlag_Util.setFlag(misc3Flgs, Misc3Flags.DestroyCaster.mask.toUByte(), v) }
    var flagMisc3MissToAlive: Boolean
        get() = BitFlag_Util.isFlagSet(misc3Flgs, Misc3Flags.MissToAlive.mask.toUByte())
        set(v) { misc3Flgs = BitFlag_Util.setFlag(misc3Flgs, Misc3Flags.MissToAlive.mask.toUByte(), v) }

    // -- Misc4Flags
    var flagMisc4ChargeWarriorHealer: Boolean
        get() = BitFlag_Util.isFlagSet(misc4Flgs, Misc4Flags.ChargeWarriorHealer.mask.toUByte())
        set(v) { misc4Flgs = BitFlag_Util.setFlag(misc4Flgs, Misc4Flags.ChargeWarriorHealer.mask.toUByte(), v) }
    var flagMisc4EmptyOverdrive: Boolean
        get() = BitFlag_Util.isFlagSet(misc4Flgs, Misc4Flags.EmptyOverdrive.mask.toUByte())
        set(v) { misc4Flgs = BitFlag_Util.setFlag(misc4Flgs, Misc4Flags.EmptyOverdrive.mask.toUByte(), v) }
    var flagMisc4ShowSpellcastAura: Boolean
        get() = BitFlag_Util.isFlagSet(misc4Flgs, Misc4Flags.ShowSpellcastAura.mask.toUByte())
        set(v) { misc4Flgs = BitFlag_Util.setFlag(misc4Flgs, Misc4Flags.ShowSpellcastAura.mask.toUByte(), v) }
    var flagMisc4RunOffScreen: Boolean
        get() = BitFlag_Util.isFlagSet(misc4Flgs, Misc4Flags.RunOffScreen.mask.toUByte())
        set(v) { misc4Flgs = BitFlag_Util.setFlag(misc4Flgs, Misc4Flags.RunOffScreen.mask.toUByte(), v) }
    var flagMisc4CopycatEnabled: Boolean
        get() = BitFlag_Util.isFlagSet(misc4Flgs, Misc4Flags.CopycatEnabled.mask.toUByte())
        set(v) { misc4Flgs = BitFlag_Util.setFlag(misc4Flgs, Misc4Flags.CopycatEnabled.mask.toUByte(), v) }
    var flagMisc4Unk20: Boolean
        get() = BitFlag_Util.isFlagSet(misc4Flgs, Misc4Flags.Unk20.mask.toUByte())
        set(v) { misc4Flgs = BitFlag_Util.setFlag(misc4Flgs, Misc4Flags.Unk20.mask.toUByte(), v) }
    var flagMisc4AeonOverdrive: Boolean
        get() = BitFlag_Util.isFlagSet(misc4Flgs, Misc4Flags.AeonOverdrive.mask.toUByte())
        set(v) { misc4Flgs = BitFlag_Util.setFlag(misc4Flgs, Misc4Flags.AeonOverdrive.mask.toUByte(), v) }
    var flagMisc4Bribe: Boolean
        get() = BitFlag_Util.isFlagSet(misc4Flgs, Misc4Flags.Bribe.mask.toUByte())
        set(v) { misc4Flgs = BitFlag_Util.setFlag(misc4Flgs, Misc4Flags.Bribe.mask.toUByte(), v) }

    // -- DamageFlags
    var flagDamagePhysical: Boolean
        get() = BitFlag_Util.isFlagSet(damageFlgs, DamageFlags.Physical.mask.toUByte())
        set(v) { damageFlgs = BitFlag_Util.setFlag(damageFlgs, DamageFlags.Physical.mask.toUByte(), v) }
    var flagDamageMagical: Boolean
        get() = BitFlag_Util.isFlagSet(damageFlgs, DamageFlags.Magical.mask.toUByte())
        set(v) { damageFlgs = BitFlag_Util.setFlag(damageFlgs, DamageFlags.Magical.mask.toUByte(), v) }
    var flagDamageCanCrit: Boolean
        get() = BitFlag_Util.isFlagSet(damageFlgs, DamageFlags.CanCrit.mask.toUByte())
        set(v) { damageFlgs = BitFlag_Util.setFlag(damageFlgs, DamageFlags.CanCrit.mask.toUByte(), v) }
    var flagDamageGearCritBonus: Boolean
        get() = BitFlag_Util.isFlagSet(damageFlgs, DamageFlags.GearCritBonus.mask.toUByte())
        set(v) { damageFlgs = BitFlag_Util.setFlag(damageFlgs, DamageFlags.GearCritBonus.mask.toUByte(), v) }
    var flagDamageHeals: Boolean
        get() = BitFlag_Util.isFlagSet(damageFlgs, DamageFlags.Heals.mask.toUByte())
        set(v) { damageFlgs = BitFlag_Util.setFlag(damageFlgs, DamageFlags.Heals.mask.toUByte(), v) }
    var flagDamageCleansesStatuses: Boolean
        get() = BitFlag_Util.isFlagSet(damageFlgs, DamageFlags.CleansesStatuses.mask.toUByte())
        set(v) { damageFlgs = BitFlag_Util.setFlag(damageFlgs, DamageFlags.CleansesStatuses.mask.toUByte(), v) }
    var flagDamageSupressBreakDamageLimit: Boolean
        get() = BitFlag_Util.isFlagSet(damageFlgs, DamageFlags.SupressBreakDamageLimit.mask.toUByte())
        set(v) { damageFlgs = BitFlag_Util.setFlag(damageFlgs, DamageFlags.SupressBreakDamageLimit.mask.toUByte(), v) }
    var flagDamageBreaksDamageLimit: Boolean
        get() = BitFlag_Util.isFlagSet(damageFlgs, DamageFlags.BreaksDamageLimit.mask.toUByte())
        set(v) { damageFlgs = BitFlag_Util.setFlag(damageFlgs, DamageFlags.BreaksDamageLimit.mask.toUByte(), v) }

    // -- PreviewFlags
    var flagPreviewActive: Boolean
        get() = BitFlag_Util.isFlagSet(previewFlgs, PreviewFlags.Active.mask.toUByte())
        set(v) { previewFlgs = BitFlag_Util.setFlag(previewFlgs, PreviewFlags.Active.mask.toUByte(), v) }
    var flagPreviewHealMp: Boolean
        get() = BitFlag_Util.isFlagSet(previewFlgs, PreviewFlags.HealMp.mask.toUByte())
        set(v) { previewFlgs = BitFlag_Util.setFlag(previewFlgs, PreviewFlags.HealMp.mask.toUByte(), v) }
    var flagPreviewHealStatuses: Boolean
        get() = BitFlag_Util.isFlagSet(previewFlgs, PreviewFlags.HealStatuses.mask.toUByte())
        set(v) { previewFlgs = BitFlag_Util.setFlag(previewFlgs, PreviewFlags.HealStatuses.mask.toUByte(), v) }
    var flagPreviewIsMap: Boolean
        get() = BitFlag_Util.isFlagSet(previewFlgs, PreviewFlags.IsMap.mask.toUByte())
        set(v) { previewFlgs = BitFlag_Util.setFlag(previewFlgs, PreviewFlags.IsMap.mask.toUByte(), v) }
    var flagPreviewIsRenameCard: Boolean
        get() = BitFlag_Util.isFlagSet(previewFlgs, PreviewFlags.IsRenameCard.mask.toUByte())
        set(v) { previewFlgs = BitFlag_Util.setFlag(previewFlgs, PreviewFlags.IsRenameCard.mask.toUByte(), v) }
    var flagPreviewIsSphere: Boolean
        get() = BitFlag_Util.isFlagSet(previewFlgs, PreviewFlags.IsSphere.mask.toUByte())
        set(v) { previewFlgs = BitFlag_Util.setFlag(previewFlgs, PreviewFlags.IsSphere.mask.toUByte(), v) }
    var flagPreviewHealHp: Boolean
        get() = BitFlag_Util.isFlagSet(previewFlgs, PreviewFlags.HealHp.mask.toUByte())
        set(v) { previewFlgs = BitFlag_Util.setFlag(previewFlgs, PreviewFlags.HealHp.mask.toUByte(), v) }
    var flagPreviewIsRenameCard2: Boolean
        get() = BitFlag_Util.isFlagSet(previewFlgs, PreviewFlags.IsRenameCard2.mask.toUByte())
        set(v) { previewFlgs = BitFlag_Util.setFlag(previewFlgs, PreviewFlags.IsRenameCard2.mask.toUByte(), v) }

    // -- StatusFlags (ushort)
    var flagStatusScan: Boolean
        get() = BitFlag_Util.isFlagSet(statusFlgs, StatusFlags.Scan.mask.toUShort())
        set(v) { statusFlgs = BitFlag_Util.setFlag(statusFlgs, StatusFlags.Scan.mask.toUShort(), v) }
    var flagStatusDistillPower: Boolean
        get() = BitFlag_Util.isFlagSet(statusFlgs, StatusFlags.DistillPower.mask.toUShort())
        set(v) { statusFlgs = BitFlag_Util.setFlag(statusFlgs, StatusFlags.DistillPower.mask.toUShort(), v) }
    var flagStatusDistillMana: Boolean
        get() = BitFlag_Util.isFlagSet(statusFlgs, StatusFlags.DistillMana.mask.toUShort())
        set(v) { statusFlgs = BitFlag_Util.setFlag(statusFlgs, StatusFlags.DistillMana.mask.toUShort(), v) }
    var flagStatusDistillSpeed: Boolean
        get() = BitFlag_Util.isFlagSet(statusFlgs, StatusFlags.DistillSpeed.mask.toUShort())
        set(v) { statusFlgs = BitFlag_Util.setFlag(statusFlgs, StatusFlags.DistillSpeed.mask.toUShort(), v) }
    var flagStatusDistillUnused: Boolean
        get() = BitFlag_Util.isFlagSet(statusFlgs, StatusFlags.DistillUnused.mask.toUShort())
        set(v) { statusFlgs = BitFlag_Util.setFlag(statusFlgs, StatusFlags.DistillUnused.mask.toUShort(), v) }
    var flagStatusDistillAbility: Boolean
        get() = BitFlag_Util.isFlagSet(statusFlgs, StatusFlags.DistillAbility.mask.toUShort())
        set(v) { statusFlgs = BitFlag_Util.setFlag(statusFlgs, StatusFlags.DistillAbility.mask.toUShort(), v) }
    var flagStatusShield: Boolean
        get() = BitFlag_Util.isFlagSet(statusFlgs, StatusFlags.Shield.mask.toUShort())
        set(v) { statusFlgs = BitFlag_Util.setFlag(statusFlgs, StatusFlags.Shield.mask.toUShort(), v) }
    var flagStatusBoost: Boolean
        get() = BitFlag_Util.isFlagSet(statusFlgs, StatusFlags.Boost.mask.toUShort())
        set(v) { statusFlgs = BitFlag_Util.setFlag(statusFlgs, StatusFlags.Boost.mask.toUShort(), v) }
    var flagStatusEject: Boolean
        get() = BitFlag_Util.isFlagSet(statusFlgs, StatusFlags.Eject.mask.toUShort())
        set(v) { statusFlgs = BitFlag_Util.setFlag(statusFlgs, StatusFlags.Eject.mask.toUShort(), v) }
    var flagStatusAutoLife: Boolean
        get() = BitFlag_Util.isFlagSet(statusFlgs, StatusFlags.AutoLife.mask.toUShort())
        set(v) { statusFlgs = BitFlag_Util.setFlag(statusFlgs, StatusFlags.AutoLife.mask.toUShort(), v) }
    var flagStatusCurse: Boolean
        get() = BitFlag_Util.isFlagSet(statusFlgs, StatusFlags.Curse.mask.toUShort())
        set(v) { statusFlgs = BitFlag_Util.setFlag(statusFlgs, StatusFlags.Curse.mask.toUShort(), v) }
    var flagStatusDefend: Boolean
        get() = BitFlag_Util.isFlagSet(statusFlgs, StatusFlags.Defend.mask.toUShort())
        set(v) { statusFlgs = BitFlag_Util.setFlag(statusFlgs, StatusFlags.Defend.mask.toUShort(), v) }
    var flagStatusGuard: Boolean
        get() = BitFlag_Util.isFlagSet(statusFlgs, StatusFlags.Guard.mask.toUShort())
        set(v) { statusFlgs = BitFlag_Util.setFlag(statusFlgs, StatusFlags.Guard.mask.toUShort(), v) }
    var flagStatusSentinel: Boolean
        get() = BitFlag_Util.isFlagSet(statusFlgs, StatusFlags.Sentinel.mask.toUShort())
        set(v) { statusFlgs = BitFlag_Util.setFlag(statusFlgs, StatusFlags.Sentinel.mask.toUShort(), v) }
    var flagStatusDoom: Boolean
        get() = BitFlag_Util.isFlagSet(statusFlgs, StatusFlags.Doom.mask.toUShort())
        set(v) { statusFlgs = BitFlag_Util.setFlag(statusFlgs, StatusFlags.Doom.mask.toUShort(), v) }

    // -- StatBuffFlags (ushort)
    var flagStatBuffCheer: Boolean
        get() = BitFlag_Util.isFlagSet(statBuffFlgs, StatBuffFlags.Cheer.mask.toUShort())
        set(v) { statBuffFlgs = BitFlag_Util.setFlag(statBuffFlgs, StatBuffFlags.Cheer.mask.toUShort(), v) }
    var flagStatBuffAim: Boolean
        get() = BitFlag_Util.isFlagSet(statBuffFlgs, StatBuffFlags.Aim.mask.toUShort())
        set(v) { statBuffFlgs = BitFlag_Util.setFlag(statBuffFlgs, StatBuffFlags.Aim.mask.toUShort(), v) }
    var flagStatBuffFocus: Boolean
        get() = BitFlag_Util.isFlagSet(statBuffFlgs, StatBuffFlags.Focus.mask.toUShort())
        set(v) { statBuffFlgs = BitFlag_Util.setFlag(statBuffFlgs, StatBuffFlags.Focus.mask.toUShort(), v) }
    var flagStatBuffReflex: Boolean
        get() = BitFlag_Util.isFlagSet(statBuffFlgs, StatBuffFlags.Reflex.mask.toUShort())
        set(v) { statBuffFlgs = BitFlag_Util.setFlag(statBuffFlgs, StatBuffFlags.Reflex.mask.toUShort(), v) }
    var flagStatBuffLuck: Boolean
        get() = BitFlag_Util.isFlagSet(statBuffFlgs, StatBuffFlags.Luck.mask.toUShort())
        set(v) { statBuffFlgs = BitFlag_Util.setFlag(statBuffFlgs, StatBuffFlags.Luck.mask.toUShort(), v) }
    var flagStatBuffJinx: Boolean
        get() = BitFlag_Util.isFlagSet(statBuffFlgs, StatBuffFlags.Jinx.mask.toUShort())
        set(v) { statBuffFlgs = BitFlag_Util.setFlag(statBuffFlgs, StatBuffFlags.Jinx.mask.toUShort(), v) }

    // -- SpecialBuffFlags (ushort)
    var flagSpecialBuffDoubleHp: Boolean
        get() = BitFlag_Util.isFlagSet(specialBuffFlgs, SpecialBuffFlags.DoubleHp.mask.toUShort())
        set(v) { specialBuffFlgs = BitFlag_Util.setFlag(specialBuffFlgs, SpecialBuffFlags.DoubleHp.mask.toUShort(), v) }
    var flagSpecialBuffDoubleMp: Boolean
        get() = BitFlag_Util.isFlagSet(specialBuffFlgs, SpecialBuffFlags.DoubleMp.mask.toUShort())
        set(v) { specialBuffFlgs = BitFlag_Util.setFlag(specialBuffFlgs, SpecialBuffFlags.DoubleMp.mask.toUShort(), v) }
    var flagSpecialBuffMpCost0: Boolean
        get() = BitFlag_Util.isFlagSet(specialBuffFlgs, SpecialBuffFlags.MpCost0.mask.toUShort())
        set(v) { specialBuffFlgs = BitFlag_Util.setFlag(specialBuffFlgs, SpecialBuffFlags.MpCost0.mask.toUShort(), v) }
    var flagSpecialBuffQuartet: Boolean
        get() = BitFlag_Util.isFlagSet(specialBuffFlgs, SpecialBuffFlags.Quartet.mask.toUShort())
        set(v) { specialBuffFlgs = BitFlag_Util.setFlag(specialBuffFlgs, SpecialBuffFlags.Quartet.mask.toUShort(), v) }
    var flagSpecialBuffAlwaysCrit: Boolean
        get() = BitFlag_Util.isFlagSet(specialBuffFlgs, SpecialBuffFlags.AlwaysCrit.mask.toUShort())
        set(v) { specialBuffFlgs = BitFlag_Util.setFlag(specialBuffFlgs, SpecialBuffFlags.AlwaysCrit.mask.toUShort(), v) }
    var flagSpecialBuffOverdrive150: Boolean
        get() = BitFlag_Util.isFlagSet(specialBuffFlgs, SpecialBuffFlags.Overdrive150.mask.toUShort())
        set(v) { specialBuffFlgs = BitFlag_Util.setFlag(specialBuffFlgs, SpecialBuffFlags.Overdrive150.mask.toUShort(), v) }
    var flagSpecialBuffOverdrive200: Boolean
        get() = BitFlag_Util.isFlagSet(specialBuffFlgs, SpecialBuffFlags.Overdrive200.mask.toUShort())
        set(v) { specialBuffFlgs = BitFlag_Util.setFlag(specialBuffFlgs, SpecialBuffFlags.Overdrive200.mask.toUShort(), v) }

    // -- DamageTypeFlags
    var flagDamageTypeHp: Boolean
        get() = BitFlag_Util.isFlagSet(damageTypeFlgs, DamageTypeFlags.Hp.mask.toUByte())
        set(v) { damageTypeFlgs = BitFlag_Util.setFlag(damageTypeFlgs, DamageTypeFlags.Hp.mask.toUByte(), v) }
    var flagDamageTypeMp: Boolean
        get() = BitFlag_Util.isFlagSet(damageTypeFlgs, DamageTypeFlags.Mp.mask.toUByte())
        set(v) { damageTypeFlgs = BitFlag_Util.setFlag(damageTypeFlgs, DamageTypeFlags.Mp.mask.toUByte(), v) }
    var flagDamageTypeCtb: Boolean
        get() = BitFlag_Util.isFlagSet(damageTypeFlgs, DamageTypeFlags.Ctb.mask.toUByte())
        set(v) { damageTypeFlgs = BitFlag_Util.setFlag(damageTypeFlgs, DamageTypeFlags.Ctb.mask.toUByte(), v) }

    // -- ElementFlags
    var flagElementFire: Boolean
        get() = BitFlag_Util.isFlagSet(elementFlgs, ElementFlags.Fire.mask.toUByte())
        set(v) { elementFlgs = BitFlag_Util.setFlag(elementFlgs, ElementFlags.Fire.mask.toUByte(), v) }
    var flagElementBlizzard: Boolean
        get() = BitFlag_Util.isFlagSet(elementFlgs, ElementFlags.Blizzard.mask.toUByte())
        set(v) { elementFlgs = BitFlag_Util.setFlag(elementFlgs, ElementFlags.Blizzard.mask.toUByte(), v) }
    var flagElementThunder: Boolean
        get() = BitFlag_Util.isFlagSet(elementFlgs, ElementFlags.Thunder.mask.toUByte())
        set(v) { elementFlgs = BitFlag_Util.setFlag(elementFlgs, ElementFlags.Thunder.mask.toUByte(), v) }
    var flagElementWater: Boolean
        get() = BitFlag_Util.isFlagSet(elementFlgs, ElementFlags.Water.mask.toUByte())
        set(v) { elementFlgs = BitFlag_Util.setFlag(elementFlgs, ElementFlags.Water.mask.toUByte(), v) }
    var flagElementHoly: Boolean
        get() = BitFlag_Util.isFlagSet(elementFlgs, ElementFlags.Holy.mask.toUByte())
        set(v) { elementFlgs = BitFlag_Util.setFlag(elementFlgs, ElementFlags.Holy.mask.toUByte(), v) }

    // endregion

    // region IO

    /** Serialize this single command + its trailing text bytes. */
    fun writeSingle(hasExtraInfo: Boolean): ByteArray {
        val commandStruct = Ability_CommandStruct().apply { abilityInfo = this@Ability_Command }

        // Text File
        var textFile = ByteArray(0)

        commandStruct.nameTSInfo.offset = textFile.size.toUShort()
        commandStruct.nameTSInfo.scriptId = nameScriptId
        textFile = FfxEncoding.writeBytesIntoTextFile(textFile, nameScriptBytes)

        commandStruct.unusedText1TSInfo.offset = textFile.size.toUShort()
        commandStruct.unusedText1TSInfo.scriptId = unusedText1ScriptId
        textFile = FfxEncoding.writeBytesIntoTextFile(textFile, unusedText1ScriptBytes)

        commandStruct.descriptionTSInfo.offset = textFile.size.toUShort()
        commandStruct.descriptionTSInfo.scriptId = descriptionScriptId
        textFile = FfxEncoding.writeBytesIntoTextFile(textFile, descriptionScriptBytes)

        commandStruct.unusedText2TSInfo.offset = textFile.size.toUShort()
        commandStruct.unusedText2TSInfo.scriptId = unusedText2ScriptId
        textFile = FfxEncoding.writeBytesIntoTextFile(textFile, unusedText2ScriptBytes)

        val structBytes = BinaryMapping.toByteArray(commandStruct)
        val extraBytes = if (hasExtraInfo) {
            BinaryMapping.toByteArray(extraInfo ?: ExtraCommandInfo())
        } else {
            ByteArray(0)
        }

        return structBytes + extraBytes + textFile
    }

    // endregion

    companion object {

        /** Parse a single Ability_Command + its trailing text bytes from a stand-alone byte file. */
        fun readSingle(byteFile: ByteArray, hasExtraInfo: Boolean): Ability_Command {
            val buffer = ByteBuffer.wrap(byteFile).order(ByteOrder.LITTLE_ENDIAN)
            val commandStruct = BinaryMapping.read<Ability_CommandStruct>(buffer)
            if (hasExtraInfo) {
                commandStruct.abilityInfo.extraInfo = BinaryMapping.read<ExtraCommandInfo>(buffer)
            }
            val textFile = ByteArray(byteFile.size - buffer.position())
            buffer.get(textFile)

            val command = commandStruct.abilityInfo
            command.nameScriptBytes = FfxEncoding.getScriptBytesFromTextFile(textFile, commandStruct.nameTSInfo.offset.toInt())
            command.unusedText1ScriptBytes = FfxEncoding.getScriptBytesFromTextFile(textFile, commandStruct.unusedText1TSInfo.offset.toInt())
            command.descriptionScriptBytes = FfxEncoding.getScriptBytesFromTextFile(textFile, commandStruct.descriptionTSInfo.offset.toInt())
            command.unusedText2ScriptBytes = FfxEncoding.getScriptBytesFromTextFile(textFile, commandStruct.unusedText2TSInfo.offset.toInt())
            command.nameScriptId = commandStruct.nameTSInfo.scriptId
            command.unusedText1ScriptId = commandStruct.unusedText1TSInfo.scriptId
            command.descriptionScriptId = commandStruct.descriptionTSInfo.scriptId
            command.unusedText2ScriptId = commandStruct.unusedText2TSInfo.scriptId
            return command
        }

        /** Parse all commands from an EntryListFile-wrapped byte file. */
        fun readList(byteFile: ByteArray, hasExtraInfo: Boolean): List<Ability_Command> {
            val listFile = EntryListFile.unpack(byteFile)
            val commandList = mutableListOf<Ability_Command>()
            val buffer = ByteBuffer.wrap(listFile.firstFile).order(ByteOrder.LITTLE_ENDIAN)
            val secondFile = listFile.secondFile ?: ByteArray(0)

            for (i in 0 until listFile.header.realEntryCount) {
                val commandStruct = BinaryMapping.read<Ability_CommandStruct>(buffer)
                if (hasExtraInfo) {
                    commandStruct.abilityInfo.extraInfo = BinaryMapping.read<ExtraCommandInfo>(buffer)
                }
                val command = commandStruct.abilityInfo
                command.nameScriptBytes = FfxEncoding.getScriptBytesFromTextFile(secondFile, commandStruct.nameTSInfo.offset.toInt())
                command.unusedText1ScriptBytes = FfxEncoding.getScriptBytesFromTextFile(secondFile, commandStruct.unusedText1TSInfo.offset.toInt())
                command.descriptionScriptBytes = FfxEncoding.getScriptBytesFromTextFile(secondFile, commandStruct.descriptionTSInfo.offset.toInt())
                command.unusedText2ScriptBytes = FfxEncoding.getScriptBytesFromTextFile(secondFile, commandStruct.unusedText2TSInfo.offset.toInt())
                command.nameScriptId = commandStruct.nameTSInfo.scriptId
                command.unusedText1ScriptId = commandStruct.unusedText1TSInfo.scriptId
                command.descriptionScriptId = commandStruct.descriptionTSInfo.scriptId
                command.unusedText2ScriptId = commandStruct.unusedText2TSInfo.scriptId
                commandList.add(command)
            }
            return commandList
        }

        /** Pack a list of commands into an EntryListFile byte array. */
        fun writeList(commandList: List<Ability_Command>, hasExtraInfo: Boolean): ByteArray {
            val commandStructList = mutableListOf<Ability_CommandStruct>()

            // Text File
            var textFile = ByteArray(0)
            for (command in commandList) {
                val commandStruct = Ability_CommandStruct().apply { abilityInfo = command }

                commandStruct.nameTSInfo.offset = textFile.size.toUShort()
                commandStruct.nameTSInfo.scriptId = command.nameScriptId
                textFile = FfxEncoding.writeBytesIntoTextFile(textFile, command.nameScriptBytes)

                commandStruct.unusedText1TSInfo.offset = textFile.size.toUShort()
                commandStruct.unusedText1TSInfo.scriptId = command.unusedText1ScriptId
                textFile = FfxEncoding.writeBytesIntoTextFile(textFile, command.unusedText1ScriptBytes)

                commandStruct.descriptionTSInfo.offset = textFile.size.toUShort()
                commandStruct.descriptionTSInfo.scriptId = command.descriptionScriptId
                textFile = FfxEncoding.writeBytesIntoTextFile(textFile, command.descriptionScriptBytes)

                commandStruct.unusedText2TSInfo.offset = textFile.size.toUShort()
                commandStruct.unusedText2TSInfo.scriptId = command.unusedText2ScriptId
                textFile = FfxEncoding.writeBytesIntoTextFile(textFile, command.unusedText2ScriptBytes)

                commandStructList.add(commandStruct)
            }

            var listFile = ByteArray(0)
            for (commandStruct in commandStructList) {
                listFile += BinaryMapping.toByteArray(commandStruct)
                if (hasExtraInfo) {
                    listFile += BinaryMapping.toByteArray(commandStruct.abilityInfo.extraInfo ?: ExtraCommandInfo())
                }
            }

            val entrySize: Short = if (hasExtraInfo) 0x60 else 0x5C
            return EntryListFile.pack(
                entrySize = entrySize,
                entryCount = commandList.size.toShort(),
                firstFile = listFile,
                secondFile = textFile,
            )
        }
    }
}
