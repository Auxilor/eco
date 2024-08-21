package com.willfp.eco.core.data.handlers;

import com.willfp.eco.core.data.keys.PersistentDataKey;
import com.willfp.eco.core.registry.Registrable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

public abstract class PersistentDataHandler implements Registrable {
    /**
     * The id of the handler.
     */
    private final String id;

    /**
     * Create a new persistent data handler.
     *
     * @param id The id of the handler.
     */
    protected PersistentDataHandler(@NotNull final String id) {
        this.id = id;
    }

    @Override
    public @NotNull String getID() {
        return id;
    }

    /**
     * Read a key from persistent data.
     *
     * @param uuid The uuid.
     * @param key  The key.
     * @param <T>  The type of the key.
     * @return The value, or null if not found.
     */
    @Nullable
    public abstract <T> T read(@NotNull UUID uuid, @NotNull PersistentDataKey<T> key);

    /**
     * Write a key to persistent data.
     *
     * @param uuid  The uuid.
     * @param key   The key.
     * @param value The value.
     * @param <T>   The type of the key.
     */
    public abstract <T> void write(@NotNull UUID uuid, @NotNull PersistentDataKey<T> key, @NotNull T value);

    /**
     * Serialize data.
     *
     * @param keys The keys to serialize.
     * @return The serialized data.
     */
    @NotNull
    public abstract Set<SerializedProfile> serializeData(@NotNull final Set<PersistentDataKey<?>> keys);

    /**
     * Load profile data.
     *
     * @param data The data.
     */
    public abstract void loadProfileData(@NotNull Set<SerializedProfile> data);
}
