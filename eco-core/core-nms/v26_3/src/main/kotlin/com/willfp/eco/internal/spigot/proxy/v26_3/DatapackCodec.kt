package com.willfp.eco.internal.spigot.proxy.v26_3

import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.mojang.serialization.DynamicOps
import com.mojang.serialization.JsonOps
import com.willfp.eco.internal.spigot.proxies.DatapackCodecProxy
import com.willfp.eco.internal.spigot.proxies.PendingContent
import java.util.Optional
import net.minecraft.core.Holder
import net.minecraft.core.HolderGetter
import net.minecraft.core.HolderLookup
import net.minecraft.core.HolderOwner
import net.minecraft.core.HolderSet
import net.minecraft.core.Registry
import net.minecraft.core.registries.Registries
import net.minecraft.resources.RegistryDataLoader
import net.minecraft.resources.RegistryOps
import net.minecraft.resources.ResourceKey
import net.minecraft.tags.TagKey
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.CraftServer

/**
 * Codec-level datapack validation.
 *
 * Same as the shared implementation, except 26.3 renamed `WORLDGEN_REGISTRIES` to
 * `WORLD_REGISTRIES` and removed `RegistryOps.RegistryInfo`:
 * `RegistryInfoLookup.lookup` now returns the `HolderGetter` directly, and every `HolderGetter`
 * is its own `HolderOwner`.
 */
class DatapackCodec : DatapackCodecProxy {
    private val registries: Map<String, RegistryDataLoader.RegistryData<*>> by lazy {
        (RegistryDataLoader.WORLD_REGISTRIES + RegistryDataLoader.DIMENSION_REGISTRIES)
            .associateBy { Registries.elementsDirPath(it.key()) }
    }

    override fun validatableRegistries(): Set<String> = registries.keys

    override fun validate(registryDirPath: String, json: String, pending: PendingContent): String? {
        val data = registries[registryDirPath] ?: return null

        // Registry-aware ops, so that fields referencing other registry entries resolve instead of
        // failing as "can't parse without registry ops".
        val ops = registryOps(pending) ?: return null

        val element = try {
            JsonParser.parseString(json)
        } catch (e: Exception) {
            return e.message ?: "malformed JSON"
        }

        return try {
            data.elementCodec()
                .parse(ops, element)
                .error()
                .map { it.message() }
                .orElse(null)
        } catch (e: Throwable) {
            /*
            A placeholder holder for pending content is unbound: a codec that dereferences one
            during decode, rather than merely resolving it, throws. With nothing pending there is
            no placeholder to blame, so the throw is the answer and the entry is refused.

            With content pending the entry keeps only its syntax and path checks, because refusing
            it would fail exactly the publishes this exists to support. That is weaker than a real
            decode, so it is logged: the alternative is a decode bug that never surfaces.
             */
            if (pending.isEmpty()) {
                "codec validation threw (${e.message})"
            } else {
                Bukkit.getLogger().warning(
                    "[eco] $registryDirPath entry fell back to syntax and path checks only: codec " +
                        "decode threw while resolving content this publish is writing ($e)"
                )

                null
            }
        }
    }

    private fun registryOps(pending: PendingContent): DynamicOps<JsonElement>? = runCatching {
        val live = (Bukkit.getServer() as CraftServer).server
            .registryAccess()
            .createSerializationContext(JsonOps.INSTANCE)

        if (pending.isEmpty()) {
            live
        } else {
            RegistryOps.create(JsonOps.INSTANCE, DraftAwareLookup(live.lookupProvider, pending))
        }
    }.getOrNull()

    /**
     * Wraps the live registry lookup so that references to content the same publish is writing
     * resolve to a placeholder instead of an error.
     */
    private class DraftAwareLookup(
        private val delegate: RegistryOps.RegistryInfoLookup,
        private val pending: PendingContent
    ) : RegistryOps.RegistryInfoLookup {
        override fun <T : Any> lookup(
            registryKey: ResourceKey<out Registry<out T>>
        ): Optional<HolderGetter<T>> {
            val getter = delegate.lookup(registryKey)

            if (getter.isEmpty) {
                return getter
            }

            val dir = Registries.elementsDirPath(registryKey)
            val elements = pending.elementsIn(dir)
            val tags = pending.tagsIn(dir)

            if (elements.isEmpty() && tags.isEmpty()) {
                return getter
            }

            return getter.map { DraftAwareGetter(it, elements, tags) }
        }

        override fun lookupForValueCopyViaBuilders(): HolderLookup.Provider =
            delegate.lookupForValueCopyViaBuilders()
    }

    private class DraftAwareGetter<T : Any>(
        private val live: HolderGetter<T>,
        private val elements: Set<String>,
        private val tags: Set<String>
    ) : HolderGetter<T> {
        override fun get(key: ResourceKey<T>): Optional<Holder.Reference<T>> {
            val found = live.get(key)

            if (found.isPresent || idOf(key) !in elements) {
                return found
            }

            return Optional.of(Holder.Reference.createStandAlone(live, key))
        }

        override fun get(key: TagKey<T>): Optional<HolderSet.Named<T>> {
            val found = live.get(key)

            if (found.isPresent || idOf(key) !in tags) {
                return found
            }

            return Optional.of(HolderSet.emptyNamed(live, key))
        }

        // Holders stay owned by the live getter, so serialization checks must answer as it would.
        override fun canSerialize(owner: HolderOwner<T>): Boolean = live.canSerialize(owner)
    }

    private companion object {
        /**
         * The ID part of a `ResourceKey` or `TagKey`, read out of `toString()`.
         *
         * `ResourceKey[minecraft:worldgen/biome / ns:key]` and
         * `TagKey[registry=minecraft:worldgen/biome, location=ns:key]` both end with the ID. If a
         * future format change breaks the parse, the miss is a no-match, which leaves validation
         * exactly as strict as it was before this existed.
         */
        fun idOf(key: Any): String {
            val text = key.toString().removeSuffix("]")

            return text.substringAfterLast(" / ")
                .substringAfterLast('=')
                .trim()
        }
    }
}
