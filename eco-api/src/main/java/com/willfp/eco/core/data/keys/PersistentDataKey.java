package com.willfp.eco.core.data.keys;

import com.willfp.eco.core.Eco;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;

/**
 * A persistent data key is a key with a type that can be stored about an offline player.
 *
 * @param <T> The type of the data.
 */
public class PersistentDataKey<T> {
    /**
     * The key of the persistent data value.
     */
    private final NamespacedKey key;

    /**
     * The default value for the key.
     */
    private final T defaultValue;

    /**
     * The persistent data key type.
     */
    private final PersistentDataKeyType<T> type;

    /**
     * If the key is for server data.
     */
    private final boolean isServerKey;

    /**
     * Create a new Persistent Data Key.
     *
     * @param key          The key.
     * @param type         The data type.
     * @param defaultValue The default value.
     */
    public PersistentDataKey(@NotNull final NamespacedKey key,
                             @NotNull final PersistentDataKeyType<T> type,
                             @NotNull final T defaultValue) {
        this(key, type, defaultValue, false);
    }

    /**
     * Create a new Persistent Data Key.
     *
     * @param key          The key.
     * @param type         The data type.
     * @param defaultValue The default value.
     * @param isServerKey  If the key is for server data.
     */
    public PersistentDataKey(@NotNull final NamespacedKey key,
                             @NotNull final PersistentDataKeyType<T> type,
                             @NotNull final T defaultValue,
                             final boolean isServerKey) {
        this.key = key;
        this.defaultValue = defaultValue;
        this.type = type;
        this.isServerKey = isServerKey;

        Eco.getHandler().getKeyRegistry().registerKey(this);
    }

    @Override
    public String toString() {
        return "PersistentDataKey{"
                + "key=" + key
                + ", defaultValue=" + defaultValue
                + ", type=" + type
                + '}';
    }

    /**
     * Get the key.
     *
     * @return The key.
     */
    public NamespacedKey getKey() {
        return this.key;
    }

    /**
     * Get the default value.
     *
     * @return The default value.
     */
    public T getDefaultValue() {
        return this.defaultValue;
    }

    /**
     * Get the data key type.
     *
     * @return The key type.
     */
    public PersistentDataKeyType<T> getType() {
        return this.type;
    }

    /**
     * Get if the key is for server data.
     *
     * @return If server key.
     */
    public boolean isServerKey() {
        return isServerKey;
    }

    /**
     * Get all persistent data keys.
     *
     * @return The keys.
     */
    public static Set<PersistentDataKey<?>> values() {
        return Eco.getHandler().getKeyRegistry().getRegisteredKeys();
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PersistentDataKey that)) {
            return false;
        }
        return Objects.equals(this.getKey(), that.getKey());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getKey());
    }
}
