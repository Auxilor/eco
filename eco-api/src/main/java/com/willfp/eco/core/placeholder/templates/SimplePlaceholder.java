package com.willfp.eco.core.placeholder.templates;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.placeholder.RegistrablePlaceholder;
import com.willfp.eco.core.placeholder.context.PlaceholderContext;
import com.willfp.eco.util.PatternUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * A template class for simple placeholders.
 */
public abstract class SimplePlaceholder implements RegistrablePlaceholder {
    /**
     * The plugin.
     */
    private final EcoPlugin plugin;

    /**
     * The name of the placeholder.
     */
    private final String identifier;

    /**
     * The placeholder pattern.
     */
    private final Pattern pattern;

    /**
     * Create a new simple placeholder.
     *
     * @param plugin     The plugin.
     * @param identifier The identifier.
     */
    protected SimplePlaceholder(@NotNull final EcoPlugin plugin,
                                @NotNull final String identifier) {
        this.plugin = plugin;
        this.identifier = identifier;
        this.pattern = PatternUtils.compileLiteral(identifier);
    }

    @Override
    public String tryTranslateQuickly(@NotNull final String text,
                                      @NotNull final PlaceholderContext context) {
        return text.replace(
                "%" + this.identifier + "%",
                Objects.requireNonNullElse(this.getValue(this.identifier, context), "")
        );
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
        return "SimplePlaceholder{" +
                "plugin='" + plugin + '\'' +
                "identifier='" + identifier + '\'' +
                '}';
    }

    @Override
    public boolean equals(@NotNull final Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof SimplePlaceholder that)) {
            return false;
        }

        return Objects.equals(pattern, that.getPattern())
                && Objects.equals(plugin, that.getPlugin());
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, plugin);
    }
}
