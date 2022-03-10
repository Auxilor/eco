package com.willfp.eco.core.placeholder;

import org.jetbrains.annotations.NotNull;

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
    void injectPlaceholders(@NotNull StaticPlaceholder... placeholders);

    /**
     * Get injected placeholders.
     *
     * @return Injected placeholders.
     */
    List<StaticPlaceholder> getInjectedPlaceholders();
}
