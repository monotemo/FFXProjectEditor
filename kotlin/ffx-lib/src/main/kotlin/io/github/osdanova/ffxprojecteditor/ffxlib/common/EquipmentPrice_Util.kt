package io.github.osdanova.ffxprojecteditor.ffxlib.common

import io.github.osdanova.ffxprojecteditor.ffxlib.dictionaries.AutoAbilityPrice_Dictionary

/**
 * Computes the in-game gil price of an [EquipmentStruct] based on its
 * abilities and slot count.
 */
object EquipmentPrice_Util {

    fun getEquipmentPrice(
        equipment: EquipmentStruct,
        autoAbilityPrices: List<Int>? = null,
    ): Int {
        val prices = if (autoAbilityPrices.isNullOrEmpty())
            AutoAbilityPrice_Dictionary.Instance.values.toList()
        else autoAbilityPrices

        var price = 0
        when (equipment.slot_count.toInt()) {
            1 -> {
                val autoAbilityId = FfxCommon_Util.getGameIndex(equipment.ability1)
                price += getSellPrice(autoAbilityId, prices)

                price += 12
            }
            2 -> {
                var autoAbilityId = FfxCommon_Util.getGameIndex(equipment.ability1)
                price += getSellPrice(autoAbilityId, prices)
                autoAbilityId = FfxCommon_Util.getGameIndex(equipment.ability2)
                price += getSellPrice(autoAbilityId, prices)

                price = (price * 1.5).toInt()
                price += 18
            }
            3 -> {
                var abilityCount = 0
                var autoAbilityId = FfxCommon_Util.getGameIndex(equipment.ability1)
                price += getSellPrice(autoAbilityId, prices)
                if (autoAbilityId.toInt() != 255) abilityCount++
                autoAbilityId = FfxCommon_Util.getGameIndex(equipment.ability2)
                price += getSellPrice(autoAbilityId, prices)
                if (autoAbilityId.toInt() != 255) abilityCount++
                autoAbilityId = FfxCommon_Util.getGameIndex(equipment.ability3)
                price += getSellPrice(autoAbilityId, prices)
                if (autoAbilityId.toInt() != 255) abilityCount++

                price = (price * 4.5).toInt()
                price += 56
                if (abilityCount > 1) price = (price / 1.5).toInt()
            }
            4 -> {
                var abilityCount = 0
                var autoAbilityId = FfxCommon_Util.getGameIndex(equipment.ability1)
                price += getSellPrice(autoAbilityId, prices)
                if (autoAbilityId.toInt() != 255) abilityCount++
                autoAbilityId = FfxCommon_Util.getGameIndex(equipment.ability2)
                price += getSellPrice(autoAbilityId, prices)
                if (autoAbilityId.toInt() != 255) abilityCount++
                autoAbilityId = FfxCommon_Util.getGameIndex(equipment.ability3)
                price += getSellPrice(autoAbilityId, prices)
                if (autoAbilityId.toInt() != 255) abilityCount++
                autoAbilityId = FfxCommon_Util.getGameIndex(equipment.ability4)
                price += getSellPrice(autoAbilityId, prices)
                if (autoAbilityId.toInt() != 255) abilityCount++

                price *= 15
                price += 187
                if (abilityCount == 2) price /= 2
                if (abilityCount > 2) price /= 3
            }
        }

        return price
    }

    private fun getSellPrice(abilityId: UShort, autoAbilityPrices: List<Int>): Int {
        if (abilityId.toInt() == 255) return 0
        return autoAbilityPrices[abilityId.toInt()] / 4
    }
}
