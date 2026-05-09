package io.github.osdanova.ffxprojecteditor.binary

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BinaryMappingTest {

    class Primitives {
        @BinField var b: Byte = 0
        @BinField var ub: UByte = 0u
        @BinField var s: Short = 0
        @BinField var us: UShort = 0u
        @BinField var i: Int = 0
        @BinField var ui: UInt = 0u
        @BinField var bool: Boolean = false
    }

    @Test
    fun primitivesRoundTrip() {
        val src = Primitives().apply {
            b = -7
            ub = 0xFEu
            s = -300
            us = 0xBEEFu
            i = -123_456
            ui = 0xCAFEBABEu
            bool = true
        }
        val bytes = BinaryMapping.toByteArray(src, 32)
        // 1 + 1 + 2 + 2 + 4 + 4 + 1 = 15 bytes
        assertEquals(15, bytes.size)
        val back = BinaryMapping.read(bytes, Primitives::class)
        assertEquals(src.b, back.b)
        assertEquals(src.ub, back.ub)
        assertEquals(src.s, back.s)
        assertEquals(src.us, back.us)
        assertEquals(src.i, back.i)
        assertEquals(src.ui, back.ui)
        assertEquals(src.bool, back.bool)
    }

    class Arrays {
        @BinField(count = 4) var bytes: ByteArray = ByteArray(4)
        @BinField(count = 3) var shorts: ShortArray = ShortArray(3)
        @BinField(count = 2) var ushorts: Array<UShort> = arrayOf(0u, 0u)
    }

    @Test
    fun arraysRoundTrip() {
        val src = Arrays().apply {
            bytes = byteArrayOf(1, 2, 3, 4)
            shorts = shortArrayOf(0x1111, 0x2222, 0x3333)
            ushorts = arrayOf(0xAAAAu, 0xBBBBu)
        }
        val bytes = BinaryMapping.toByteArray(src, 32)
        // 4 + 6 + 4 = 14
        assertEquals(14, bytes.size)
        val back = BinaryMapping.read(bytes, Arrays::class)
        assertContentEquals(src.bytes, back.bytes)
        assertContentEquals(src.shorts, back.shorts)
        assertContentEquals(src.ushorts, back.ushorts)
    }

    class Inner {
        @BinField var a: UShort = 0u
        @BinField var b: UShort = 0u
    }

    class Outer {
        @BinField var head: UShort = 0u
        @BinField var inner: Inner = Inner()
        @BinField var tail: UShort = 0u
    }

    @Test
    fun nestedRoundTrip() {
        val src = Outer().apply {
            head = 0x1234u
            inner = Inner().apply { a = 0x5555u; b = 0x6666u }
            tail = 0x7777u
        }
        val bytes = BinaryMapping.toByteArray(src, 16)
        // 2 + (2 + 2) + 2 = 8
        assertEquals(8, bytes.size)
        val back = BinaryMapping.read(bytes, Outer::class)
        assertEquals(src.head, back.head)
        assertEquals(src.inner.a, back.inner.a)
        assertEquals(src.inner.b, back.inner.b)
        assertEquals(src.tail, back.tail)
    }

    class DeclarationOrderSensitive {
        // Names are deliberately chosen so alphabetical order (alpha, beta,
        // gamma) DIFFERS from declaration order (gamma, alpha, beta). If the
        // mapper accidentally picks alphabetical order, the bytes below
        // won't decode the same values back.
        @BinField var gamma: Byte = 0
        @BinField var alpha: Short = 0
        @BinField var beta: Int = 0
    }

    @Test
    fun fieldsAreSerializedInDeclarationOrderNotAlphabeticalOrder() {
        val src = DeclarationOrderSensitive().apply {
            gamma = 0x42
            alpha = 0x1234
            beta = 0x789ABCDE
        }
        val bytes = BinaryMapping.toByteArray(src, 16)
        assertEquals(7, bytes.size, "size must be 1 + 2 + 4 = 7 in declaration order")
        // Bytes by position: [gamma, alpha lo, alpha hi, beta b0, beta b1, beta b2, beta b3]
        assertEquals(0x42.toByte(), bytes[0])
        assertEquals(0x34.toByte(), bytes[1])
        assertEquals(0x12.toByte(), bytes[2])
        assertEquals(0xDE.toByte(), bytes[3])
        assertEquals(0xBC.toByte(), bytes[4])
        assertEquals(0x9A.toByte(), bytes[5])
        assertEquals(0x78.toByte(), bytes[6])
        val back = BinaryMapping.read(bytes, DeclarationOrderSensitive::class)
        assertEquals(src.gamma, back.gamma)
        assertEquals(src.alpha, back.alpha)
        assertEquals(src.beta, back.beta)
    }

    class WithOffsets {
        @BinField(offset = 0x4) var late: UInt = 0u
        @BinField(offset = 0x0) var early: UInt = 0u
    }

    @Test
    fun explicitOffsetsRoundTrip() {
        val src = WithOffsets().apply { early = 0x11111111u; late = 0x22222222u }
        val bytes = BinaryMapping.toByteArray(src, 16)
        assertTrue(bytes.size >= 8)
        val back = BinaryMapping.read(bytes, WithOffsets::class)
        assertEquals(src.early, back.early)
        assertEquals(src.late, back.late)
    }
}
