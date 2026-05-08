package io.github.osdanova.ffxprojecteditor.ffxlib.common

import io.github.osdanova.ffxprojecteditor.binary.BinField
import io.github.osdanova.ffxprojecteditor.ffxlib.dictionaries.Element_Flags
import io.github.osdanova.ffxprojecteditor.util.BitFlag_Util

/**
 * Stores per-element absorb / immune / resist / weak flags.
 *
 * Each property is a [Element_Flags] bitmask stored as a UByte. The boolean
 * accessor properties expose individual element bits.
 */
class ElementalWeaknessData {
    @BinField var absorb: UByte = 0u
    @BinField var immune: UByte = 0u
    @BinField var resist: UByte = 0u
    @BinField var weak: UByte = 0u

    private fun get(value: UByte, flag: Element_Flags): Boolean =
        BitFlag_Util.isFlagSet(value, flag.mask.toUByte())

    private fun set(value: UByte, flag: Element_Flags, set: Boolean): UByte =
        BitFlag_Util.setFlag(value, flag.mask.toUByte(), set)

    var absorbFire: Boolean
        get() = get(absorb, Element_Flags.Fire)
        set(v) { absorb = set(absorb, Element_Flags.Fire, v) }
    var absorbBlizzard: Boolean
        get() = get(absorb, Element_Flags.Blizzard)
        set(v) { absorb = set(absorb, Element_Flags.Blizzard, v) }
    var absorbThunder: Boolean
        get() = get(absorb, Element_Flags.Thunder)
        set(v) { absorb = set(absorb, Element_Flags.Thunder, v) }
    var absorbWater: Boolean
        get() = get(absorb, Element_Flags.Water)
        set(v) { absorb = set(absorb, Element_Flags.Water, v) }
    var absorbHoly: Boolean
        get() = get(absorb, Element_Flags.Holy)
        set(v) { absorb = set(absorb, Element_Flags.Holy, v) }

    var immuneFire: Boolean
        get() = get(immune, Element_Flags.Fire)
        set(v) { immune = set(immune, Element_Flags.Fire, v) }
    var immuneBlizzard: Boolean
        get() = get(immune, Element_Flags.Blizzard)
        set(v) { immune = set(immune, Element_Flags.Blizzard, v) }
    var immuneThunder: Boolean
        get() = get(immune, Element_Flags.Thunder)
        set(v) { immune = set(immune, Element_Flags.Thunder, v) }
    var immuneWater: Boolean
        get() = get(immune, Element_Flags.Water)
        set(v) { immune = set(immune, Element_Flags.Water, v) }
    var immuneHoly: Boolean
        get() = get(immune, Element_Flags.Holy)
        set(v) { immune = set(immune, Element_Flags.Holy, v) }

    var resistFire: Boolean
        get() = get(resist, Element_Flags.Fire)
        set(v) { resist = set(resist, Element_Flags.Fire, v) }
    var resistBlizzard: Boolean
        get() = get(resist, Element_Flags.Blizzard)
        set(v) { resist = set(resist, Element_Flags.Blizzard, v) }
    var resistThunder: Boolean
        get() = get(resist, Element_Flags.Thunder)
        set(v) { resist = set(resist, Element_Flags.Thunder, v) }
    var resistWater: Boolean
        get() = get(resist, Element_Flags.Water)
        set(v) { resist = set(resist, Element_Flags.Water, v) }
    var resistHoly: Boolean
        get() = get(resist, Element_Flags.Holy)
        set(v) { resist = set(resist, Element_Flags.Holy, v) }

    var weakFire: Boolean
        get() = get(weak, Element_Flags.Fire)
        set(v) { weak = set(weak, Element_Flags.Fire, v) }
    var weakBlizzard: Boolean
        get() = get(weak, Element_Flags.Blizzard)
        set(v) { weak = set(weak, Element_Flags.Blizzard, v) }
    var weakThunder: Boolean
        get() = get(weak, Element_Flags.Thunder)
        set(v) { weak = set(weak, Element_Flags.Thunder, v) }
    var weakWater: Boolean
        get() = get(weak, Element_Flags.Water)
        set(v) { weak = set(weak, Element_Flags.Water, v) }
    var weakHoly: Boolean
        get() = get(weak, Element_Flags.Holy)
        set(v) { weak = set(weak, Element_Flags.Holy, v) }
}
