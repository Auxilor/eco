package com.willfp.eco.core.integrations.placeholder;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class to handle placeholder integrations.
 */
public final class PlaceholderManager {
    /**
     * All registered placeholders.
     */
    private static final Map<String, PlaceholderEntry> REGISTERED_PLACEHOLDERS = new HashMap<>();

    /**
     * All registered placeholder integrations.
     */
    private static final Set<PlaceholderIntegration> REGISTERED_INTEGRATIONS = new HashSet<>();

    /**
     * Register a new placeholder integration.
     *
     * @param integration The {@link PlaceholderIntegration} to register.
     */
    public static void addIntegration(@NotNull final PlaceholderIntegration integration) {
        integration.registerIntegration();
        REGISTERED_INTEGRATIONS.add(integration);
    }

    /**
     * Register a placeholder.
     *
     * @param expansion The {@link PlaceholderEntry} to register.
     */
    public static void registerPlaceholder(@NotNull final PlaceholderEntry expansion) {
        REGISTERED_PLACEHOLDERS.remove(expansion.getIdentifier());
        REGISTERED_PLACEHOLDERS.put(expansion.getIdentifier(), expansion);
    }

    /**
     * Get the result of a placeholder with respect to a player.
     *
     * @param player     The player to get the result from.
     * @param identifier The placeholder identifier.
     * @return The value of the placeholder.
     */
    public static String getResult(@Nullable final Player player,
                                   @NotNull final String identifier) {
        PlaceholderEntry entry = REGISTERED_PLACEHOLDERS.get(identifier.toLowerCase());
        if (entry == null) {
            return "";
        }

        if (player == null && entry.requiresPlayer()) {
            return "";
        }

        return entry.getResult(player);
    }

    /**
     * Translate all placeholders with respect to a player.
     *
     * @param text   The text that may contain placeholders to translate.
     * @param player The player to translate the placeholders with respect to.
     * @return The text, translated.
     */
    public static String translatePlaceholders(@NotNull final String text,
                                               @Nullable final Player player) {
        String processed = text;
        for (PlaceholderIntegration integration : REGISTERED_INTEGRATIONS) {
            processed = integration.translate(processed, player);
        }
        return processed;
    }

    /**
     * Find all placeholders in a given text.
     *
     * @param text The text.
     * @return The placeholders.
     */
    public static List<String> findPlaceholdersIn(@NotNull final String text) {
        List<String> found = new ArrayList<>();
        for (PlaceholderIntegration integration : REGISTERED_INTEGRATIONS) {
            found.addAll(integration.findPlaceholdersIn(text));
        }

        return found;
    }

    private PlaceholderManager() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
