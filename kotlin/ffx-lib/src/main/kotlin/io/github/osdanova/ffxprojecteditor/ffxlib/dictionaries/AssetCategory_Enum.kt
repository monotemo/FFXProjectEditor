package io.github.osdanova.ffxprojecteditor.ffxlib.dictionaries

enum class AssetCategory_Enum(val value: Int) {
    None(0),
    Unk1(1),
    Unk2(2),
    EntityModel(3),
    EquipmentModel(4),
    EquipmentName(5),
    Unk6(6),
    Unk7(7),
    Unk8(8),
    Unk9(9);

    companion object {
        fun fromValue(v: Int): AssetCategory_Enum? = entries.firstOrNull { it.value == v }
    }
}
