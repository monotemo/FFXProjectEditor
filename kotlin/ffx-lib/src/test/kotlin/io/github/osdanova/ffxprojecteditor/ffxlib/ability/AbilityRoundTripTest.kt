package io.github.osdanova.ffxprojecteditor.ffxlib.ability

import io.github.osdanova.ffxprojecteditor.ffxlib.arm.Arms_Rate
import io.github.osdanova.ffxprojecteditor.test.Fixtures
import org.junit.jupiter.api.Assumptions.assumeTrue
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AbilityRoundTripTest {

    @Test
    fun commandRoundTripsSyntheticDataWithoutExtraInfo() {
        val src = newCommand(withExtra = false)
        val bytes = src.writeSingle(hasExtraInfo = false)
        val back = Ability_Command.readSingle(bytes, hasExtraInfo = false)
        assertCommandsEqual(src, back, expectExtra = false)
        assertContentEquals(bytes, back.writeSingle(hasExtraInfo = false))
    }

    @Test
    fun commandRoundTripsSyntheticDataWithExtraInfo() {
        val src = newCommand(withExtra = true)
        val bytes = src.writeSingle(hasExtraInfo = true)
        val back = Ability_Command.readSingle(bytes, hasExtraInfo = true)
        assertCommandsEqual(src, back, expectExtra = true)
        assertContentEquals(bytes, back.writeSingle(hasExtraInfo = true))
    }

    @Test
    fun commandListRoundTrips() {
        val list = listOf(newCommand(withExtra = true), newCommand(withExtra = true).apply {
            anim1Id = 99
            costMp = 50u
            nameScriptBytes = byteArrayOf(0x20, 0x21)
            nameScriptId = 99u
        })
        val bytes = Ability_Command.writeList(list, hasExtraInfo = true)
        val back = Ability_Command.readList(bytes, hasExtraInfo = true)
        assertEquals(list.size, back.size)
        assertCommandsEqual(list[0], back[0], expectExtra = true)
        assertCommandsEqual(list[1], back[1], expectExtra = true)
        assertContentEquals(bytes, Ability_Command.writeList(back, hasExtraInfo = true))
    }

    @Test
    fun hitCalcTypeBitFieldRoundTrips() {
        val cmd = Ability_Command()
        for (hct in HitCalcType.entries) {
            cmd.flagMisc1HitCalcType = hct
            assertEquals(hct, cmd.flagMisc1HitCalcType)
        }
        // The other bits in misc1Flgs must not be clobbered
        cmd.misc1Flgs = 0u
        cmd.flagMisc1UseInCombat = true
        cmd.flagMisc1HitCalcType = HitCalcType.Accuracy25
        assertTrue(cmd.flagMisc1UseInCombat, "low bit clobbered by HitCalcType setter")
        assertEquals(HitCalcType.Accuracy25, cmd.flagMisc1HitCalcType)
    }

    @Test
    fun armsRateListRoundTrips() {
        val rates = listOf(0, 100, 50, 25, 12, 6, -7, Int.MAX_VALUE, Int.MIN_VALUE)
        val bytes = Arms_Rate.writeList(rates)
        val back = Arms_Rate.readList(bytes)
        assertEquals(rates, back)
    }

    /**
     * Real-binary round trip: read the actual command.bin / item.bin from the
     * user's extracted master folder. Skipped when fixtures aren't available.
     */
    @Test
    fun realCommandFileRoundTripsByteForByte() {
        assumeTrue(Fixtures.isAvailable(), "FFX_FIXTURES_DIR not set")
        val candidates = listOf(
            "new_uspc/battle/kernel/command.bin" to true,
            "new_uspc/battle/kernel/item.bin" to true,
            "new_uspc/battle/kernel/monmagic1.bin" to false,
            "new_uspc/battle/kernel/monmagic2.bin" to false,
        ).mapNotNull { (rel, hasExtra) ->
            Fixtures.bytesOrNull(rel)?.let { Triple(rel, it, hasExtra) }
        }
        assumeTrue(candidates.isNotEmpty(), "no command fixtures under FFX_FIXTURES_DIR/new_uspc/battle/kernel")

        for ((rel, original, hasExtra) in candidates) {
            val parsed = Ability_Command.readList(original, hasExtra)
            val rewritten = Ability_Command.writeList(parsed, hasExtra)
            assertContentEquals(
                original, rewritten,
                "$rel did not round-trip byte-for-byte (size=${original.size})"
            )
        }
    }

    @Test
    fun realArmsRateRoundTripsByteForByte() {
        assumeTrue(Fixtures.isAvailable(), "FFX_FIXTURES_DIR not set")
        val original = Fixtures.bytesOrNull("jppc/battle/kernel/arms_rate.bin")
        assumeTrue(original != null, "arms_rate.bin not present in fixtures")
        val parsed = Arms_Rate.readList(original!!)
        val rewritten = Arms_Rate.writeList(parsed)
        assertContentEquals(original, rewritten, "arms_rate did not round-trip byte-for-byte")
    }

    private fun newCommand(withExtra: Boolean): Ability_Command = Ability_Command().apply {
        anim1Id = 0x1001
        anim2Id = 0x1002
        iconId = 5u
        casterAnimId = 7u
        menuFlgs = 0u
        flagMenuMainMenu = true
        flagMenuOpenSpecialMenu = true
        subSubMenuCategorization = 1u
        subMenuCategorization = 2u
        characterUser = 3
        targetFlgs = 0u
        flagTargetEnabled = true
        flagTargetEnemies = true
        flagTargetMulti = true
        targetsAllowed = 4u
        flagMisc1HitCalcType = HitCalcType.AttackAccuracy
        flagMisc1UseInCombat = true
        flagMisc2DelayS = true
        flagMisc3Piercing = true
        flagDamageTypeHp = true
        stealGil = false
        moveRank = 9u
        costMp = 10u
        costOverdrive = 0u
        attackCritBonus = 5u
        damageFormula = 1u
        attackAccuracy = 100u
        attackPower = 16u
        hitCount = 1u
        shatterChance = 0u
        flagElementFire = true
        flagElementHoly = true
        nameScriptBytes = byteArrayOf(0x50, 0x51, 0x52)
        unusedText1ScriptBytes = ByteArray(0)
        descriptionScriptBytes = byteArrayOf(0x60, 0x61)
        unusedText2ScriptBytes = ByteArray(0)
        nameScriptId = 1u
        descriptionScriptId = 2u
        if (withExtra) {
            extraInfo = ExtraCommandInfo().apply {
                orderingIndexInMenu = 7u
                sphereTypeForSphereGrid = -1
                unk1 = 0u
                unk2 = 0u
            }
        }
    }

    private fun assertCommandsEqual(src: Ability_Command, back: Ability_Command, expectExtra: Boolean) {
        assertEquals(src.anim1Id, back.anim1Id)
        assertEquals(src.anim2Id, back.anim2Id)
        assertEquals(src.iconId, back.iconId)
        assertEquals(src.casterAnimId, back.casterAnimId)
        assertEquals(src.menuFlgs, back.menuFlgs)
        assertEquals(src.subSubMenuCategorization, back.subSubMenuCategorization)
        assertEquals(src.subMenuCategorization, back.subMenuCategorization)
        assertEquals(src.characterUser, back.characterUser)
        assertEquals(src.targetFlgs, back.targetFlgs)
        assertEquals(src.targetsAllowed, back.targetsAllowed)
        assertEquals(src.misc1Flgs, back.misc1Flgs)
        assertEquals(src.misc2Flgs, back.misc2Flgs)
        assertEquals(src.misc3Flgs, back.misc3Flgs)
        assertEquals(src.misc4Flgs, back.misc4Flgs)
        assertEquals(src.damageFlgs, back.damageFlgs)
        assertEquals(src.stealGil, back.stealGil)
        assertEquals(src.previewFlgs, back.previewFlgs)
        assertEquals(src.damageTypeFlgs, back.damageTypeFlgs)
        assertEquals(src.moveRank, back.moveRank)
        assertEquals(src.costMp, back.costMp)
        assertEquals(src.costOverdrive, back.costOverdrive)
        assertEquals(src.attackCritBonus, back.attackCritBonus)
        assertEquals(src.damageFormula, back.damageFormula)
        assertEquals(src.attackAccuracy, back.attackAccuracy)
        assertEquals(src.attackPower, back.attackPower)
        assertEquals(src.hitCount, back.hitCount)
        assertEquals(src.shatterChance, back.shatterChance)
        assertEquals(src.elementFlgs, back.elementFlgs)
        assertEquals(src.flagMisc1HitCalcType, back.flagMisc1HitCalcType)
        assertEquals(src.flagMenuMainMenu, back.flagMenuMainMenu)
        assertEquals(src.flagTargetEnabled, back.flagTargetEnabled)
        assertEquals(src.flagElementFire, back.flagElementFire)
        assertContentEquals(src.nameScriptBytes, back.nameScriptBytes)
        assertContentEquals(src.descriptionScriptBytes, back.descriptionScriptBytes)
        assertEquals(src.nameScriptId, back.nameScriptId)
        assertEquals(src.descriptionScriptId, back.descriptionScriptId)
        if (expectExtra) {
            assertNotNull(back.extraInfo)
            assertEquals(src.extraInfo!!.orderingIndexInMenu, back.extraInfo!!.orderingIndexInMenu)
            assertEquals(src.extraInfo!!.sphereTypeForSphereGrid, back.extraInfo!!.sphereTypeForSphereGrid)
        }
    }
}
