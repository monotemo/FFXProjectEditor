package io.github.osdanova.ffxprojecteditor.ffxlib.monster

import io.github.osdanova.ffxprojecteditor.binary.BinField
import io.github.osdanova.ffxprojecteditor.ffxlib.common.TextScriptInfo

/**
 * Header (0x30 bytes) at the beginning of a monster file. Contains the file size
 * plus pointers to each of the seven sub-files.
 */
class MonsterHeaderFile {
    /** Could be a uint count. */
    @BinField var signature: Int = 8
    @BinField var aiFilePointer: Int = 0
    @BinField var workerFilePointer: Int = 0
    @BinField var statSheetPointer: Int = 0
    @BinField var spoilsFilePointer: Int = 0
    @BinField var lootFilePointer: Int = 0
    @BinField var audioFilePointer: Int = 0
    @BinField var textFilePointer: Int = 0

    /** Size of the whole file. */
    @BinField var fileSize: Int = 0

    // 12 bytes of padding so the header lands at exactly 0x30, matching where
    // Monster_File.write starts the body. (The C# source declares Count=16, but
    // its writer also positions the body at 0x30 — meaning the C# code actually
    // corrupts the first 4 bytes of the AI section on every write. We diverge
    // here so round-trips are byte-faithful.)
    @BinField(count = 12) var padding: ByteArray = ByteArray(12)
}

/**
 * The on-disk layout of the monster stat-sheet sub-file. Wraps the
 * [Monster_StatSheet] payload with five [TextScriptInfo] entries (offsets into
 * the trailing text blob) and 4 padding bytes.
 */
class MonsterStatSheetStruct {
    @BinField var nameTSInfo: TextScriptInfo = TextScriptInfo()
    @BinField var sensorTSInfo: TextScriptInfo = TextScriptInfo()

    /** みしよう (unused) */
    @BinField var unusedText1TSInfo: TextScriptInfo = TextScriptInfo()
    @BinField var scanTSInfo: TextScriptInfo = TextScriptInfo()

    /** みしよう (unused) */
    @BinField var unusedText2TSInfo: TextScriptInfo = TextScriptInfo()
    @BinField var statSheet: Monster_StatSheet = Monster_StatSheet()
    @BinField(count = 4) var padding: ByteArray = ByteArray(4)
}
