package com.willfp.eco.core.data;

import com.willfp.eco.core.Eco;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Persistent data container wrapper that allows for full string (non-namespaced) keys.
 */
public interface ExtendedPersistentDataContainer {
    /**
     * Set a key.
     *
     * @param key      The key.
     * @param dataType The data type.
     * @param value    The value.
     * @param <T>      The type.
     * @param <Z>      The type.
     */
    <T, Z> void set(@NotNull String key, @NotNull PersistentDataType<T, Z> dataType, @NotNull Z value);

    /**
     * Get if there is a key.
     *
     * @param key      The key.
     * @param dataType The data type.
     * @param <T>      The type.
     * @param <Z>      The type.
     * @return If the key is present.
     */
    <T, Z> boolean has(@NotNull String key, @NotNull PersistentDataType<T, Z> dataType);

    /**
     * Get a value.
     *
     * @param key      The key.
     * @param dataType The data type.
     * @param <T>      The type.
     * @param <Z>      The type.
     * @return The value, or null if not found.
     */
    @Nullable <T, Z> Z get(@NotNull String key, @NotNull PersistentDataType<T, Z> dataType);

    /**
     * Get a value or default if not present.
     *
     * @param key          The key.
     * @param dataType     The data type.
     * @param defaultValue The default value.
     * @param <T>          The type.
     * @param <Z>          The type.
     * @return The value, or the default if not found.
     */
    @NotNull <T, Z> Z getOrDefault(@NotNull String key, @NotNull PersistentDataType<T, Z> dataType, @NotNull Z defaultValue);

    /**
     * Get all keys, including namespaced keys.
     *
     * @return The keys.
     */
    @NotNull
    Set<String> getAllKeys();

    /**
     * Remove a key.
     *
     * @param key The key.
     */
    void remove(@NotNull String key);

    /**
     * Get the base PDC.
     *
     * @return The base.
     */
    @NotNull
    PersistentDataContainer getBase();

    /**
     * Get extension for PersistentDataContainers to add non-namespaced keys.
     *
     * @param base The base container.
     * @return The extended container.
     */
    static ExtendedPersistentDataContainer extend(@NotNull PersistentDataContainer base) {
        return Eco.get().adaptPdc(base);
    }

    /**
     * Create a new extended container.
     *
     * @return The extended container.
     */
    static ExtendedPersistentDataContainer create() {
        return extend(Eco.get().newPdc());
    }
}
