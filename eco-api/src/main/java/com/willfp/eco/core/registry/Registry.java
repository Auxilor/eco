package com.willfp.eco.core.registry;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * A registry for {@link Registrable}s.
 *
 * @param <T> The type of {@link Registrable}.
 */
public class Registry<T extends Registrable> implements Iterable<T> {
    /**
     * The ID pattern.
     */
    private static final Pattern ID_PATTERN = Pattern.compile("[a-z0-9_]{1,100}");

    /**
     * The registry.
     */
    private final Map<String, T> registry = new HashMap<>();

    /**
     * If the registry is locked.
     */
    private boolean isLocked = false;

    /**
     * The locker, used to 'secure' registries and prevent random unlocking.
     */
    @Nullable
    private Object locker = null;

    /**
     * Instantiate a new registry.
     */
    public Registry() {

    }

    /**
     * Register a new element.
     *
     * @param element The element to register.
     * @return The element.
     */
    @NotNull
    public T register(@NotNull final T element) {
        if (this.isLocked) {
            throw new IllegalStateException("Cannot add to locked registry! (ID: " + element.getID() + ")");
        }

        Preconditions.checkArgument(ID_PATTERN.matcher(element.getID()).matches(), "ID must match pattern: " + ID_PATTERN.pattern() + " (was " + element.getID() + ")");

        registry.put(element.getID(), element);

        element.onRegister();
        onRegister(element);

        return element;
    }

    /**
     * Remove an element.
     *
     * @param element The element.
     * @return The element.
     */
    public T remove(@NotNull final T element) {
        if (this.isLocked) {
            throw new IllegalStateException("Cannot remove from locked registry! (ID: " + element.getID() + ")");
        }

        element.onRemove();
        onRemove(element);

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
        if (this.isLocked) {
            throw new IllegalStateException("Cannot remove from locked registry! (ID: " + id + ")");
        }

        T element = registry.get(id);

        if (element != null) {
            return remove(element);
        }

        return null;
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
        for (T value : Set.copyOf(registry.values())) {
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

    /**
     * Get if the registry is locked.
     *
     * @return If the registry is locked.
     */
    public boolean isLocked() {
        return isLocked;
    }

    /**
     * Lock the registry.
     *
     * @param locker The locker.
     */
    public void lock(@Nullable final Object locker) {
        if (this.isLocked && this.locker != locker) {
            throw new IllegalArgumentException("Registry is already locked with a different locker!");
        }

        this.locker = locker;
        isLocked = true;
    }

    /**
     * Unlock the registry.
     *
     * @param locker The locker.
     */
    public void unlock(@Nullable final Object locker) {
        if (this.locker != locker) {
            throw new IllegalArgumentException("Cannot unlock registry!");
        }

        this.locker = null;
        isLocked = false;
    }

    /**
     * Run when an element is registered.
     *
     * @param element The element.
     */
    protected void onRegister(@NotNull final T element) {
        // Override this method to do something when an element is registered.
    }

    /**
     * Run when an element is removed.
     *
     * @param element The element.
     */
    protected void onRemove(@NotNull final T element) {
        // Override this method to do something when an element is removed.
    }

    /**
     * Get if the registry is empty.
     *
     * @return If the registry is empty.
     */
    public boolean isEmpty() {
        return registry.isEmpty();
    }

    /**
     * Get if the registry is not empty.
     *
     * @return If the registry is not empty.
     */
    public boolean isNotEmpty() {
        return !isEmpty();
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return values().iterator();
    }

    /**
     * Try to fit a string to the ID pattern.
     *
     * @param string The string.
     * @return The string in lowercase, but with all spaces, dots, and dashes replaced with underscores.
     */
    @NotNull
    public static String tryFitPattern(@NotNull final String string) {
        return string.replace(" ", "_")
                .replace(".", "_")
                .replace("-", "_")
                .toLowerCase(Locale.ENGLISH);
    }
}
