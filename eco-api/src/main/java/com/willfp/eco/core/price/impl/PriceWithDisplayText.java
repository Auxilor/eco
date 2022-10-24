package com.willfp.eco.core.price.impl;

import com.willfp.eco.core.price.Price;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Free (default) price.
 */
public final class PriceWithDisplayText implements Price {
    /**
     * The delegate price.
     */
    private final Price delegate;

    /**
     * The display text.
     */
    private final String displayText;

    /**
     * Create a new displayed price.
     *
     * @param delegate    The delegate price.
     * @param displayText The display text to show.
     */
    public PriceWithDisplayText(@NotNull final Price delegate,
                                @NotNull final String displayText) {
        this.delegate = delegate;
        this.displayText = displayText;
    }

    @Override
    public boolean canAfford(@NotNull Player player) {
        return delegate.canAfford(player);
    }

    @Override
    public void pay(@NotNull Player player) {
        delegate.pay(player);
    }

    @Override
    public String getDisplayText() {
        return displayText;
    }
}
