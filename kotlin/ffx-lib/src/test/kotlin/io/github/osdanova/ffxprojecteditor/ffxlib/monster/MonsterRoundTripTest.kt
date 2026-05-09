package io.github.osdanova.ffxprojecteditor.ffxlib.monster

import io.github.osdanova.ffxprojecteditor.test.Fixtures
import org.junit.jupiter.api.Assumptions.assumeTrue
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Synthetic round-trip tests construct an in-memory struct, serialize it,
 * deserialize the bytes, and assert field-level equality. Real-binary
 * round-trip tests at the bottom resolve fixtures via [Fixtures] (env var
 * FFX_FIXTURES_DIR) and skip when the binaries aren't present.
 */
class MonsterRoundTripTest {

    @Test
    fun statSheetRoundTripsSyntheticData() {
        val src = Monster_StatSheet().apply {
            hp = 12345u
            mp = 67u
            hpOverkill = 9999u
            strength = 88u
            defense = 77u
            magic = 66u
            magicDefense = 55u
            agility = 44u
            luck = 33u
            evasion = 22u
            accuracy = 11u
            poisonDamage = 99u
            forcedAction = 0xABCDu
            monsterId = 42
            modelId = 100
            ctbIconId = 5u
            doomCount = -3
            arenaId = 10
            arenaIdPadding = 0u
            model2Id = 200
            // Flag accessors round-trip through the raw UShort
            prop_Armored = true
            prop_ImmunityPhysicalDamage = true
            auto_Haste = true
            auto_AutoLife = true
            immunity_Scan = true
            // Set a couple of abilities
            abilities[0] = 0x3001u
            abilities[15] = 0x3010u
            // Text script bytes (script id + bytes round-trip via the wrapper)
            nameScriptBytes = byteArrayOf(0x50, 0x51, 0x52)
            sensorScriptBytes = byteArrayOf(0x60)
            unusedText1ScriptBytes = ByteArray(0)
            scanScriptBytes = byteArrayOf(0x70, 0x71)
            unusedText2ScriptBytes = ByteArray(0)
            nameScriptId = 1u
            sensorScriptId = 2u
            scanScriptId = 3u
        }

        val bytes = src.writeSingle()
        val back = Monster_StatSheet.readSingle(bytes)

        assertEquals(src.hp, back.hp)
        assertEquals(src.mp, back.mp)
        assertEquals(src.hpOverkill, back.hpOverkill)
        assertEquals(src.strength, back.strength)
        assertEquals(src.luck, back.luck)
        assertEquals(src.poisonDamage, back.poisonDamage)
        assertEquals(src.forcedAction, back.forcedAction)
        assertEquals(src.monsterId, back.monsterId)
        assertEquals(src.modelId, back.modelId)
        assertEquals(src.doomCount, back.doomCount)
        assertEquals(src.arenaId, back.arenaId)
        assertEquals(src.model2Id, back.model2Id)

        // Boolean accessors survive the round trip
        assertTrue(back.prop_Armored)
        assertTrue(back.prop_ImmunityPhysicalDamage)
        assertTrue(back.auto_Haste)
        assertTrue(back.auto_AutoLife)
        assertTrue(back.immunity_Scan)

        // Abilities
        assertContentEquals(src.abilities, back.abilities)

        // Text scripts come back via the same offset/scriptId pairs
        assertContentEquals(src.nameScriptBytes, back.nameScriptBytes)
        assertContentEquals(src.sensorScriptBytes, back.sensorScriptBytes)
        assertContentEquals(src.scanScriptBytes, back.scanScriptBytes)
        assertEquals(src.nameScriptId, back.nameScriptId)
        assertEquals(src.sensorScriptId, back.sensorScriptId)
        assertEquals(src.scanScriptId, back.scanScriptId)

        // Re-emitting the round-tripped struct should be byte-identical
        assertContentEquals(bytes, back.writeSingle())
    }

    @Test
    fun lootRoundTripsSyntheticData() {
        val src = Monster_Loot().apply {
            gil = 1234
            ap = 56
            apOverkill = 78
            ronsoRageId = 9u
            drop1Chance = 200u
            drop2Chance = 100u
            stealChance = 250u
            gearChance = 50u
            drop1Id = 0x2001u
            drop1RareId = 0x2002u
            drop2Id = 0x2003u
            drop2RareId = 0x2004u
            drop1Count = 1u
            drop1RareCount = 1u
            drop2Count = 2u
            drop2RareCount = 2u
            stealId = 0x2005u
            stealRareId = 0x2006u
            stealCount = 3u
            stealRareCount = 3u
            bribeId = 0x2007u
            bribeCount = 99u
            gearSlotCount = 4u
            gearAttack = 100u
            gearAbilityCount = 3u
            tidusAbilities.weaponAbilities[0] = 0x8001u
            tidusAbilities.armorAbilities[7] = 0x8008u
            yunaAbilities.weaponAbilities[3] = 0x9003u
            zanmatoLevel = 5u
        }

        val bytes = src.writeSingle()
        val back = Monster_Loot.readSingle(bytes)

        assertEquals(src.gil, back.gil)
        assertEquals(src.ap, back.ap)
        assertEquals(src.apOverkill, back.apOverkill)
        assertEquals(src.ronsoRageId, back.ronsoRageId)
        assertEquals(src.drop1Chance, back.drop1Chance)
        assertEquals(src.drop2Chance, back.drop2Chance)
        assertEquals(src.stealChance, back.stealChance)
        assertEquals(src.bribeId, back.bribeId)
        assertEquals(src.gearSlotCount, back.gearSlotCount)
        assertEquals(src.zanmatoLevel, back.zanmatoLevel)
        assertContentEquals(src.tidusAbilities.weaponAbilities, back.tidusAbilities.weaponAbilities)
        assertContentEquals(src.tidusAbilities.armorAbilities, back.tidusAbilities.armorAbilities)
        assertContentEquals(src.yunaAbilities.weaponAbilities, back.yunaAbilities.weaponAbilities)
        assertContentEquals(bytes, back.writeSingle())
    }

