package io.github.osdanova.ffxprojecteditor.ffxlib.dictionaries

enum class Element_Flags(val mask: Int) {
    Fire(0x01),
    Blizzard(0x02),
    Thunder(0x04),
    Water(0x08),
    Holy(0x10);
}
