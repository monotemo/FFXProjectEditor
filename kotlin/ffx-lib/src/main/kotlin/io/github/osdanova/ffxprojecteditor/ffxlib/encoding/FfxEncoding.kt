package io.github.osdanova.ffxprojecteditor.ffxlib.encoding

/**
 * Port of the C# `FfxEncoding` partial class. Provides decoders/encoders for FFX text scripts.
 * Byte values are stored as `Int` (0-255) because the original C# uses unsigned bytes; this
 * avoids sign-extension headaches when reading from streams.
 */
object FfxEncoding {
    const val C_NULL: Int = 0
    const val C_NEW_LINE: Int = 3
    const val C_FORMAT: Int = 10
    const val C_CHAR_NAME: Int = 19

    /**
     * Decode the given byte array into a text script.
     */
    fun decodeScript(byteScript: ByteArray): TextScript {
        val textScript = TextScript()

        var currentCommand = TextScript.TextCommand()
        var currentArray = mutableListOf<Byte>()
        var i = 0
        while (i < byteScript.size) {
            val b = byteScript[i]
            val key = b.toInt() and 0xFF

            // String
            if (!ControlDecoder.containsKey(key)) {
                currentArray.add(b)
            } else {
                // Save string if any
                if (currentArray.isNotEmpty()) {
                    currentCommand.byteArray = currentArray.toByteArray()
                    textScript.commands.add(currentCommand)
                }

                // Add code
                currentCommand = TextScript.TextCommand()
                currentCommand.controlCode = key
                if (key == C_FORMAT || key == C_CHAR_NAME) { // 1 byte param
                    currentCommand.byteArray = byteArrayOf(byteScript[i + 1])
                    i++
                }
                textScript.commands.add(currentCommand)

                // Prepare next command
                currentCommand = TextScript.TextCommand()
                currentArray = mutableListOf()
            }
            i++
        }

        // Save string if any
        if (currentArray.isNotEmpty()) {
            currentCommand.byteArray = currentArray.toByteArray()
            textScript.commands.add(currentCommand)
        }

        return textScript
    }

    /**
     * Decode the given string command using the given decoder map.
     */
    fun decodeString(stringCommand: TextScript.TextCommand, decoder: Map<Int, Char>): String {
        if (stringCommand.isControlCode) {
            throw IllegalStateException("FfxEncoding.decodeString: This is a control code")
        }

        val sb = StringBuilder()
        for (b in stringCommand.byteArray) {
            val key = b.toInt() and 0xFF
            val c = decoder[key]
            if (c != null) {
                sb.append(c)
            } else {
                sb.append("<MISS:").append(key).append('>') // Using this until the full encodings are known
            }
        }
        return sb.toString()
    }

    /**
     * Encode the given string into a [TextScript.TextCommand] using the given encoder map.
     */
    fun encodeString(stringText: String, encoder: Map<Char, Int>): TextScript.TextCommand {
        val textCommand = TextScript.TextCommand()
        val byteList = mutableListOf<Byte>()
        for (c in stringText) {
            val v = encoder[c]
                ?: throw IllegalArgumentException("FfxEncoding.encodeString: Invalid char for given encoder: $c")
            byteList.add(v.toByte())
        }
        textCommand.byteArray = byteList.toByteArray()
        return textCommand
    }

    /**
     * Given a text file, find and return the script (as a byte array) located at the given offset.
     * The script ends at the first NULL byte.
     */
    fun getScriptBytesFromTextFile(textFile: ByteArray, scriptOffset: Int): ByteArray {
        var endIndex = scriptOffset
        while (endIndex < textFile.size && textFile[endIndex].toInt() != 0) {
            endIndex++
        }
        val length = endIndex - scriptOffset
        val result = ByteArray(length)
        System.arraycopy(textFile, scriptOffset, result, 0, length)
        return result
    }

    /**
     * Given a text file, append the given bytes including a trailing NULL.
     */
    fun writeBytesIntoTextFile(textFile: ByteArray, bytesToWrite: ByteArray): ByteArray {
        val combined = ByteArray(textFile.size + bytesToWrite.size + 1)
        System.arraycopy(textFile, 0, combined, 0, textFile.size)
        System.arraycopy(bytesToWrite, 0, combined, textFile.size, bytesToWrite.size)
        // Trailing 0 already present from ByteArray default init
        return combined
    }

    /**
     * A text script contains the commands that form a text.
     * A script contains a list of commands which can be either simple text or a control code with a parameter.
     */
    class TextScript {
        val commands: MutableList<TextCommand> = mutableListOf()

        // Can be a string or a control code with parameters
        class TextCommand {
            var controlCode: Int = 0
            var byteArray: ByteArray = ByteArray(0)

            val isControlCode: Boolean get() = controlCode != 0
        }

        fun getString(decoder: Map<Int, Char>, withControlCodes: Boolean = false): String {
            val result = StringBuilder()
            for (command in commands) {
                if (!command.isControlCode) {
                    result.append(decodeString(command, decoder))
                } else {
                    if (!withControlCodes && command.controlCode == C_FORMAT) {
                        continue
                    }

                    val paramKey = if (command.byteArray.isNotEmpty()) command.byteArray[0].toInt() and 0xFF else 0
                    when (command.controlCode) {
                        C_NEW_LINE -> result.append(System.lineSeparator())

                        C_FORMAT -> {
                            val s = FormatCodes[paramKey]
                            if (s != null) {
                                result.append(s)
                            } else {
                                result.append("<C_ERROR:").append(command.controlCode).append(':').append(paramKey).append('>')
                            }
                        }

                        C_CHAR_NAME -> {
                            val s = CharacterNameCodes[paramKey]
                            if (s != null) {
                                result.append(s)
                            } else {
                                result.append("<C_ERROR:").append(command.controlCode).append(':').append(paramKey).append('>')
                            }
                        }

                        else -> { /* no-op */ }
                    }
                }
            }
            return result.toString()
        }
    }
}
