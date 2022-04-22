package com.willfp.eco.core.placeholder;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a class that can have placeholders injected into it.
 */
public interface PlaceholderInjectable {
    /**
     * Inject placeholder.
     *
     * @param placeholders The placeholders.
     */
    default void injectPlaceholders(@NotNull StaticPlaceholder... placeholders) {
        this.injectPlaceholders(List.of(placeholders));
    }

    /**
     * Inject placeholder.
     *
     * @param placeholders The placeholders.
     */
    default void injectPlaceholders(@NotNull InjectablePlaceholder... placeholders) {
        this.addInjectablePlaceholder(List.of(placeholders));
    }

    /**
     * Inject placeholder.
     *
     * @param placeholders The placeholders.
     */
    @Deprecated(since = "6.35.0", forRemoval = true)
    default void injectPlaceholders(@NotNull Iterable<StaticPlaceholder> placeholders) {
        List<InjectablePlaceholder> toInject = new ArrayList<>();
        for (StaticPlaceholder placeholder : placeholders) {
            toInject.add(placeholder);
        }
        this.addInjectablePlaceholder(toInject);
    }

    /**
     * Inject placeholders.
     * <p>
     * When implementing a PlaceholderInjectable object, override this method.
     *
     * @param placeholders The placeholders.
     */
    default void addInjectablePlaceholder(@NotNull Iterable<InjectablePlaceholder> placeholders) {
        List<StaticPlaceholder> toInject = new ArrayList<>();
        for (InjectablePlaceholder placeholder : placeholders) {
            if (placeholder instanceof StaticPlaceholder staticPlaceholder) {
                toInject.add(staticPlaceholder);
            }
        }
        this.injectPlaceholders(toInject);
    }

    /**
     * Clear injected placeholders.
     */
    void clearInjectedPlaceholders();

    /**
     * Get injected placeholders.
     *
     * @return Injected placeholders.
     * @deprecated Use getPlaceholderInjections.
     */
    @Deprecated(since = "6.35.0", forRemoval = true)
    @NotNull
    default List<StaticPlaceholder> getInjectedPlaceholders() {
        List<StaticPlaceholder> found = new ArrayList<>();

        for (InjectablePlaceholder placeholder : getPlaceholderInjections()) {
            if (placeholder instanceof StaticPlaceholder staticPlaceholder) {
                found.add(staticPlaceholder);
            }
        }

        return found;
    }

    /**
     * Get injected placeholders.
     * <p>
     * Override this method in implementations.
     *
     * @return Injected placeholders.
     */
    @NotNull
    default List<InjectablePlaceholder> getPlaceholderInjections() {
        return new ArrayList<>(getInjectedPlaceholders());
    }
}
