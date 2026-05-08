package io.github.osdanova.ffxprojecteditor.ffxlib.common

import io.github.osdanova.ffxprojecteditor.binary.BinField
import io.github.osdanova.ffxprojecteditor.util.BitFlag_Util

/** Weapon/armor flag bits. */
enum class WeaponFlags(val mask: Int) {
    IsSummon(0x01),
    IsHidden(0x02),
    IsCelestial(0x04),
    IsBrotherhood(0x08);
}

/** 0 = Weapon, 1 = Armor. */
enum class EquipmentType_Enum(val value: Int) {
    Weapon(0),
    Armor(1);

    companion object {
        fun fromValue(v: Int): EquipmentType_Enum? = entries.firstOrNull { it.value == v }
    }
}

/**
 * Equipment record. Size 0x16. Field offsets are explicit because the C# source
 * also used explicit `[Data(0xN)]` offsets.
 *
 * Enum-typed fields (Character, Type, CharacterEquipped, Dmg_formula) are stored
 * as raw [UByte] -- callers convert to/from the corresponding enum.
 */
class EquipmentStruct {
    @BinField(offset = 0x0)  var name_id: UShort = 0u
    @BinField(offset = 0x2)  var exists: Boolean = false
    @BinField(offset = 0x3)  var flags: UByte = 0u
    @BinField(offset = 0x4)  var character: UByte = 0u
    @BinField(offset = 0x5)  var type: UByte = 0u
    @BinField(offset = 0x6)  var characterEquipped: UByte = 0u
    @BinField(offset = 0x7)  var unk7: Byte = 0
    @BinField(offset = 0x8)  var dmg_formula: UByte = 0u
    @BinField(offset = 0x9)  var power: Byte = 0
    @BinField(offset = 0xA)  var crit_bonus: Byte = 0
    @BinField(offset = 0xB)  var slot_count: Byte = 0
    @BinField(offset = 0xC)  var model_id: UShort = 0u
    @BinField(offset = 0xE)  var ability1: UShort = 0u
    @BinField(offset = 0x10) var ability2: UShort = 0u
    @BinField(offset = 0x12) var ability3: UShort = 0u
    @BinField(offset = 0x14) var ability4: UShort = 0u

    var flagIsSummon: Boolean
        get() = BitFlag_Util.isFlagSet(flags, WeaponFlags.IsSummon.mask.toUByte())
        set(v) { flags = BitFlag_Util.setFlag(flags, WeaponFlags.IsSummon.mask.toUByte(), v) }
    var flagIsHidden: Boolean
        get() = BitFlag_Util.isFlagSet(flags, WeaponFlags.IsHidden.mask.toUByte())
        set(v) { flags = BitFlag_Util.setFlag(flags, WeaponFlags.IsHidden.mask.toUByte(), v) }
    var flagIsCelestial: Boolean
        get() = BitFlag_Util.isFlagSet(flags, WeaponFlags.IsCelestial.mask.toUByte())
        set(v) { flags = BitFlag_Util.setFlag(flags, WeaponFlags.IsCelestial.mask.toUByte(), v) }
    var flagIsBrotherhood: Boolean
        get() = BitFlag_Util.isFlagSet(flags, WeaponFlags.IsBrotherhood.mask.toUByte())
        set(v) { flags = BitFlag_Util.setFlag(flags, WeaponFlags.IsBrotherhood.mask.toUByte(), v) }
}
