package com.willfp.eco.core.data.keys;

import com.willfp.eco.core.config.interfaces.Config;
import com.willfp.eco.core.data.handlers.DataTypeSerializer;
import com.willfp.eco.core.data.handlers.PersistentDataHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
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
    public static final PersistentDataKeyType<String> STRING = new PersistentDataKeyType<>("STRING");

    /**
     * Boolean.
     */
    public static final PersistentDataKeyType<Boolean> BOOLEAN = new PersistentDataKeyType<>("BOOLEAN");

    /**
     * Int.
     */
    public static final PersistentDataKeyType<Integer> INT = new PersistentDataKeyType<>("INT");

    /**
     * Double.
     */
    public static final PersistentDataKeyType<Double> DOUBLE = new PersistentDataKeyType<>("DOUBLE");

    /**
     * String List.
     */
    public static final PersistentDataKeyType<List<String>> STRING_LIST = new PersistentDataKeyType<>("STRING_LIST");

    /**
     * Config.
     */
    public static final PersistentDataKeyType<Config> CONFIG = new PersistentDataKeyType<>("CONFIG");

    /**
     * Big Decimal.
     */
    public static final PersistentDataKeyType<BigDecimal> BIG_DECIMAL = new PersistentDataKeyType<>("BIG_DECIMAL");

    /**
     * The name of the key type.
     */
    private final String name;

    /**
     * The serializers for this key type.
     */
    private final Map<PersistentDataHandler, DataTypeSerializer<T>> serializers = new HashMap<>();

    /**
     * Create new PersistentDataKeyType.
     *
     * @param name The name.
     */
    private PersistentDataKeyType(@NotNull final String name) {
        VALUES.add(this);

        this.name = name;
    }

    /**
     * Get the name of the key type.
     *
     * @return The name.
     */
    @NotNull
    public String name() {
        return name;
    }

    /**
     * Register a serializer for this key type.
     *
     * @param handler    The handler.
     * @param serializer The serializer.
     */
    public void registerSerializer(@NotNull final PersistentDataHandler handler,
                                   @NotNull final DataTypeSerializer<T> serializer) {
        this.serializers.put(handler, serializer);
    }

    /**
     * Get the serializer for a handler.
     *
     * @param handler The handler.
     * @return The serializer.
     */
    @NotNull
    public DataTypeSerializer<T> getSerializer(@NotNull final PersistentDataHandler handler) {
        DataTypeSerializer<T> serializer = this.serializers.get(handler);

        if (serializer == null) {
            throw new NoSuchElementException("No serializer for handler: " + handler);
        }

        return serializer;
    }

    @Override
    public boolean equals(@Nullable final Object that) {
        if (this == that) {
            return true;
        }
        if (!(that instanceof PersistentDataKeyType<?> type)) {
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
