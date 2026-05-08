package io.github.osdanova.ffxprojecteditor.ffxlib.memory

object MemoryMap {
    const val ADDR_ENEMY_CONTROL: Int = 0xD2A8FA

    const val POINTER_FILE_COMMAND: Int = 0xD2A92C // Points to absolute address
    const val POINTER_FILE_MONMAGIC1: Int = 0xD2A930 // Points to absolute address
    const val POINTER_FILE_MONMAGIC2: Int = 0xD2A934 // Points to absolute address
    const val POINTER_FILE_PLY_ROM: Int = 0xD2A938 // Points to absolute address
    const val POINTER_FILE_ITEM: Int = 0xD2A940 // Points to absolute address
    const val POINTER_FILE_A_ABILITY: Int = 0xD2A944 // Points to absolute address
    const val POINTER_FILE_SUM_ASSURE: Int = 0xD2A948 // Points to absolute address
    const val POINTER_FILE_ST_NUMBER: Int = 0xD2A958 // Points to absolute address
    const val POINTER_FILE_TAKARA: Int = 0x00D35FEC // Points to absolute address
    const val POINTER_FILE_MONSTER1: Int = 0xD2CA6C // Points to absolute address
    const val POINTER_FILE_MONSTER2: Int = 0xD2CA70 // Points to absolute address
    const val POINTER_FILE_MONSTER3: Int = 0xD2CA74 // Points to absolute address



    const val SIZE_BATTLE_CHR_ENTRY: Int = 0xF90
    const val ADDR_BATTLE_ENEMY_COUNT: Int = 0xD34460 + 93
    const val OFFSET_BATTLE_ENEMY_SCALE_V4: Int = 931



    const val ADDR_EQUIPMENT: Int = 0xD30F2C
    const val SIZE_EQUIPMENT: Int = 0x16
    const val COUNT_EQUIPMENT: Int = 178

    const val ADDR_ARENA_LIST: Int = 0xD30C9C // 104 monster counts and 35 monster unlocks.

    /******************************************
     * BATTLE
     ******************************************/

    const val ADDR_BATTLE_ACTIVE: Int = 0xD2A8E0 // Bool
    const val ADDR_BATTLE_TRIGGER: Int = 0xD2A8E2 // Random, Scripted
    const val ADDR_BATTLE_NAME: Int = 0xD2C25A // String 13
    const val ADDR_BATTLE_ENCOUNTER_INDEX: Int = 0xD2C259 // Byte

    const val ADDR_BATTLE_FORMATION_SLOTS: Int = 0xD2c895 // 1 sbyte each. 0xFF = nobody

    const val ADDR_BTL: Int = 0xD2A8D0

    const val ADDR_BTL_DEBUG_SETTINGS: Int = ADDR_BTL + 0x28

    const val DEBUG_INVINCIBLE_MON: Int = ADDR_BTL_DEBUG_SETTINGS + 0
    const val DEBUG_INVINCIBLE_PLY: Int = ADDR_BTL_DEBUG_SETTINGS + 1
    const val DEBUG_MON_CONTROL: Int = ADDR_BTL_DEBUG_SETTINGS + 2
    const val DEBUG_FREE_CAMERA: Int = ADDR_BTL_DEBUG_SETTINGS + 4
    const val DEBUG_NO_MAGIC_EFFECTS: Int = ADDR_BTL_DEBUG_SETTINGS + 8
    const val DEBUG_NO_MP_COST: Int = ADDR_BTL_DEBUG_SETTINGS + 9


    const val POINTER_BATTLE_PLAYER_LIST: Int = 0xD334CC // Points to absolute address
    const val POINTER_BATTLE_ENEMY_LIST: Int = 0xD34460 // Points to absolute address

    /******************************************
     * SAVE DATA
     ******************************************/
    const val ADDR_SAVEDATA: Int = 0xD2CA90
    const val SIZE_SAVEDATA: Int = 0x68C0

    const val ADDR_SAVE_RIKKU_ALBHED: Int = 0xD2CA90 + 0xD1 // bool

    const val ADDR_SAVE_TEMPLE_SEALS: Int = 0xD2CA90 + 0xC5C // 1 byte bitfield

    const val ADDR_SAVE_ALBHED_CHARACTERS: Int = 0xD2CA90 + 0x3D10 // uint. Bitfield
    const val ADDR_SAVE_YOJIMBO_COMPAT: Int = 0xD2CA90 + 0x3DA4 // uint

    const val ADDR_SAVE_ITEM_IDS: Int = 0xD2CA90 + 0x3ECC // 112 ushorts
    const val ADDR_SAVE_ITEM_COUNTS: Int = 0xD2CA90 + 0x40CC // 112 bytes
    const val COUNT_SAVE_ITEM: Int = 112 // 112 items

    const val ADDR_SAVE_KEY_ITEMS: Int = 0xD2CA90 + 0x448C // 7 bytes. Bitfield


    const val ADDR_SAVE_SPHERE_MUSIC: Int = 0xD2CDDA
    const val ADDR_SAVE_SPHERE_MOVIE: Int = 0xD2CDDB
    const val ADDR_SAVE_GIL: Int = 0xD307D8
    const val ADDR_SAVE_STATSHEET_TIDUS: Int = 0xD32060

    /******************************************
     * BLITZBALL
     ******************************************/
    const val ADDR_BLITZ_SCORE_OWN: Int = 0xD2E0CE // Byte
    const val ADDR_BLITZ_SCORE_OPPONENT: Int = 0xD2E0CF // Byte
    const val ADDR_BLITZ_HALF: Int = 0xD2E0D0 // Byte. 0 = First; 1 = Second
    const val ADDR_BLITZ_WINS: Int = 0xD2E1E2 // ushort
    const val POINTER_BLITZ_TIMER: Int = 0xF2FF14 // Points to absolute address
    const val OFFSET_BLITZ_TIMER: Int = 0x24C // Short. Offset from POINTER_BLITZ_TIMER

    /******************************************
     * MINIGAMES
     ******************************************/
    const val ADDR_LIGHT_TOTAL_ALL: Int = 0xD2CE8C // Ushort
    const val ADDR_LIGHT_TOTAL_DODGED: Int = 0xD2CE8E // Ushort
    const val ADDR_LIGHT_RECORD_STREAK: Int = 0xD2CE90 // Ushort

    const val POINTER_CHOCOBO_TIMER: Int = 0xF2FF14 // Points to absolute address
    const val OFFSET_CHOCOBO_TIMER: Int = 0x1D8 // 2 bytes. Decimals, Seconds. Offset from POINTER_CHOCOBO_TIMER
    const val OFFSET_CHOCOBO_POINTS: Int = 0x1FB // 4 bytes. Baloons (Own/Trainer), Hits (Own/Trainer). Offset from POINTER_CHOCOBO_TIMER

    /******************************************
     * OVERDRIVES
     ******************************************/
    const val ADDR_TIDUS_OD_COUNT: Int = 0xD3083C // int
    const val ADDR_TIDUS_OD_UNLOCK: Int = 0xD307FC // byte. bitflag (0,1,2,3)
    const val ADDR_WAKKA_BATTLE_COUNT: Int = 0xD322FC // int
    const val ADDR_WAKKA_OD_UNLOCK: Int = 0xD307FE // byte. bitflag (4,5,6,7)
    const val ADDR_AURON_OD_UNLOCK: Int = 0xD307FC // byte. bitflag (4,5,6,7)
    const val ADDR_KHIMARI_OD_UNLOCK: Int = 0xD307FD // byte. bitflag (4,5,6,7)
}
