package io.github.osdanova.ffxprojecteditor.ffxlib.monster

import io.github.osdanova.ffxprojecteditor.binary.BinaryMapping
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Top-level monster file. Wraps a 0x30-byte [MonsterHeaderFile] plus up to seven
 * sub-files. Sub-files are nullable since not every monster has every section.
 */
class Monster_File {
    var aiFile: ByteArray? = null
    var workerFile: ByteArray? = null
    var statSheetFile: Monster_StatSheet? = null
    var unkFile: ByteArray? = null
    var lootFile: Monster_Loot? = null
    var audioFile: ByteArray? = null
    var textFile: ByteArray? = null

    companion object {
        private const val HEADER_SIZE: Int = 0x30

        /**
         * Decode a complete monster file. The header pointers are walked from
         * end-of-file backwards (text → audio → loot → spoils → statSheet →
         * worker → ai) so that the size of each section can be derived from the
         * next pointer.
         */
        fun read(fileByte: ByteArray): Monster_File {
            val file = Monster_File()
            val buffer = ByteBuffer.wrap(fileByte).order(ByteOrder.LITTLE_ENDIAN)

            val header = BinaryMapping.read<MonsterHeaderFile>(buffer)

            var statSheetByteFile = ByteArray(0)
            var lootByteFile = ByteArray(0)
            var currentEofIndex = header.fileSize

            if (header.textFilePointer > 0) {
                file.textFile = readSlice(fileByte, header.textFilePointer, currentEofIndex)
                currentEofIndex = header.textFilePointer
            }

            if (header.audioFilePointer > 0) {
                file.audioFile = readSlice(fileByte, header.audioFilePointer, currentEofIndex)
                currentEofIndex = header.audioFilePointer
            }

            if (header.lootFilePointer > 0) {
                lootByteFile = readSlice(fileByte, header.lootFilePointer, currentEofIndex)
                currentEofIndex = header.lootFilePointer
            }

            if (header.spoilsFilePointer > 0) {
                file.unkFile = readSlice(fileByte, header.spoilsFilePointer, currentEofIndex)
                currentEofIndex = header.spoilsFilePointer
            }

            if (header.statSheetPointer > 0) {
                statSheetByteFile = readSlice(fileByte, header.statSheetPointer, currentEofIndex)
                currentEofIndex = header.statSheetPointer
            }

            if (header.workerFilePointer > 0) {
                file.workerFile = readSlice(fileByte, header.workerFilePointer, currentEofIndex)
                currentEofIndex = header.workerFilePointer
            }

            if (header.aiFilePointer > 0) {
                file.aiFile = readSlice(fileByte, header.aiFilePointer, currentEofIndex)
            }

            if (statSheetByteFile.isNotEmpty()) {
                file.statSheetFile = Monster_StatSheet.readSingle(statSheetByteFile)
            }
            if (lootByteFile.isNotEmpty()) {
                file.lootFile = Monster_Loot.readSingle(lootByteFile)
            }

            return file
        }

        private fun readSlice(source: ByteArray, start: Int, end: Int): ByteArray =
            source.copyOfRange(start, end)
    }

    /**
     * Encode the monster file. Sub-files are written sequentially starting at
     * 0x30 (immediately after the header), recording offsets back into the
     * header. The header is then serialized at the start of the buffer.
     */
    fun write(): ByteArray {
        val header = MonsterHeaderFile()

        // Body starts at HEADER_SIZE.
        val body = java.io.ByteArrayOutputStream()
        var position = HEADER_SIZE

        val ai = aiFile
        if (ai != null && ai.isNotEmpty()) {
            header.aiFilePointer = position
            body.write(ai)
            position += ai.size
        }

        val worker = workerFile
        if (worker != null && worker.isNotEmpty()) {
            header.workerFilePointer = position
            body.write(worker)
            position += worker.size
        }

        val statSheet = statSheetFile
        if (statSheet != null) {
            header.statSheetPointer = position
            val bytes = statSheet.writeSingle()
            body.write(bytes)
            position += bytes.size
            // Note: Files may be aligned to 4 or 8 bytes using 0xFF
        }

        val unk = unkFile
        if (unk != null && unk.isNotEmpty()) {
            header.spoilsFilePointer = position
            body.write(unk)
            position += unk.size
        }

        val loot = lootFile
        if (loot != null) {
            header.lootFilePointer = position
            val bytes = loot.writeSingle()
            body.write(bytes)
            position += bytes.size
        }

        val audio = audioFile
        if (audio != null && audio.isNotEmpty()) {
            header.audioFilePointer = position
            body.write(audio)
            position += audio.size
        }

        val text = textFile
        if (text != null && text.isNotEmpty()) {
            header.textFilePointer = position
            body.write(text)
            position += text.size
        }

        header.fileSize = position

        val headerBytes = BinaryMapping.toByteArray(header, sizeHint = HEADER_SIZE)
        return headerBytes + body.toByteArray()
    }
}
