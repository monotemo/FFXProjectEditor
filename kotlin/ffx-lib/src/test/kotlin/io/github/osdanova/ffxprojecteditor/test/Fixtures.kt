package io.github.osdanova.ffxprojecteditor.test

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Locates real FFX game files for round-trip tests. Tests are opt-in: they
 * resolve fixtures relative to the `FFX_FIXTURES_DIR` env var. When the var
 * is unset or the file is missing, fixture-backed tests skip gracefully so
 * the suite stays green in CI / fresh checkouts.
 *
 * Expected layout under `FFX_FIXTURES_DIR` mirrors the extracted master
 * folder the editor itself loads, e.g.:
 *
 *   $FFX_FIXTURES_DIR/jppc/battle/mon/_m001/m001.bin
 *   $FFX_FIXTURES_DIR/jppc/battle/kernel/arms_rate.bin
 *   $FFX_FIXTURES_DIR/new_uspc/battle/kernel/item.bin
 */
object Fixtures {
    private val root: Path? = System.getenv("FFX_FIXTURES_DIR")?.let { Paths.get(it) }

    fun isAvailable(): Boolean = root?.let { Files.isDirectory(it) } == true

    fun pathOrNull(relative: String): Path? {
        val r = root ?: return null
        val p = r.resolve(relative)
        return if (Files.isRegularFile(p)) p else null
    }

    fun bytesOrNull(relative: String): ByteArray? = pathOrNull(relative)?.let(Files::readAllBytes)
}
