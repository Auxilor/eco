package com.willfp.eco.core.lookup;

import com.willfp.eco.core.lookup.test.Testable;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Handle lookups, used in {@link com.willfp.eco.core.entities.Entities} and {@link com.willfp.eco.core.items.Items}.
 *
 * @param <T> The type of testable object, eg {@link com.willfp.eco.core.items.TestableItem}.
 */
public interface LookupHandler<T extends Testable<?>> {
    /**
     * Parse arguments to an object.
     *
     * @param args The arguments.
     * @return The object.
     */
    @NotNull
    T parse(@NotNull String[] args);

    /**
     * Validate an object.
     *
     * @param object The object.
     * @return If validated.
     */
    boolean validate(@NotNull T object);

    /**
     * Get the failsafe object.
     * <p>
     * A failsafe object should never pass {@link this#validate(Testable)}.
     *
     * @return The failsafe.
     */
    @NotNull
    T getFailsafe();

    /**
     * Join several options together.
     *
     * @param options The options.
     * @return The joined object.
     */
    @NotNull
    T join(@NotNull Collection<T> options);
}
