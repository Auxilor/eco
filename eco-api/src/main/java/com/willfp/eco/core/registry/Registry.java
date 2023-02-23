package com.willfp.eco.core.registry;

import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * A registry for {@link Registrable}s.
 *
 * @param <T> The type of {@link Registrable}.
 */
public abstract class Registry<T extends Registrable> {
    /**
     * The ID pattern.
     */
    private static final Pattern ID_PATTERN = Pattern.compile("[a-z0-9_]{1,100}");

    /**
     * The registry.
     */
    private final Map<String, T> registry = new HashMap<>();

    /**
     * Instantiate a new registry.
     */
    protected Registry() {

    }

    /**
     * Register a new element.
     *
     * @param element The element to register.
     * @return The element.
     */
    @NotNull
    public T register(@NotNull final T element) {
        Validate.isTrue(ID_PATTERN.matcher(element.getID()).matches(), "ID must match pattern: " + ID_PATTERN.pattern());

        registry.put(element.getID(), element);

        element.onRegister();

        return element;
    }

    /**
     * Remove an element.
     *
     * @param element The element.
     * @return The element.
     */
    public T remove(@NotNull final T element) {
        element.onRemove();

        registry.remove(element.getID());

        return element;
    }

    /**
     * Remove an element by ID.
     *
     * @param id The ID.
     * @return The element.
     */
    @Nullable
    public T remove(@NotNull final String id) {
        T element = registry.get(id);

        if (element != null) {
            element.onRemove();
        }

        return registry.remove(id);
    }

    /**
     * Get an element by ID.
     *
     * @param id The ID.
     * @return The element, or null if not found.
     */
    @Nullable
    public T get(@NotNull final String id) {
        return registry.get(id);
    }

    /**
     * Clear the registry.
     */
    public void clear() {
        for (T value : registry.values()) {
            remove(value);
        }
    }

    /**
     * Get all elements.
     *
     * @return All elements.
     */
    public Set<T> values() {
        return Set.copyOf(registry.values());
    }
}
