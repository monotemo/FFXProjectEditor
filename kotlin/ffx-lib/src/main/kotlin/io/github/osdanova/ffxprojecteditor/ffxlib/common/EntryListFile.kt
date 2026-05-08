package io.github.osdanova.ffxprojecteditor.ffxlib.common

import io.github.osdanova.ffxprojecteditor.binary.BinField
import io.github.osdanova.ffxprojecteditor.binary.BinaryMapping

/**
 * Generic entry-list container used by many of FFX's binary files.
 * Begins with a 0x14-byte FileHeader, followed by the entry table
 * (firstFile) and an optional secondary blob (secondFile).
 */
class EntryListFile {
    var header: FileHeader = FileHeader()
    var firstFile: ByteArray = ByteArray(0)
    var secondFile: ByteArray? = null

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

        val realEntryCount: Int
            get() = (entryCount + 1) - previousFileCount
    }

    companion object {
        const val HEADER_SIZE: Int = 0x14

        fun unpack(byteFile: ByteArray): EntryListFile {
            val file = EntryListFile()
            file.header = BinaryMapping.read(byteFile, FileHeader::class)

            val tableStart = HEADER_SIZE
            val firstSize = file.header.realEntryCount * file.header.entrySize.toInt()
            val firstEnd = tableStart + firstSize
            file.firstFile = byteFile.copyOfRange(tableStart, firstEnd)

            if (file.header.entryTableSize > 0 && firstEnd < byteFile.size) {
                file.secondFile = byteFile.copyOfRange(firstEnd, byteFile.size)
            }
            return file
        }

        fun pack(
            entrySize: Short,
            entryCount: Short,
            firstFile: ByteArray,
            secondFile: ByteArray? = null,
            signature: Byte = 1,
            previousFileCount: Short = 0,
        ): ByteArray {
            val header = FileHeader().apply {
                this.signature = signature
                this.previousFileCount = previousFileCount
                this.entryCount = (entryCount - 1).toShort()
                this.entrySize = entrySize
                this.entryTableSize = clampSize(entryCount.toInt() * entrySize.toInt())
                this.entryTableFileOffset = HEADER_SIZE
            }

            var byteFile = BinaryMapping.toByteArray(header)
            byteFile += firstFile
            if (secondFile != null) {
                byteFile += secondFile
            }
            return byteFile
        }

        /**
         * Filesize uses 2 bytes. If the size is bigger than what a short can store,
         * extra bytes are removed (e.g. /battle/kernel/item_get.bin).
         */
        private fun clampSize(size: Int): Short = (size and 0xFFFF).toShort()
    }
}
