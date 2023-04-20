package com.willfp.eco.core.data;

import org.jetbrains.annotations.NotNull;

/**
 * An adapter for objects stored in {@link ExternalDataStore}.
 *
 * @param <A> The accessed class.
 * @param <S> The stored class.
 */
public abstract class ExternalDataStoreObjectAdapter<A, S> {
    /**
     * The class that is accessed (read / written).
     */
    private final Class<? extends A> accessedClass;

    /**
     * The class that is stored internally.
     */
    private final Class<? extends S> storedClass;

    /**
     * Create a new adapter.
     *
     * @param accessedClass The class that is accessed (read / written).
     * @param storedClass   The class that is stored internally.
     */
    protected ExternalDataStoreObjectAdapter(@NotNull final Class<? extends A> accessedClass,
                                             @NotNull final Class<? extends S> storedClass) {
        this.accessedClass = accessedClass;
        this.storedClass = storedClass;
    }

    /**
     * Convert an object to the stored object.
     *
     * @param obj The object.
     * @return The stored object.
     */
    @NotNull
    public abstract S toStoredObject(@NotNull final A obj);

    /**
     * Convert an object to the accessed object.
     *
     * @param obj The object.
     * @return The accessed object.
     */
    @NotNull
    public abstract A toAccessedObject(@NotNull final S obj);

    /**
     * Get the class that is accessed (read / written).
     *
     * @return The class.
     */
    public Class<? extends A> getAccessedClass() {
        return accessedClass;
    }

    /**
     * Get the class that is stored internally.
     *
     * @return The class.
     */
    public Class<? extends S> getStoredClass() {
        return storedClass;
    }
}
