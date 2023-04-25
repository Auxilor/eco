package com.willfp.eco.core.placeholder;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Represents a class that can have placeholders injected into it.
 */
public interface PlaceholderInjectable {
    /**
     * Inject arguments.
     *
     * @param placeholders The placeholders.
     */
    default void injectPlaceholders(@NotNull StaticPlaceholder... placeholders) {
        this.addInjectablePlaceholder(List.of(placeholders));
    }

    /**
     * Inject arguments.
     *
     * @param placeholders The placeholders.
     */
    default void injectPlaceholders(@NotNull InjectablePlaceholder... placeholders) {
        this.addInjectablePlaceholder(List.of(placeholders));
    }

    /**
     * Inject placeholders.
     * <p>
     * When implementing a PlaceholderInjectable object, override this method.
     *
     * @param placeholders The placeholders.
     */
    void addInjectablePlaceholder(@NotNull Iterable<InjectablePlaceholder> placeholders);

    /**
     * Clear injected placeholders.
     */
    void clearInjectedPlaceholders();

    /**
     * Get injected placeholders.
     * <p>
     * Override this method in implementations.
     *
     * @return Injected placeholders.
     */
    @NotNull
    List<InjectablePlaceholder> getPlaceholderInjections();
}
