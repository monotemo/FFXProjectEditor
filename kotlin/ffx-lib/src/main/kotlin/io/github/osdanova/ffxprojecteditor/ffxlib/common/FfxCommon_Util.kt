package io.github.osdanova.ffxprojecteditor.ffxlib.common

import io.github.osdanova.ffxprojecteditor.ffxlib.dictionaries.AssetCategory_Enum
import io.github.osdanova.ffxprojecteditor.ffxlib.dictionaries.AutoAbility_Dictionary
import io.github.osdanova.ffxprojecteditor.ffxlib.dictionaries.CommandCharacter_Dictionary
import io.github.osdanova.ffxprojecteditor.ffxlib.dictionaries.CommandMonster1_Dictionary
import io.github.osdanova.ffxprojecteditor.ffxlib.dictionaries.CommandMonster2_Dictionary
import io.github.osdanova.ffxprojecteditor.ffxlib.dictionaries.GameCategory_Enum
import io.github.osdanova.ffxprojecteditor.ffxlib.dictionaries.Item_Dictionary

/**
 * Operations to get and set info from packed game indices.
 *
 * A "game index" is a 16-bit value where the top nibble is a [GameCategory_Enum]
 * and the lower 12 bits are an index into that category's dictionary.
 */
object FfxCommon_Util {

    fun getGameCategory(entry: UShort): UByte =
        ((entry.toInt() and 0xF000) shr 12).toUByte()

    fun getGameIndex(entry: UShort): UShort =
        (entry.toInt() and 0x0FFF).toUShort()

    fun setGameCategory(entry: UShort, valueEnum: UByte): UShort {
        val value = valueEnum.toInt()
        if (value < 0 || value > 0xF) {
            throw IllegalArgumentException("Value must be between 0x0 and 0xF.")
        }
        return ((entry.toInt() and 0x0FFF) or (value shl 12)).toUShort()
    }

    fun setGameIndex(entry: UShort, value: UShort): UShort {
        val v = value.toInt()
        if (v < 0 || v > 0xFFF) {
            throw IllegalArgumentException("value must be between 0x000 and 0xFFF.")
        }
        return ((entry.toInt() and 0xF000) or (v and 0x0FFF)).toUShort()
    }

    fun getGameIndexName(category: UByte, index: UShort): String {
        val categoryEnum = GameCategory_Enum.fromValue(category.toInt())

        if (categoryEnum == GameCategory_Enum.None && (index.toInt() == 0 || index.toInt() == 255)) {
            return ""
        }
        return when (categoryEnum) {
            GameCategory_Enum.Items ->
                Item_Dictionary.Instance[index] ?: "<NOT_INDEXED>"
            GameCategory_Enum.Commands ->
                CommandCharacter_Dictionary.Instance[index] ?: "<NOT_INDEXED>"
            GameCategory_Enum.MonMagic1 ->
                CommandMonster1_Dictionary.Instance[index] ?: "<NOT_INDEXED>"
            GameCategory_Enum.MonMagic2 ->
                CommandMonster2_Dictionary.Instance[index] ?: "<NOT_INDEXED>"
            GameCategory_Enum.AutoAbilities ->
                AutoAbility_Dictionary.Instance[index] ?: "<NOT_INDEXED>"
            else -> throw RuntimeException("[FfxCommon_Util] GetGameIndexName: Invalid category")
        }
    }

    fun getAssetIndexName(category: UByte, index: UShort): String {
        val categoryEnum = AssetCategory_Enum.fromValue(category.toInt())

        if (categoryEnum == AssetCategory_Enum.None && (index.toInt() == 0 || index.toInt() == 255)) {
            return ""
        }
        return when (categoryEnum) {
            AssetCategory_Enum.EquipmentModel -> "<TODO>"
            AssetCategory_Enum.EquipmentName -> "<TODO>"
            else -> throw RuntimeException("[FfxCommon_Util] GetAssetIndexName: Invalid category")
        }
    }
}
