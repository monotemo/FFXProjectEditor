package io.github.osdanova.ffxprojecteditor.ffxlib.common

import io.github.osdanova.ffxprojecteditor.binary.BinField

/**
 * Header used at the start of many FFX entry-table files (e.g. battle/kernel/ *.bin).
 * Total size is 0x14 bytes.
 */
class FileHeader {
    /** Could be file version. */
    @BinField var signature: Byte = 0

    /** Probably 4 shorts including the version. */
    @BinField(count = 7) var unknownBytes: ByteArray = ByteArray(7)

    /** For split files (e.g. battle/kernel/monster1/2/3.bin). */
    @BinField var previousFileCount: Short = 0

    /** Entry count - 1 (increase by 1 when using it). */
    @BinField var entryCount: Short = 0

    @BinField var entrySize: Short = 0

    /** Entry count * size. */
    @BinField var entryTableSize: Short = 0

    /** Size of the header (0x14). Could be 2 shorts. */
    @BinField var entryTableFileOffset: Int = 0
}

/**
 * Entry that describes where (and possibly which script) a piece of localized text
 * lives in a text/script bundle.
 */
class TextScriptInfo {
    @BinField var offset: UShort = 0u

    /** Possibly unused and possibly wrong. */
    @BinField var scriptId: UShort = 0u
}
