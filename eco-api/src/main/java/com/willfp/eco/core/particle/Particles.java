package com.willfp.eco.core.particle;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.particle.impl.EmptyParticle;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Static facade for the particle registry.
 */
public final class Particles {

    private Particles() {
        throw new UnsupportedOperationException("This is a utility class.");
    }

    private static final Map<NamespacedKey, RegisteredParticle> REGISTRY = new ConcurrentHashMap<>();

    private record ConfigSource(EcoPlugin plugin, String configKey) {}
    private static final List<ConfigSource> CONFIG_SOURCES = new CopyOnWriteArrayList<>();

    @Nullable
    private static volatile Function<String, SpawnableParticle> stringResolver = null;

    @Nullable
    private static volatile BiConsumer<EcoPlugin, String> configLoader = null;

    @Nullable
    private static volatile Runnable cacheInvalidator = null;

    /** Internal: install the string resolver. Called once by core-backend. */
    public static void installStringResolver(@NotNull Function<String, SpawnableParticle> resolver) {
        stringResolver = resolver;
    }

    /** Internal: install the config loader. Called once by core-backend. */
    public static void installConfigLoader(@NotNull BiConsumer<EcoPlugin, String> loader) {
        configLoader = loader;
    }

    /** Internal: install the cache invalidator hook. */
    public static void installCacheInvalidator(@NotNull Runnable invalidator) {
        cacheInvalidator = invalidator;
    }

    private static void invalidateCache() {
        Runnable inv = cacheInvalidator;
        if (inv != null) {
            inv.run();
        }
    }

    /** Register a programmatically-built particle. Origin is {@link ParticleOrigin#PLUGIN}. */
    public static void register(@NotNull NamespacedKey key, @NotNull SpawnableParticle particle) {
        register(key, particle, null);
    }

    /** Register with explicit owner plugin (for diagnostics). Origin is {@link ParticleOrigin#PLUGIN}. */
    public static void register(@NotNull NamespacedKey key,
                                @NotNull SpawnableParticle particle,
                                @Nullable Plugin owner) {
        invalidateCache();
        REGISTRY.put(key, new RegisteredParticle(key, particle, ParticleOrigin.PLUGIN, owner));
    }

    /** Internal: register with explicit origin. Used by the config loader. */
    public static void registerInternal(@NotNull NamespacedKey key,
                                        @NotNull SpawnableParticle particle,
                                        @NotNull ParticleOrigin origin,
                                        @Nullable Plugin owner) {
        invalidateCache();
        REGISTRY.put(key, new RegisteredParticle(key, particle, origin, owner));
    }

    /** Lookup by namespaced key. Returns {@link EmptyParticle#INSTANCE} on miss. */
    @NotNull
    public static SpawnableParticle lookup(@NotNull NamespacedKey key) {
        RegisteredParticle entry = REGISTRY.get(key);
        return entry != null ? entry.particle() : EmptyParticle.INSTANCE;
    }

    /** Lookup by single-token string. Returns {@link EmptyParticle#INSTANCE} on miss. */
    @NotNull
    public static SpawnableParticle lookup(@NotNull String key) {
        Function<String, SpawnableParticle> resolver = stringResolver;
        if (resolver == null) {
            return EmptyParticle.INSTANCE;
        }
        SpawnableParticle result = resolver.apply(key);
        return result != null ? result : EmptyParticle.INSTANCE;
    }

    /** Lookup with null-on-miss semantics. */
    @Nullable
    public static SpawnableParticle find(@NotNull NamespacedKey key) {
        RegisteredParticle entry = REGISTRY.get(key);
        return entry != null ? entry.particle() : null;
    }

    /** Bulk-load every entry under {@code configKey} from {@code plugin}'s configs. */
    public static void loadFromConfig(@NotNull EcoPlugin plugin, @NotNull String configKey) {
        ConfigSource src = new ConfigSource(plugin, configKey);
        if (!CONFIG_SOURCES.contains(src)) {
            CONFIG_SOURCES.add(src);
        }

        BiConsumer<EcoPlugin, String> loader = configLoader;
        if (loader != null) {
            loader.accept(plugin, configKey);
        }
    }

    /** Clear all CONFIG-origin entries and replay every recorded source. */
    public static void reloadConfigs() {
        invalidateCache();
        REGISTRY.values().removeIf(it -> it.origin() == ParticleOrigin.CONFIG);
        BiConsumer<EcoPlugin, String> loader = configLoader;
        if (loader != null) {
            for (ConfigSource src : CONFIG_SOURCES) {
                loader.accept(src.plugin(), src.configKey());
            }
        }
    }

    /** Remove a particle from the registry regardless of origin. */
    public static void unregister(@NotNull NamespacedKey key) {
        invalidateCache();
        REGISTRY.remove(key);
    }

    /** @return Snapshot of every registered particle. */
    @NotNull
    public static List<RegisteredParticle> dump() {
        return List.copyOf(REGISTRY.values());
    }

    /** Internal: read-only registry view. */
    @NotNull
    public static Map<NamespacedKey, RegisteredParticle> registryView() {
        return Collections.unmodifiableMap(REGISTRY);
    }
}