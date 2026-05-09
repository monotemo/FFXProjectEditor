package io.github.osdanova.ffxprojecteditor.util

/**
 * Bit manipulation extensions ported from C# Bit_Util.
 *
 * Provides GetBit/SetBit on the integral primitives we typically deal with
 * (Int, UInt, UByte, UShort) plus on ByteArray, where the bit position is
 * an absolute index into the entire array.
 */

// ---- ByteArray ----

fun ByteArray.getBit(bitPosition: Int): Boolean {
    val byteIndex = bitPosition / 8
    val bitOffset = bitPosition % 8
    if (byteIndex >= this.size) {
        throw IndexOutOfBoundsException("Bit position exceeds array bounds.")
    }
    return (this[byteIndex].toInt() and (1 shl bitOffset)) != 0
}

fun ByteArray.setBit(bitPosition: Int, set: Boolean) {
    val byteIndex = bitPosition / 8
    val bitOffset = bitPosition % 8
    if (byteIndex >= this.size) {
        throw IndexOutOfBoundsException("Bit position exceeds array bounds.")
    }
    val current = this[byteIndex].toInt()
    val updated = if (set) {
        current or (1 shl bitOffset)
    } else {
        current and (1 shl bitOffset).inv()
    }
    this[byteIndex] = updated.toByte()
}

// ---- Int ----

fun Int.getBit(bitPosition: Int): Boolean =
    (this and (1 shl bitPosition)) != 0

fun Int.setBit(bitPosition: Int, set: Boolean): Int =
    if (set) this or (1 shl bitPosition) else this and (1 shl bitPosition).inv()

// ---- UInt ----

fun UInt.getBit(bitPosition: Int): Boolean =
    (this.toInt() and (1 shl bitPosition)) != 0

fun UInt.setBit(bitPosition: Int, set: Boolean): UInt {
    val intValue = this.toInt()
    val updated = if (set) intValue or (1 shl bitPosition) else intValue and (1 shl bitPosition).inv()
    return updated.toUInt()
}

// ---- UByte ----

fun UByte.getBit(bitPosition: Int): Boolean =
    (this.toInt() and (1 shl bitPosition)) != 0

fun UByte.setBit(bitPosition: Int, set: Boolean): UByte {
    val intValue = this.toInt()
    val updated = if (set) intValue or (1 shl bitPosition) else intValue and (1 shl bitPosition).inv()
    return updated.toUByte()
}

// ---- Byte ----

fun Byte.getBit(bitPosition: Int): Boolean =
    (this.toInt() and (1 shl bitPosition)) != 0

fun Byte.setBit(bitPosition: Int, set: Boolean): Byte {
    val intValue = this.toInt()
    val updated = if (set) intValue or (1 shl bitPosition) else intValue and (1 shl bitPosition).inv()
    return updated.toByte()
}

// ---- UShort ----

fun UShort.getBit(bitPosition: Int): Boolean =
    (this.toInt() and (1 shl bitPosition)) != 0

fun UShort.setBit(bitPosition: Int, set: Boolean): UShort {
    val intValue = this.toInt()
    val updated = if (set) intValue or (1 shl bitPosition) else intValue and (1 shl bitPosition).inv()
    return updated.toUShort()
}

// ---- Short ----

fun Short.getBit(bitPosition: Int): Boolean =
    (this.toInt() and (1 shl bitPosition)) != 0

fun Short.setBit(bitPosition: Int, set: Boolean): Short {
    val intValue = this.toInt()
    val updated = if (set) intValue or (1 shl bitPosition) else intValue and (1 shl bitPosition).inv()
    return updated.toShort()
}
