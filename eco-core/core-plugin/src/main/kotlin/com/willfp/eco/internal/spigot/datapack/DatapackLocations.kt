package com.willfp.eco.internal.spigot.datapack

import org.bukkit.Bukkit
import java.io.File
import java.util.Properties

/**
 * Finds the server's datapack directory.
 *
 * Datapacks are server-scoped: they live in the level folder and are listed in `level.dat`. There
 * is no per-world installation on any platform at any supported version.
 *
 * The level name is read from `server.properties`, **not** `Bukkit.getWorlds()[0]`. The latter is
 * wrong when the first world's folder is dimension-nested.
 */
object DatapackLocations {
    private const val DEFAULT_LEVEL_NAME = "world"

    /**
     * Read `level-name` out of a `server.properties` file.
     */
    fun levelName(serverProperties: File): String {
        if (!serverProperties.isFile) {
            return DEFAULT_LEVEL_NAME
        }

        val properties = Properties()

        runCatching {
            serverProperties.inputStream().use { properties.load(it) }
        }.getOrElse { return DEFAULT_LEVEL_NAME }

        return properties.getProperty("level-name")?.takeIf { it.isNotBlank() } ?: DEFAULT_LEVEL_NAME
    }

    /**
     * The `datapacks` directory of the level, given the world container and `server.properties`.
     */
    fun datapacksDir(worldContainer: File, serverProperties: File): File =
        File(File(worldContainer, levelName(serverProperties)), "datapacks")

    /**
     * The `datapacks` directory of the running server.
     */
    fun datapacksDir(): File = datapacksDir(Bukkit.getWorldContainer(), File("server.properties"))

    /**
     * The pack directory name eco uses for a plugin.
     */
    fun packName(pluginId: String) = "eco_${pluginId.lowercase()}"
}
