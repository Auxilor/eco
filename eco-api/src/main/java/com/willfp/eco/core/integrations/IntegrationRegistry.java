package com.willfp.eco.core.integrations;

import com.willfp.eco.core.Eco;
import com.willfp.eco.core.registry.Registry;
import java.util.HashSet;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Registry for integrations.
 * <p>
 * All of the {@code Safely} methods run integration code inside a try/catch. If an integration
 * throws an {@link Exception} or a {@link LinkageError} (typically because the plugin it hooks
 * into changed its API), the stack trace is logged and the integration is removed from this
 * registry, so it will not be called again.
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
     * Get if any integration matches a predicate, safely.
     *
     * @param predicate The predicate to test.
     * @return If any integration returned true. False if the registry is empty.
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
     * Apply a function to the first integration in this registry, safely.
     * <p>
     * The registry is unordered, so 'first' only means the first element produced by the
     * underlying iterator, not the first integration that was registered.
     *
     * @param function     The function to apply.
     * @param defaultValue The default value.
     * @param <R>          The type of value.
     * @return The result of the function, or the default value if the registry is empty
     *         or the integration threw.
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
     * <p>
     * If the action throws, the integration is removed from this registry.
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
        } catch (final Exception | LinkageError e) {
            Eco.get().getEcoPlugin().getLogger().warning("Integration for " + integration.getPluginName() + " threw an exception!");
            Eco.get().getEcoPlugin().getLogger().warning("The integration will be disabled.");
            e.printStackTrace();
            this.remove(integration);
            return defaultValue;
        }
    }

    /**
     * Get if all integrations match a predicate, safely.
     *
     * @param predicate The predicate to test.
     * @return If all integrations returned true. True if the registry is empty.
     */
    public boolean allSafely(@NotNull final Predicate<T> predicate) {
        return !this.anySafely(predicate.negate());
    }
}
