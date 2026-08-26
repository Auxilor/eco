package com.willfp.eco.internal.spigot.proxy.v1_21_8

import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.mojang.serialization.DynamicOps
import com.mojang.serialization.JsonOps
import com.willfp.eco.internal.spigot.proxies.DatapackCodecProxy
import net.minecraft.core.registries.Registries
import net.minecraft.resources.RegistryDataLoader
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.CraftServer

/**
 * Codec-level datapack validation.
 *
 * JSON syntax and path shape checks pass a bad enum value or a missing required field straight
 * through to a server that then refuses to boot. This decodes the entry through the server's own
 * codec: the same one that would reject it at startup.
 *
 * Only two symbols are needed, and both are stable across every supported version:
 * `RegistryData.key()` and `RegistryData.elementCodec()`. `requiredNonEmpty()`, which became
 * `validator()` at 26.1.2, is deliberately not touched, and neither is `ResourceLocation`, which
 * became `Identifier` at 1.21.11. That is what lets one implementation serve all versions.
 */
class DatapackCodec : DatapackCodecProxy {
    private val registries: Map<String, RegistryDataLoader.RegistryData<*>> by lazy {
        (RegistryDataLoader.WORLDGEN_REGISTRIES + RegistryDataLoader.DIMENSION_REGISTRIES)
            .associateBy { Registries.elementsDirPath(it.key()) }
    }

    override fun validatableRegistries(): Set<String> = registries.keys

    override fun validate(registryDirPath: String, json: String): String? {
        val data = registries[registryDirPath] ?: return null

        // Registry-aware ops, so that fields referencing other registry entries resolve instead of
        // failing as "can't parse without registry ops".
        val ops = registryOps() ?: return null

        val element = try {
            JsonParser.parseString(json)
        } catch (e: Exception) {
            return e.message ?: "malformed JSON"
        }

        return data.elementCodec()
            .parse(ops, element)
            .error()
            .map { it.message() }
            .orElse(null)
    }

    private fun registryOps(): DynamicOps<JsonElement>? = runCatching {
        (Bukkit.getServer() as CraftServer).server
            .registryAccess()
            .createSerializationContext(JsonOps.INSTANCE)
    }.getOrNull()
}
