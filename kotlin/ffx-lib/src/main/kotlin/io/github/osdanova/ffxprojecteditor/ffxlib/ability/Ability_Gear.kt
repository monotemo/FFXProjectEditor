package io.github.osdanova.ffxprojecteditor.ffxlib.ability

import io.github.osdanova.ffxprojecteditor.binary.BinField
import io.github.osdanova.ffxprojecteditor.ffxlib.common.ElementalWeaknessData
import io.github.osdanova.ffxprojecteditor.ffxlib.common.StatusByteList
import io.github.osdanova.ffxprojecteditor.ffxlib.common.StatusDurationByteList

/**
 * Stat / status / ability flags payload attached to gear and gear-style abilities.
 */
class Ability_Gear {
    @BinField var sosFlag: Byte = 0
    @BinField var elementalStrike: Byte = 0
    @BinField var elementalWeakness: ElementalWeaknessData = ElementalWeaknessData()
    @BinField var statusChance: StatusByteList = StatusByteList()
    @BinField var statusDuration: StatusDurationByteList = StatusDurationByteList()
    @BinField var statusResistChance: StatusByteList = StatusByteList()
    @BinField var statIncreaseAmount: Byte = 0
    @BinField var unk56: Byte = 0
    @BinField var statIncreaseFlags: Byte = 0
    @BinField var autoStatus1: Byte = 0
    @BinField var autoStatus2: Byte = 0
    @BinField var autoStatus3: Byte = 0
    @BinField var autoStatus4: Byte = 0
    @BinField var unk5C: Byte = 0
    @BinField var unk5D: Byte = 0
    @BinField var statusExtraFlags: Short = 0
    @BinField var statusResistExtraFlags: Short = 0
    @BinField var abilitFlags1: Byte = 0
    @BinField var abilitFlags2: Byte = 0
    @BinField var abilitFlags3: Byte = 0
    @BinField var abilitFlags4: Byte = 0
    @BinField var abilitFlags5: Byte = 0
    @BinField var unk67: Byte = 0
    @BinField var unk68: Byte = 0
    @BinField var groupIndex: Byte = 0
    @BinField var groupLevel: Byte = 0
    @BinField var internationalBonusIndex: Byte = 0
}
