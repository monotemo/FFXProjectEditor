package io.github.osdanova.ffxprojecteditor.ffxlib.monster

import io.github.osdanova.ffxprojecteditor.binary.BinField
import io.github.osdanova.ffxprojecteditor.binary.BinaryMapping

/**
 * Per-character bonus abilities granted by a piece of looted gear. Each side
 * (weapon / armor) holds 8 ability ids.
 */
class LootGearAbilities {
    @BinField(count = 8) var weaponAbilities: Array<UShort> = Array(8) { 0u }
    @BinField(count = 8) var armorAbilities: Array<UShort> = Array(8) { 0u }
}

/**
 * Spoils-of-war / loot data for a monster: gil/AP/drops/steals/bribe + a
 * randomly-rolled gear definition (one [LootGearAbilities] per playable
 * character).
 */
class Monster_Loot {
    @BinField var gil: Short = 0
    @BinField var ap: Short = 0
    @BinField var apOverkill: Short = 0
    @BinField var ronsoRageId: UShort = 0u

    // Drops
    @BinField var drop1Chance: UByte = 0u
    @BinField var drop2Chance: UByte = 0u
    @BinField var stealChance: UByte = 0u
    @BinField var gearChance: UByte = 0u

    @BinField var drop1Id: UShort = 0u
    @BinField var drop1RareId: UShort = 0u
    @BinField var drop2Id: UShort = 0u
    @BinField var drop2RareId: UShort = 0u
    @BinField var drop1Count: UByte = 0u
    @BinField var drop1RareCount: UByte = 0u
    @BinField var drop2Count: UByte = 0u
    @BinField var drop2RareCount: UByte = 0u

    // Overkills
    @BinField var dropOverkillId: UShort = 0u
    @BinField var dropOverkillRareId: UShort = 0u
    @BinField var dropOverkill2Id: UShort = 0u
    @BinField var dropOverkill2RareId: UShort = 0u
    @BinField var dropOverkillCount: UByte = 0u
    @BinField var dropOverkillRareCount: UByte = 0u
    @BinField var dropOverkill2Count: UByte = 0u
    @BinField var dropOverkill2RareCount: UByte = 0u

    // Steals
    @BinField var stealId: UShort = 0u
    @BinField var stealRareId: UShort = 0u
    @BinField var stealCount: UByte = 0u
    @BinField var stealRareCount: UByte = 0u
    @BinField var bribeId: UShort = 0u
    @BinField var bribeCount: UByte = 0u

    // Gear
    @BinField var gearSlotCount: UByte = 0u
    @BinField var gearFormula: UByte = 0u
    @BinField var gearCrit: UByte = 0u
    @BinField var gearAttack: UByte = 0u
    @BinField var gearAbilityCount: UByte = 0u
    @BinField var tidusAbilities: LootGearAbilities = LootGearAbilities()
    @BinField var yunaAbilities: LootGearAbilities = LootGearAbilities()
    @BinField var auronAbilities: LootGearAbilities = LootGearAbilities()
    @BinField var kimahriAbilities: LootGearAbilities = LootGearAbilities()
    @BinField var wakkaAbilities: LootGearAbilities = LootGearAbilities()
    @BinField var luluAbilities: LootGearAbilities = LootGearAbilities()
    @BinField var rikkuAbilities: LootGearAbilities = LootGearAbilities()

    // Extra
    @BinField var zanmatoLevel: UByte = 0u
    @BinField var unk1: UByte = 0u
    @BinField var unk2: UByte = 0u
    @BinField var unk3: UByte = 0u
    @BinField(count = 3) var padding: ByteArray = ByteArray(3)

    companion object {
        fun readSingle(byteFile: ByteArray): Monster_Loot =
            BinaryMapping.read(byteFile, Monster_Loot::class)
    }

    fun writeSingle(): ByteArray = BinaryMapping.toByteArray(this, sizeHint = 512)
}
