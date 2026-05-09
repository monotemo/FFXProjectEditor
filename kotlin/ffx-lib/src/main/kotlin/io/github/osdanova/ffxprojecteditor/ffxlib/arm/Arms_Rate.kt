package io.github.osdanova.ffxprojecteditor.ffxlib.arm

import io.github.osdanova.ffxprojecteditor.ffxlib.common.EntryListFile
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Reads / writes a flat list of Int rates wrapped in an [EntryListFile].
 */
class Arms_Rate {
    companion object {
        fun readList(byteFile: ByteArray): List<Int> {
            val listFile = EntryListFile.unpack(byteFile)
            val buffer = ByteBuffer.wrap(listFile.firstFile).order(ByteOrder.LITTLE_ENDIAN)
            val rateList = mutableListOf<Int>()
            for (i in 0 until listFile.header.realEntryCount) {
                rateList.add(buffer.int)
            }
            return rateList
        }

        fun writeList(rates: List<Int>): ByteArray {
            val buffer = ByteBuffer.allocate(rates.size * 4).order(ByteOrder.LITTLE_ENDIAN)
            for (r in rates) buffer.putInt(r)
            val firstFile = buffer.array()

            return EntryListFile.pack(
                entrySize = 4,
                entryCount = rates.size.toShort(),
                firstFile = firstFile,
            )
        }
    }
}
