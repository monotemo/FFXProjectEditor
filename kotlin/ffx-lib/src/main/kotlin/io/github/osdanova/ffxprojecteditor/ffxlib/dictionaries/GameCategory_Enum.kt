package io.github.osdanova.ffxprojecteditor.ffxlib.dictionaries

enum class GameCategory_Enum(val value: Int) {
    None(0),
    Models(1),
    Items(2),
    Commands(3),
    MonMagic1(4),
    Cat5(5),
    MonMagic2(6),
    Cat7(7),
    AutoAbilities(8),
    Cat9(9),
    KeyItems(10);

    companion object {
        fun fromValue(v: Int): GameCategory_Enum? = entries.firstOrNull { it.value == v }
    }
}
