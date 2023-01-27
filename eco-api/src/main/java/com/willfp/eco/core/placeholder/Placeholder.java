package com.willfp.eco.core.placeholder;

import com.willfp.eco.core.EcoPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

/**
 * A placeholder represents a string that can hold a value.
 */
public sealed interface Placeholder permits PlayerPlaceholder, PlayerlessPlaceholder,
        DynamicPlaceholder, PlayerDynamicPlaceholder, InjectablePlaceholder {
    /**
     * Get the plugin that holds the placeholder.
     *
     * @return The plugin.
     */
    @Nullable
    EcoPlugin getPlugin();

    /**
     * Get the identifier for the placeholder.
     *
     * @return The identifier.
     */
    @NotNull
    String getIdentifier();

    /**
     * Get the pattern for the placeholder.
     *
     * @return The pattern.
     */
    @NotNull
    default Pattern getPattern() {
        return Pattern.compile(this.getIdentifier());
    }
}
