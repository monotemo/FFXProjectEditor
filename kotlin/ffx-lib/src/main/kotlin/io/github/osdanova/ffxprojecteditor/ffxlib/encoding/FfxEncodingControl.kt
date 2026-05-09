package io.github.osdanova.ffxprojecteditor.ffxlib.encoding

/**
 * Control code lookup tables ported from `FfxEncoding.control.cs`.
 * Keys are `Int` (0-255) to match the rest of the encoding subsystem.
 */
val ControlDecoder: Map<Int, String> = linkedMapOf(
    // Control codes
    0 to "<NULL>", // NULL
    1 to "<C1>",
    2 to "<C2>",
    3 to "<C3>", // Environment.NewLine
    4 to "<C4>",
    5 to "<C5>",
    6 to "<C6>",
    7 to "<C7>",
    8 to "<C8>",
    9 to "<C9>",
    10 to "<C10>", // Formatting control code
    11 to "<C11>",
    12 to "<C12>",
    13 to "<C13>",
    14 to "<C14>",
    15 to "<C15>",
    16 to "<C16>",
    17 to "<C17>",
    18 to "<C18>", // Input?
    19 to "<C19>", // Character name control code
    20 to "<C20>",
    21 to "<C21>",
    22 to "<C22>", // Item name 1 ?
    23 to "<C23>", // Item name 2 ?
    24 to "<C24>",
    25 to "<C25>",
    26 to "<C26>",
    27 to "<C27>",
    28 to "<C28>",
    29 to "<C29>",
    30 to "<C30>",
    31 to "<C31>",
    32 to "<C32>",
    33 to "<C33>",
    34 to "<C34>",
    35 to "<C35>", // Key Item ?
    36 to "<C36>",
    37 to "<C37>",
    38 to "<C38>",
    39 to "<C39>",
    40 to "<C40>",
    41 to "<C41>",
    42 to "<C42>",
    43 to "<C43>",
    44 to "<C44>",
    45 to "<C45>",
    46 to "<C46>",
    47 to "<C47>",
)

// Codes for 10 (0x0A)
val FormatCodes: Map<Int, String> = linkedMapOf(
    65 to "</>",   // Back to normal format
    67 to "<W>",   // Warning yellow text (start and end)
    177 to "<B>",  // Bold (white)
)

// Codes for 19 (0x13)
val CharacterNameCodes: Map<Int, String> = linkedMapOf(
    48 to "<TIDUS>",
    49 to "<YUNA>",
    50 to "<AURON>",
    51 to "<KIMAHRI>",
    52 to "<WAKKA>",
    53 to "<LULU>",
    54 to "<RIKKU>",
    55 to "<SEYMOUR>",
    56 to "<VALEFOR>",
    57 to "<IFRIT>",
    58 to "<IXION>",
    59 to "<SHIVA>",
    60 to "<BAHAMUT>",
    61 to "<ANIMA>",
    62 to "<YOJIMBO>",
    63 to "<CINDY>",
    64 to "<SANDY>",
    65 to "<MINDY>",
)
