package io.github.osdanova.ffxprojecteditor.ffxlib.common

import io.github.osdanova.ffxprojecteditor.binary.BinField

/**
 * 13 temporal-status duration bytes.
 */
class StatusDurationByteList {
    @BinField var sleep: Byte = 0
    @BinField var silence: Byte = 0
    @BinField var darkness: Byte = 0
    @BinField var shell: Byte = 0
    @BinField var protect: Byte = 0
    @BinField var reflect: Byte = 0
    @BinField var nulTide: Byte = 0
    @BinField var nulBlaze: Byte = 0
    @BinField var nulShock: Byte = 0
    @BinField var nulFrost: Byte = 0
    @BinField var regen: Byte = 0
    @BinField var haste: Byte = 0
    @BinField var slow: Byte = 0
}
