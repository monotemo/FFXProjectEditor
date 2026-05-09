package io.github.osdanova.ffxprojecteditor.ffxlib.ability

import io.github.osdanova.ffxprojecteditor.binary.BinField
import io.github.osdanova.ffxprojecteditor.ffxlib.common.TextScriptInfo

/**
 * On-disk layout of a command-style ability entry: four [TextScriptInfo]
 * pointers into the trailing text blob, then the [Ability_Command] payload.
 * The C# wrapper class `Ability_Structs` was a namespace-only outer; flattened here.
 */
class Ability_CommandStruct {
    @BinField var nameTSInfo: TextScriptInfo = TextScriptInfo()

    /** みしよう (unused) */
    @BinField var unusedText1TSInfo: TextScriptInfo = TextScriptInfo()
    @BinField var descriptionTSInfo: TextScriptInfo = TextScriptInfo()

    /** みしよう (unused) */
    @BinField var unusedText2TSInfo: TextScriptInfo = TextScriptInfo()
    @BinField var abilityInfo: Ability_Command = Ability_Command()
}

/**
 * On-disk layout of a gear-style ability entry: four [TextScriptInfo]
 * pointers into the trailing text blob, then the [Ability_Gear] payload.
 */
class Ability_GearStruct {
    @BinField var nameTSInfo: TextScriptInfo = TextScriptInfo()

    /** みしよう (unused) */
    @BinField var unusedText1TSInfo: TextScriptInfo = TextScriptInfo()
    @BinField var descriptionTSInfo: TextScriptInfo = TextScriptInfo()

    /** みしよう (unused) */
    @BinField var unusedText2TSInfo: TextScriptInfo = TextScriptInfo()
    @BinField var abilityInfo: Ability_Gear = Ability_Gear()
}
