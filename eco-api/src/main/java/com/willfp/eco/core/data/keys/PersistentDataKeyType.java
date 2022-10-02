package com.willfp.eco.core.data.keys;

import com.willfp.eco.core.config.interfaces.Config;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * All storable data key types.
 *
 * @param <T> The type.
 */
public final class PersistentDataKeyType<T> {
    /**
     * The registered key types.
     */
    private static final List<PersistentDataKeyType<?>> VALUES = new ArrayList<>();

    /**
     * String.
     */
    public static final PersistentDataKeyType<String> STRING = new PersistentDataKeyType<>(String.class, "STRING");

    /**
     * Boolean.
     */
    public static final PersistentDataKeyType<Boolean> BOOLEAN = new PersistentDataKeyType<>(Boolean.class, "BOOLEAN");

    /**
     * Int.
     */
    public static final PersistentDataKeyType<Integer> INT = new PersistentDataKeyType<>(Integer.class, "INT");

    /**
     * Double.
     */
    public static final PersistentDataKeyType<Double> DOUBLE = new PersistentDataKeyType<>(Double.class, "DOUBLE");

    /**
     * String List.
     */
    public static final PersistentDataKeyType<List<String>> STRING_LIST = new PersistentDataKeyType<>(null, "STRING_LIST");

    /**
     * Config.
     */
    public static final PersistentDataKeyType<Config> CONFIG = new PersistentDataKeyType<>(Config.class, "CONFIG");

    /**
     * The class of the type.
     */
    private final Class<T> typeClass;

    /**
     * The name of the key type.
     */
    private final String name;

    /**
     * Get the class of the type.
     *
     * @return The class.
     * @deprecated String list type will return null.
     */
    @Deprecated(since = "6.36.0", forRemoval = true)
    @Nullable
    public Class<T> getTypeClass() {
        return typeClass;
    }

    /**
     * Get the name of the key type.
     *
     * @return The name.
     */
    public String name() {
        return name;
    }

    /**
     * Create new PersistentDataKeyType.
     *
     * @param typeClass The type class.
     * @param name      The name.
     */
    private PersistentDataKeyType(@Nullable final Class<T> typeClass,
                                  @NotNull final String name) {
        VALUES.add(this);

        this.typeClass = typeClass;
        this.name = name;
    }

    @Override
    public boolean equals(@Nullable final Object that) {
        if (this == that) {
            return true;
        }
        if (!(that instanceof PersistentDataKeyType type)) {
            return false;
        }
        return Objects.equals(this.name, type.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name);
    }

    /**
     * Get all registered {@link PersistentDataKeyType}s.
     *
     * @return The registered types.
     */
    @NotNull
    public static PersistentDataKeyType<?>[] values() {
        return VALUES.toArray(new PersistentDataKeyType[0]);
    }

    /**
     * Get a key type from a name.
     *
     * @param name The name.
     * @return The type, or null if not found.
     */
    @Nullable
    public static PersistentDataKeyType<?> valueOf(@NotNull final String name) {
        for (PersistentDataKeyType<?> type : VALUES) {
            if (type.name.equalsIgnoreCase(name)) {
                return type;
            }
        }

        return null;
    }
}
