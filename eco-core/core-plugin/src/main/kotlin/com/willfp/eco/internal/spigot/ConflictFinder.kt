package com.willfp.eco.internal.spigot

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.util.containsIgnoreCase
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import java.io.File
import java.util.zip.ZipFile

object ConflictFinder {
    fun searchForConflicts(eco: EcoPlugin): List<Conflict> {
        val conflicts = mutableListOf<Conflict>()

        for (plugin in Bukkit.getPluginManager().plugins) {
            if (eco.configYml.getStrings("conflicts.whitelist").containsIgnoreCase(plugin.name)) {
                continue
            }

            val conflict = try {
                plugin.getConflict()
            } catch (e: Exception) {
                continue
            } // Really can't be fucked to do this properly.

            if (conflict != null) {
                conflicts.add(conflict)
            }
        }

        return conflicts
    }
}

data class Conflict(
    val plugin: Plugin,
    val conflictType: ConflictType
) {
    val conflictMessage: String
        get() = "${plugin.name} will likely conflict with eco! Reason: ${conflictType.friendlyMessage}"
}

enum class ConflictType(
    val friendlyMessage: String
) {
    LIB_LOADER("Kotlin found in libraries (lib-loader)"),
    KOTLIN_SHADE("Kotlin shaded into jar");
}

private fun Plugin.getConflict(): Conflict? {
    if (this.description.libraries.any { it.contains("kotlin-stdlib") }) {
        return Conflict(this, ConflictType.LIB_LOADER)
    }

    val file = File(this::class.java.protectionDomain.codeSource.location.toURI())

    if (!file.exists() || file.name.contains("PolymartHelper")) {
        return null
    }

    val zip = ZipFile(file)

    for (entry in zip.entries()) {
        if (entry.name.startsWith("kotlin/") || entry.name.startsWith("kotlinx/")) {
            return Conflict(this, ConflictType.KOTLIN_SHADE)
        }
    }

    return null
}
