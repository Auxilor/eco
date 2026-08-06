package com.willfp.eco.core.integrations.placeholder;

import com.willfp.eco.core.integrations.Integration;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Wrapper interface for placeholder integrations.
 * <p>
 * Implemented for placeholder providers such as PlaceholderAPI, so that eco can expose its own
 * placeholders to them and translate theirs in eco's config values.
 *
 * @see PlaceholderManager
 */
public interface PlaceholderIntegration extends Integration {
    /**
     * Register the integration with the specified plugin.
     * Not to be confused with internal registration in {@link PlaceholderManager#addIntegration(PlaceholderIntegration)}.
     */
    void registerIntegration();

    /**
     * Translate all the placeholders in a string with respect to a player.
     *
     * @param text   The text to translate.
     * @param player The player to translate with respect to.
     * @return The string, translated.
     */
    String translate(@NotNull String text,
                     @Nullable Player player);

    /**
     * Find all placeholders in a given text.
     * <p>
     * Returns an empty list unless overridden.
     *
     * @param text The text.
     * @return The placeholders, including their surrounding delimiters.
     */
    default List<String> findPlaceholdersIn(@NotNull String text) {
        return new ArrayList<>();
    }
}
