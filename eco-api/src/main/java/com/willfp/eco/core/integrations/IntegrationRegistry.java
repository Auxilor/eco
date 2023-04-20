package com.willfp.eco.core.integrations;

import com.willfp.eco.core.Eco;
import com.willfp.eco.core.registry.Registry;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Registry for integrations.
 *
 * @param <T> The type of integration.
 */
public class IntegrationRegistry<T extends Integration> extends Registry<T> {
    /**
     * Iterate over all integrations, safely.
     *
     * @param action The action to perform.
     */
    public void forEachSafely(@NotNull final Consumer<T> action) {
        for (T integration : new HashSet<>(this.values())) {
            try {
                action.accept(integration);
            } catch (final Exception e) {
                Eco.get().getEcoPlugin().getLogger().warning("Integration for " + integration.getPluginName() + " threw an exception!");
                Eco.get().getEcoPlugin().getLogger().warning("The integration will be disabled.");
                e.printStackTrace();
                this.remove(integration);
            }
        }
    }

    /**
     * If any integrations return true, safely.
     *
     * @param predicate The predicate to test.
     */
    public boolean anySafely(@NotNull final Predicate<T> predicate) {
        for (T integration : new HashSet<>(this.values())) {
            try {
                if (predicate.test(integration)) {
                    return true;
                }
            } catch (final Exception e) {
                Eco.get().getEcoPlugin().getLogger().warning("Integration for " + integration.getPluginName() + " threw an exception!");
                Eco.get().getEcoPlugin().getLogger().warning("The integration will be disabled.");
                e.printStackTrace();
                this.remove(integration);
            }
        }

        return false;
    }

    /**
     * Get the first integration that returns a value, safely.
     *
     * @param function     The function to apply.
     * @param defaultValue The default value.
     * @param <R>          The type of value.
     * @return The first value that returns a value.
     */
    public <R> R firstSafely(@NotNull final R defaultValue,
                             @NotNull final Function<T, R> function) {
        if (this.values().isEmpty()) {
            return defaultValue;
        }

        T integration = this.iterator().next();

        try {
            return function.apply(integration);
        } catch (final Exception e) {
            Eco.get().getEcoPlugin().getLogger().warning("Integration for " + integration.getPluginName() + " threw an exception!");
            Eco.get().getEcoPlugin().getLogger().warning("The integration will be disabled.");
            e.printStackTrace();
            this.remove(integration);
        }

        return defaultValue;
    }

    /**
     * If all integrations return true, safely.
     *
     * @param predicate The predicate to test.
     */
    public boolean allSafely(@NotNull final Predicate<T> predicate) {
        return !this.anySafely(predicate.negate());
    }
}
