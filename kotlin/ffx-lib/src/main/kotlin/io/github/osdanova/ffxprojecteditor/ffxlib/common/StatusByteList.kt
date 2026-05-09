package io.github.osdanova.ffxprojecteditor.ffxlib.common

import io.github.osdanova.ffxprojecteditor.binary.BinField

/**
 * 25 status-effect bytes (12 permanent + 13 temporal).
 */
class StatusByteList {
    // Permanent
    @BinField var death: Byte = 0
    @BinField var zombie: Byte = 0
    @BinField var petrify: Byte = 0
    @BinField var poison: Byte = 0
    @BinField var breakPower: Byte = 0
    @BinField var breakMagic: Byte = 0
    @BinField var breakArmor: Byte = 0
    @BinField var breakMental: Byte = 0
    @BinField var confuse: Byte = 0
    @BinField var berserk: Byte = 0
    @BinField var provoke: Byte = 0
    @BinField var threaten: Byte = 0

    // Temporal
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
