package io.github.osdanova.ffxprojecteditor.ffxlib.dictionaries

enum class Element_Enum(val value: Int) {
    Physical(0),
    Fire(1),
    Blizzard(2),
    Thunder(3),
    Water(4),
    Holy(5);

    companion object {
        fun fromValue(v: Int): Element_Enum? = entries.firstOrNull { it.value == v }
    }
}
