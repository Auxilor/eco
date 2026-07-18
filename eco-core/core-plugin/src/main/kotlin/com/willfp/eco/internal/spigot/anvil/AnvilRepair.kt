package com.willfp.eco.internal.spigot.anvil

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.bukkit.Material
import org.bukkit.Tag
import java.io.InputStreamReader

/** One row of `anvil/repair.json`: materials in [units] can unit-repair items made of [repairable]. */
private data class RepairEntry(val units: List<String>, val repairable: List<String>)

/**
 * Vanilla-style "unit repair" material table (e.g. iron ingot repairs iron tools/armor), loaded
 * from `anvil/repair.json` on the classpath so it can be tweaked or extended without code changes.
 *
 * Material names unknown on the running server version (e.g. spear/copper items pre-1.21.9) are
 * silently skipped, so the same fixture works across all supported Minecraft versions.
 */
object AnvilRepair {
    private val repair: List<Pair<Collection<Material>, Collection<Material>>> = loadRepairTable()

    /** Whether [other] (the right item's material) can unit-repair [this] (left item). */
    fun Material.canUnitRepair(other: Material): Boolean {
        for ((units, repairable) in repair) {
            if (this in units) {
                return other in repairable
            }
        }
        return false
    }

    private fun loadRepairTable(): List<Pair<Collection<Material>, Collection<Material>>> {
        val entries: List<RepairEntry> = javaClass.getResourceAsStream("/anvil/repair.json")
            ?.let { InputStreamReader(it) }
            ?.use { Gson().fromJson(it, object : TypeToken<List<RepairEntry>>() {}.type) }
            ?: emptyList()

        return entries.mapNotNull { entry ->
            val units = entry.units.flatMap { resolveMaterials(it) }
            val repairable = entry.repairable.mapNotNull { getMaterialOrNull(it) }
            if (units.isEmpty() || repairable.isEmpty()) null else units to repairable
        }
    }

    /** Resolves a units entry: `TAG:x` pulls from the matching [Tag], otherwise a single material name. */
    private fun resolveMaterials(name: String): Collection<Material> =
        if (name.startsWith("TAG:")) {
            when (name.removePrefix("TAG:")) {
                "PLANKS" -> Tag.PLANKS.values
                else -> emptyList()
            }
        } else {
            getMaterialOrNull(name)?.let { listOf(it) } ?: emptyList()
        }

    /** [name] as a [Material] if it exists on this server version, else null. */
    private fun getMaterialOrNull(name: String): Material? = try {
        Material.valueOf(name)
    } catch (_: IllegalArgumentException) {
        null
    }
}
