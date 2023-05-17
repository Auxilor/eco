package com.willfp.eco.core.integrations;

import com.willfp.eco.core.Eco;
import com.willfp.eco.core.registry.Registry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Registry for integrations.
 *
 * @param <T> The type of integration.
 */
public class IntegrationRegistry<T extends Integration> extends Registry<T> {
    /**
     * Create a new integration registry.
     */
    public IntegrationRegistry() {
        super();
    }

    @Override
    public @NotNull T register(@NotNull final T element) {
        return executeSafely(() -> super.register(element), element);
    }

    /**
     * Iterate over all integrations, safely.
     *
     * @param action The action to perform.
     */
    public void forEachSafely(@NotNull final Consumer<T> action) {
        for (T integration : new HashSet<>(this.values())) {
            executeSafely(() -> action.accept(integration), integration);
        }
    }

    /**
     * If any integrations return true, safely.
     *
     * @param predicate The predicate to test.
     * @return If any integrations return true.
     */
    public boolean anySafely(@NotNull final Predicate<T> predicate) {
        for (T integration : new HashSet<>(this.values())) {
            Boolean result = executeSafely(() -> predicate.test(integration), integration);
            if (result != null && result) {
                return true;
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
    @NotNull
    public <R> R firstSafely(@NotNull final Function<T, R> function,
                             @NotNull final R defaultValue) {
        if (this.isEmpty()) {
            return defaultValue;
        }

        T integration = this.iterator().next();

        return executeSafely(() -> function.apply(integration), integration, defaultValue);
    }

    /**
     * Executes a given action safely, catching any exceptions and logging the issue.
     *
     * @param action      The action to execute.
     * @param integration The integration to apply the action on.
     */
    private void executeSafely(@NotNull final Runnable action,
                               @NotNull final T integration) {
        executeSafely(() -> {
            action.run();
            return null;
        }, integration);
    }

    /**
     * Executes a given action safely, catching any exceptions and logging the issue.
     *
     * @param action      The action to execute.
     * @param integration The integration to apply the action on.
     * @param <R>         The return type of the action.
     * @return The result of the action, or null if an exception was thrown.
     */
    private <R> R executeSafely(@NotNull final Supplier<R> action,
                                @NotNull final T integration) {
        return executeSafely(action, integration, null);
    }

    /**
     * Executes a given action safely, catching any exceptions and logging the issue.
     *
     * @param action       The action to execute.
     * @param integration  The integration to apply the action on.
     * @param defaultValue The default value to return if an exception is thrown.
     * @param <R>          The return type of the action.
     * @return The result of the action, or the default value if an exception was thrown.
     */
    private <R> R executeSafely(@NotNull final Supplier<R> action,
                                @NotNull final T integration,
                                @Nullable final R defaultValue) {
        try {
            return action.get();
        } catch (final Exception e) {
            Eco.get().getEcoPlugin().getLogger().warning("Integration for " + integration.getPluginName() + " threw an exception!");
            Eco.get().getEcoPlugin().getLogger().warning("The integration will be disabled.");
            e.printStackTrace();
            this.remove(integration);
            return defaultValue;
        }
    }

    /**
     * If all integrations return true, safely.
     *
     * @param predicate The predicate to test.
     * @return If all integrations return true.
     */
    public boolean allSafely(@NotNull final Predicate<T> predicate) {
        return !this.anySafely(predicate.negate());
    }
}
