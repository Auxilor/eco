package com.willfp.eco.core.data.keys;

import com.willfp.eco.core.Eco;
import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

/**
 * A persistent data key is a key with a type that can be stored about an offline player.
 *
 * @param <T> The type of the data.
 */
public class PersistentDataKey<T> {
    /**
     * The key of the persistent data value.
     */
    @Getter
    private final NamespacedKey key;

    /**
     * The default value for the key.
     */
    @Getter
    private final T defaultValue;

    /**
     * The persistent data key type.
     */
    @Getter
    private final PersistentDataKeyType type;

    /**
     * Create a new Persistent Data Key.
     *
     * @param key          The key.
     * @param type         The data type.
     * @param defaultValue The default value.
     */
    public PersistentDataKey(@NotNull final NamespacedKey key,
                             @NotNull final PersistentDataKeyType type,
                             @NotNull final T defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
        this.type = type;

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
}
