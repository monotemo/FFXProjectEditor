package io.github.osdanova.ffxprojecteditor.binary

/**
 * Marks a property as serialized by [BinaryMapping].
 *
 * - [offset] absolute byte offset from the start of the enclosing object. `-1` means
 *   "sequential" (continue from the current buffer position).
 * - [count] number of elements when the property is an array type (`ByteArray`,
 *   `ShortArray`, `IntArray`, or a typed `Array<T>`).
 *
 * Property declaration order is significant for sequential fields. Kotlin/JVM
 * reflection preserves source declaration order in `memberProperties`.
 */
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class BinField(
    val offset: Int = -1,
    val count: Int = 0,
)
