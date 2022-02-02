package com.willfp.eco.core.integrations.placeholder;

import com.willfp.eco.core.integrations.Integration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper class for placeholder integrations.
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
     *
     * @param text The text.
     * @return The placeholders.
     */
    default List<String> findPlaceholdersIn(@NotNull String text) {
        return new ArrayList<>();
    }
}
