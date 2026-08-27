package com.willfp.eco.internal.spigot.datapack

import com.google.gson.JsonObject
import org.bukkit.Bukkit
import java.util.logging.Logger

/**
 * A datapack format version.
 */
data class PackFormat(val major: Int, val minor: Int = 0) {
    override fun toString() = "$major.$minor"
}

/**
 * Generates `pack.mcmeta`.
 *
 * The supported range must be derived from the running server, never hardcoded: a hardcoded maximum
 * reads as incompatible the moment Mojang bumps the format, and there is no version of eco that can
 * be released fast enough to keep up. A range is safe here only because eco does not own any
 * schemas; the consumer that does owns its own breakage.
 */
object PackMcmeta {
    /**
     * The data pack format of 1.21.8, the oldest version eco supports.
     */
    const val MIN_FORMAT = 81

    /**
     * Build a `pack.mcmeta` for a pack.
     *
     * @param description The pack description.
     * @param current     The format of the running server, or null if it could not be read.
     */
    fun generate(description: String, current: PackFormat?): String {
        // An undetected format must not clamp the range to MIN_FORMAT: that reads as incompatible on
        // every newer server, an incompatible pack is not enabled, and the content then never loads
        // at all. Open-ended is safe here because every entry has already been decoded through the
        // running server's own codec, so the content is known to be correct for whatever is running.
        val max = if (current == null) Int.MAX_VALUE else maxOf(current.major, MIN_FORMAT)

        val supported = JsonObject().apply {
            addProperty("min_inclusive", MIN_FORMAT)
            addProperty("max_inclusive", max)
        }

        val pack = JsonObject().apply {
            addProperty("description", description)
            addProperty("pack_format", MIN_FORMAT)
            add("supported_formats", supported)
            addProperty("min_format", MIN_FORMAT)
            addProperty("max_format", max)
        }

        val root = JsonObject().apply {
            add("pack", pack)
        }

        return JsonCanonicaliser.canonicalise(root.toString())
    }

    /**
     * Read the data pack format out of a server `version.json`.
     *
     * The shape changed at 1.21.9, from `{"data": 81}` to `{"data_major": 88, "data_minor": 0}`.
     * Both are read.
     *
     * @return The format, or null if the document does not carry one.
     */
    fun parseVersionJson(json: String): PackFormat? {
        val root = runCatching { JsonCanonicaliser.parseStrict(json) }.getOrNull() ?: return null

        if (!root.isJsonObject) {
            return null
        }

        val packVersion = root.asJsonObject.getAsJsonObject("pack_version") ?: return null

        if (packVersion.has("data_major")) {
            val major = packVersion.get("data_major").asInt
            val minor = if (packVersion.has("data_minor")) packVersion.get("data_minor").asInt else 0
            return PackFormat(major, minor)
        }

        if (packVersion.has("data")) {
            return PackFormat(packVersion.get("data").asInt)
        }

        return null
    }

    /**
     * Read the format of the running server, out of the `version.json` bundled in the server jar.
     *
     * @return The format, or null if it could not be read.
     */
    fun currentFormat(logger: Logger): PackFormat? {
        val json = serverResource("version.json")
        val parsed = json?.let { parseVersionJson(it) }

        if (parsed == null) {
            logger.warning(
                "Could not read the server's data pack format from version.json; datapacks will " +
                        "declare an open-ended format range instead."
            )

            return null
        }

        return parsed
    }

    private fun serverResource(name: String): String? {
        for (loader in candidateLoaders()) {
            val content = runCatching {
                loader.getResourceAsStream(name)?.use { it.readBytes().toString(Charsets.UTF_8) }
            }.getOrNull()

            if (content != null) {
                return content
            }
        }

        return null
    }

    /**
     * Loaders that might see the server jar's resources, most specific first.
     *
     * Reflection rather than a direct reference, so that core-plugin keeps no compile-time
     * dependency on NMS: only the loader is wanted, never the class.
     */
    private fun candidateLoaders(): List<ClassLoader> = listOfNotNull(
        runCatching { Bukkit.getServer().javaClass.classLoader }.getOrNull(),
        runCatching { Class.forName("net.minecraft.SharedConstants").classLoader }.getOrNull(),
        runCatching { ClassLoader.getSystemClassLoader() }.getOrNull(),
        PackMcmeta::class.java.classLoader
    ).distinct()
}