    @Test
    fun monsterFileRoundTripsSyntheticSections() {
        val src = Monster_File().apply {
            aiFile = byteArrayOf(0x10, 0x11, 0x12, 0x13)
            workerFile = byteArrayOf(0x20, 0x21)
            statSheetFile = Monster_StatSheet().apply {
                hp = 100u
                monsterId = 1
            }
            unkFile = byteArrayOf(0x30, 0x31, 0x32)
            lootFile = Monster_Loot().apply {
                gil = 42
                drop1Id = 0x2001u
            }
            audioFile = byteArrayOf(0x40, 0x41)
            textFile = byteArrayOf(0x50, 0x51, 0x52, 0x53, 0x54)
        }

        val bytes = src.write()
        val back = Monster_File.read(bytes)

        assertContentEquals(src.aiFile, back.aiFile)
        assertContentEquals(src.workerFile, back.workerFile)
        assertNotNull(back.statSheetFile)
        assertEquals(src.statSheetFile!!.hp, back.statSheetFile!!.hp)
        assertEquals(src.statSheetFile!!.monsterId, back.statSheetFile!!.monsterId)
        assertContentEquals(src.unkFile, back.unkFile)
        assertNotNull(back.lootFile)
        assertEquals(src.lootFile!!.gil, back.lootFile!!.gil)
        assertEquals(src.lootFile!!.drop1Id, back.lootFile!!.drop1Id)
        assertContentEquals(src.audioFile, back.audioFile)
        assertContentEquals(src.textFile, back.textFile)
    }

    /**
     * Some monster fixtures store the loot section without the historical
     * 3-byte trailing padding (the surrounding file lays out the next
     * sub-file immediately after `Unk3`). A strict 3-byte read used to
     * crash with BufferUnderflowException; ensure we now read back cleanly
     * and the rewrite preserves the original size.
     */
    @Test
    fun lootHandlesShorterTrailingPaddingWithoutUnderflow() {
        val full = Monster_Loot().apply { gil = 7; bribeId = 0x55u; zanmatoLevel = 1u }.writeSingle()
        assertEquals(0x116 + 3, full.size, "default writeSingle should still emit 0x119 bytes")

        // Trim the 3 trailing bytes the way a real fixture might.
        val trimmed = full.copyOfRange(0, Monster_Loot.FIXED_SIZE)
        val parsed = Monster_Loot.readSingle(trimmed)
        assertEquals(7.toShort(), parsed.gil)
        assertEquals(0x55u.toUShort(), parsed.bribeId)
        assertEquals(1u.toUByte(), parsed.zanmatoLevel)
        assertEquals(0, parsed.padding.size)
        assertContentEquals(trimmed, parsed.writeSingle(),
            "round-trip of a no-padding loot section must preserve its size")

        // And a partial-padding case (2 bytes) round-trips identically too.
        val partial = full.copyOfRange(0, Monster_Loot.FIXED_SIZE + 2)
        val parsedPartial = Monster_Loot.readSingle(partial)
        assertEquals(2, parsedPartial.padding.size)
        assertContentEquals(partial, parsedPartial.writeSingle())
    }

    /**
     * Real-binary round trip: read an actual m###.bin from the user's
     * extracted master folder and re-emit it. Skipped when fixtures aren't
     * available (CI / fresh checkouts).
     */
    @Test
    fun realMonsterFileRoundTripsByteForByte() {
        assumeTrue(Fixtures.isAvailable(), "FFX_FIXTURES_DIR not set")
        val candidates = (1..50).mapNotNull { id ->
            val padded = id.toString().padStart(3, '0')
            Fixtures.bytesOrNull("jppc/battle/mon/_m$padded/m$padded.bin")
        }
        assumeTrue(candidates.isNotEmpty(), "no monster fixtures under FFX_FIXTURES_DIR/jppc/battle/mon")

        for (original in candidates.take(5)) {
            val parsed = Monster_File.read(original)
            val rewritten = parsed.write()
            assertContentEquals(
                original, rewritten,
                "monster file did not round-trip byte-for-byte (size=${original.size})"
            )
        }
    }
}
