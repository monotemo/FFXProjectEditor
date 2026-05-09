package io.github.osdanova.ffxprojecteditor.ffxlib.monster

import io.github.osdanova.ffxprojecteditor.binary.BinField
import io.github.osdanova.ffxprojecteditor.binary.BinaryMapping
import java.nio.ByteBuffer
import java.nio.ByteOrder

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

    // Trailing bytes that follow the fixed 0x116-byte struct on disk. The C#
    // source modeled this as a fixed `[Data(Count=3)] byte[]` after Unk3, but
    // some monster fixtures store fewer than 3 trailing bytes here — a strict
    // 3-byte read crashes with BufferUnderflowException, and a strict 3-byte
    // write would inflate the section and break round-trips. Treat it as a
    // variable-length tail so we can faithfully read and re-emit whatever the
    // file actually had. Default 3 zero bytes so freshly-constructed loot
    // structs match the historical 0x119-byte layout.
    var padding: ByteArray = ByteArray(3)

    companion object {
        /** Size of the fixed-layout body, before the variable trailing [padding]. */
        const val FIXED_SIZE: Int = 0x116

        fun readSingle(byteFile: ByteArray): Monster_Loot {
            val buffer = ByteBuffer.wrap(byteFile).order(ByteOrder.LITTLE_ENDIAN)
            val loot = BinaryMapping.read(buffer, Monster_Loot::class)
            val tail = byteFile.size - buffer.position()
            loot.padding = if (tail > 0) ByteArray(tail).also { buffer.get(it) } else ByteArray(0)
            return loot
        }
    }

    fun writeSingle(): ByteArray = BinaryMapping.toByteArray(this, sizeHint = 512) + padding
}
