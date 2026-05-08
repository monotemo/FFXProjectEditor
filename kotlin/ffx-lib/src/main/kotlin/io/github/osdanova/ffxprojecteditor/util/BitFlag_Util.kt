package io.github.osdanova.ffxprojecteditor.util

/**
 * Replacement for the C# BitFlag_Util that operated on `[Flags]` enums.
 * Kotlin enums don't have a [Flags] equivalent, so flag values are stored
 * as raw primitives (UByte / UShort / UInt) and manipulated through these
 * helpers using the `mask` (or `value`) of an enum entry.
 */
object BitFlag_Util {

    // ---- Int ----

    fun isFlagSet(value: Int, flag: Int): Boolean =
        (value and flag) == flag

    fun setFlag(value: Int, flag: Int, set: Boolean): Int =
        if (set) value or flag else value and flag.inv()

    fun setFlag(value: Int, flag: Int): Int = value or flag
    fun clearFlag(value: Int, flag: Int): Int = value and flag.inv()
    fun toggleFlag(value: Int, flag: Int): Int = value xor flag

    // ---- UInt ----

    fun isFlagSet(value: UInt, flag: UInt): Boolean =
        (value and flag) == flag

    fun setFlag(value: UInt, flag: UInt, set: Boolean): UInt =
        if (set) value or flag else value and flag.inv()

    fun setFlag(value: UInt, flag: UInt): UInt = value or flag
    fun clearFlag(value: UInt, flag: UInt): UInt = value and flag.inv()
    fun toggleFlag(value: UInt, flag: UInt): UInt = value xor flag

    // ---- UShort ----

    fun isFlagSet(value: UShort, flag: UShort): Boolean =
        (value.toInt() and flag.toInt()) == flag.toInt()

    fun setFlag(value: UShort, flag: UShort, set: Boolean): UShort {
        val v = value.toInt()
        val f = flag.toInt()
        return (if (set) v or f else v and f.inv()).toUShort()
    }

    fun setFlag(value: UShort, flag: UShort): UShort =
        (value.toInt() or flag.toInt()).toUShort()

    fun clearFlag(value: UShort, flag: UShort): UShort =
        (value.toInt() and flag.toInt().inv()).toUShort()

    fun toggleFlag(value: UShort, flag: UShort): UShort =
        (value.toInt() xor flag.toInt()).toUShort()

    // ---- UByte ----

    fun isFlagSet(value: UByte, flag: UByte): Boolean =
        (value.toInt() and flag.toInt()) == flag.toInt()

    fun setFlag(value: UByte, flag: UByte, set: Boolean): UByte {
        val v = value.toInt()
        val f = flag.toInt()
        return (if (set) v or f else v and f.inv()).toUByte()
    }

    fun setFlag(value: UByte, flag: UByte): UByte =
        (value.toInt() or flag.toInt()).toUByte()

    fun clearFlag(value: UByte, flag: UByte): UByte =
        (value.toInt() and flag.toInt().inv()).toUByte()

    fun toggleFlag(value: UByte, flag: UByte): UByte =
        (value.toInt() xor flag.toInt()).toUByte()
}
