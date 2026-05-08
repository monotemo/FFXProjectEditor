package io.github.osdanova.ffxprojecteditor.binary

import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KType
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

/**
 * Reflection-based binary (de)serializer modeled after Xe.BinaryMapper from the
 * original C# project. Reads and writes structs annotated with [BinField] using
 * little-endian byte order — matching the on-disk layout of FFX game files.
 *
 * Supported field types:
 *  - Primitives: `Byte`, `Short`, `Int`, `Long`, `Boolean`
 *  - Unsigned variants: `UByte`, `UShort`, `UInt`, `ULong`
 *  - Arrays with explicit `count`: `ByteArray`, `ShortArray`, `IntArray`,
 *    `Array<UByte>`, `Array<UShort>`, etc.
 *  - Nested objects whose class is also `[BinField]`-annotated.
 *
 * Enums are intentionally not supported by the mapper: store the raw primitive
 * (e.g. `UByte`) and expose typed accessors at the use site. This keeps the
 * mapper lean and matches how Kotlin enums (which lack arbitrary backing values)
 * differ from C# enums.
 */
object BinaryMapping {

    fun <T : Any> read(bytes: ByteArray, kClass: KClass<T>): T =
        read(ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN), kClass)

    fun <T : Any> read(buffer: ByteBuffer, kClass: KClass<T>): T {
        if (buffer.order() != ByteOrder.LITTLE_ENDIAN) buffer.order(ByteOrder.LITTLE_ENDIAN)
        val ctor = kClass.java.getDeclaredConstructor()
        ctor.isAccessible = true
        val instance = ctor.newInstance()
        val start = buffer.position()
        for ((prop, ann) in orderedFields(kClass)) {
            if (ann.offset >= 0) buffer.position(start + ann.offset)
            @Suppress("UNCHECKED_CAST")
            val mutable = prop as KMutableProperty1<T, Any?>
            mutable.isAccessible = true
            mutable.set(instance, readValue(buffer, prop.returnType, ann))
        }
        return instance
    }

    inline fun <reified T : Any> read(bytes: ByteArray): T = read(bytes, T::class)
    inline fun <reified T : Any> read(buffer: ByteBuffer): T = read(buffer, T::class)

    fun write(buffer: ByteBuffer, value: Any) {
        if (buffer.order() != ByteOrder.LITTLE_ENDIAN) buffer.order(ByteOrder.LITTLE_ENDIAN)
        val kClass = value::class
        val start = buffer.position()
        for ((prop, ann) in orderedFields(kClass)) {
            if (ann.offset >= 0) buffer.position(start + ann.offset)
            @Suppress("UNCHECKED_CAST")
            val getter = prop as kotlin.reflect.KProperty1<Any, Any?>
            getter.isAccessible = true
            writeValue(buffer, getter.get(value), prop.returnType, ann)
        }
    }

    fun toByteArray(value: Any, sizeHint: Int = 256): ByteArray {
        val buf = ByteBuffer.allocate(sizeHint).order(ByteOrder.LITTLE_ENDIAN)
        write(buf, value)
        val out = ByteArray(buf.position())
        buf.rewind()
        buf.get(out)
        return out
    }

    private fun orderedFields(kClass: KClass<*>): List<Pair<kotlin.reflect.KProperty1<*, *>, BinField>> =
        kClass.memberProperties
            .mapNotNull { p -> p.findAnnotation<BinField>()?.let { p to it } }

    private fun readValue(buf: ByteBuffer, type: KType, ann: BinField): Any? {
        val classifier = type.classifier ?: error("Missing classifier for $type")
        return when (classifier) {
            Byte::class -> buf.get()
            UByte::class -> buf.get().toUByte()
            Short::class -> buf.short
            UShort::class -> buf.short.toUShort()
            Int::class -> buf.int
            UInt::class -> buf.int.toUInt()
            Long::class -> buf.long
            ULong::class -> buf.long.toULong()
            Boolean::class -> buf.get().toInt() != 0
            ByteArray::class -> ByteArray(ann.count).also { buf.get(it) }
            ShortArray::class -> ShortArray(ann.count) { buf.short }
            IntArray::class -> IntArray(ann.count) { buf.int }
            else -> {
                if (classifier !is KClass<*>) error("Unsupported classifier $classifier")
                val raw = classifier.java
                if (raw.isArray || classifier == Array::class) {
                    val argType = type.arguments[0].type ?: error("Array missing component type")
                    val argClass = argType.classifier as? KClass<*> ?: error("Array missing classifier")
                    val componentJavaClass = argClass.javaObjectType
                    @Suppress("UNCHECKED_CAST")
                    val arr = java.lang.reflect.Array.newInstance(componentJavaClass, ann.count) as Array<Any?>
                    for (i in 0 until ann.count) arr[i] = readValue(buf, argType, ann)
                    arr
                } else {
                    read(buf, classifier)
                }
            }
        }
    }

    private fun writeValue(buf: ByteBuffer, value: Any?, type: KType, ann: BinField) {
        val classifier = type.classifier ?: error("Missing classifier for $type")
        when (classifier) {
            Byte::class -> buf.put(value as Byte)
            UByte::class -> buf.put((value as UByte).toByte())
            Short::class -> buf.putShort(value as Short)
            UShort::class -> buf.putShort((value as UShort).toShort())
            Int::class -> buf.putInt(value as Int)
            UInt::class -> buf.putInt((value as UInt).toInt())
            Long::class -> buf.putLong(value as Long)
            ULong::class -> buf.putLong((value as ULong).toLong())
            Boolean::class -> buf.put(if (value as Boolean) 1.toByte() else 0.toByte())
            ByteArray::class -> {
                val arr = value as ByteArray
                require(arr.size == ann.count) { "ByteArray size ${arr.size} != declared count ${ann.count}" }
                buf.put(arr)
            }
            ShortArray::class -> {
                val arr = value as ShortArray
                require(arr.size == ann.count) { "ShortArray size ${arr.size} != declared count ${ann.count}" }
                for (s in arr) buf.putShort(s)
            }
            IntArray::class -> {
                val arr = value as IntArray
                require(arr.size == ann.count) { "IntArray size ${arr.size} != declared count ${ann.count}" }
                for (i in arr) buf.putInt(i)
            }
            else -> {
                if (classifier !is KClass<*>) error("Unsupported classifier $classifier")
                val raw = classifier.java
                if (raw.isArray || classifier == Array::class) {
                    @Suppress("UNCHECKED_CAST")
                    val arr = value as Array<Any?>
                    require(arr.size == ann.count) { "Array size ${arr.size} != declared count ${ann.count}" }
                    val argType = type.arguments[0].type ?: error("Array missing component type")
                    for (e in arr) writeValue(buf, e, argType, ann)
                } else {
                    write(buf, value ?: error("null nested object for $type"))
                }
            }
        }
    }
}
