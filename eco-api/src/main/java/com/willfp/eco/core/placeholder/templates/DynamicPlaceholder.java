package com.willfp.eco.core.placeholder.templates;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.placeholder.RegistrablePlaceholder;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * A template class for dynamic placeholders.
 */
public abstract class DynamicPlaceholder implements RegistrablePlaceholder {
    /**
     * The plugin.
     */
    private final EcoPlugin plugin;

    /**
     * The placeholder pattern.
     */
    private final Pattern pattern;

    /**
     * Create a new dynamic placeholder.
     *
     * @param plugin  The plugin.
     * @param pattern The pattern.
     */
    protected DynamicPlaceholder(@NotNull final EcoPlugin plugin,
                                 @NotNull final Pattern pattern) {
        this.plugin = plugin;
        this.pattern = pattern;
    }

    @Override
    public @NotNull Pattern getPattern() {
        return pattern;
    }

    @NotNull
    @Override
    public EcoPlugin getPlugin() {
        return plugin;
    }

    @Override
    public String toString() {
        return "DynamicPlaceholder{" +
                "plugin='" + plugin + '\'' +
                "pattern='" + pattern + '\'' +
                '}';
    }

    @Override
    public boolean equals(@NotNull final Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof DynamicPlaceholder that)) {
            return false;
        }

        return Objects.equals(pattern, that.getPattern())
                && Objects.equals(plugin, that.getPlugin());
    }

    @Override
    public int hashCode() {
        return Objects.hash(pattern, plugin);
    }
}
