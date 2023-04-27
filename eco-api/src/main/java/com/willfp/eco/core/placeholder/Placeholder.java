package com.willfp.eco.core.placeholder;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.placeholder.context.PlaceholderContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

/**
 * A placeholder represents a string that can hold a value.
 */
public interface Placeholder {
    /**
     * Get the plugin that holds the arguments.
     *
     * @return The plugin.
     */
    @Nullable
    EcoPlugin getPlugin();

    /**
     * Get the value of the arguments.
     *
     * @param args    The args.
     * @param context The context.
     * @return The value.
     */
    @Nullable
    String getValue(@NotNull String args,
                    @NotNull PlaceholderContext context);

    /**
     * Get the pattern for the arguments.
     *
     * @return The pattern.
     */
    @NotNull
    Pattern getPattern();

    /**
     * Try to translate all instances of this placeholder in text quickly.
     *
     * @param text    The text to translate.
     * @param context The context.
     * @return The translated text.
     */
    default String tryTranslateQuickly(@NotNull final String text,
                                       @NotNull final PlaceholderContext context) {
        return text;
    }

    /**
     * Get the identifier for the arguments.
     *
     * @return The identifier.
     * @deprecated Some arguments may not have an identifier. Use {@link #getPattern()} instead.
     */
    @Deprecated(since = "6.56.0", forRemoval = true)
    @NotNull
    default String getIdentifier() {
        return this.getPattern().pattern();
    }
}
