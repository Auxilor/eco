package com.willfp.eco.core.integrations.placeholder;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.willfp.eco.core.Eco;
import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.placeholder.AdditionalPlayer;
import com.willfp.eco.core.placeholder.InjectablePlaceholder;
import com.willfp.eco.core.placeholder.Placeholder;
import com.willfp.eco.core.placeholder.PlaceholderInjectable;
import com.willfp.eco.core.placeholder.PlayerPlaceholder;
import com.willfp.eco.core.placeholder.PlayerStaticPlaceholder;
import com.willfp.eco.core.placeholder.PlayerlessPlaceholder;
import com.willfp.eco.core.placeholder.StaticPlaceholder;
import com.willfp.eco.util.StringUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class to handle placeholder integrations.
 */
@SuppressWarnings("removal")
public final class PlaceholderManager {
    /**
     * All registered placeholders.
     */
    private static final Map<EcoPlugin, Map<String, Placeholder>> REGISTERED_PLACEHOLDERS = new HashMap<>();

    /**
     * All registered placeholder integrations.
     */
    private static final Set<PlaceholderIntegration> REGISTERED_INTEGRATIONS = new HashSet<>();

    /**
     * Placeholder Cache.
     */
    private static final LoadingCache<EntryWithPlayer, String> PLACEHOLDER_CACHE = Caffeine.newBuilder()
            .expireAfterWrite(50, TimeUnit.MILLISECONDS)
            .build(key -> key.entry.getValue(key.player));

    /**
     * Empty injectable object.
     */
    private static final PlaceholderInjectable EMPTY_INJECTABLE = new PlaceholderInjectable() {
        @Override
        public void clearInjectedPlaceholders() {
            // Do nothing.
        }

        @Override
        public @NotNull
        List<InjectablePlaceholder> getPlaceholderInjections() {
            return Collections.emptyList();
        }
    };

    /**
     * The default PlaceholderAPI pattern; brought in for compatibility.
     */
    private static final Pattern PATTERN = Pattern.compile("[%]([^%]+)[%]");

    /**
     * Register a new placeholder integration.
     *
     * @param integration The {@link com.willfp.eco.core.integrations.placeholder.PlaceholderIntegration} to register.
     */
    public static void addIntegration(@NotNull final PlaceholderIntegration integration) {
        integration.registerIntegration();
        REGISTERED_INTEGRATIONS.add(integration);
    }

    /**
     * Register a placeholder.
     *
     * @param placeholder The placeholder to register.
     */
    public static void registerPlaceholder(@NotNull final Placeholder placeholder) {
        if (placeholder instanceof StaticPlaceholder) {
            throw new IllegalArgumentException("Static placeholders cannot be registered!");
        }

        EcoPlugin plugin = placeholder.getPlugin() == null ? Eco.getHandler().getEcoPlugin() : placeholder.getPlugin();
        Map<String, Placeholder> pluginPlaceholders = REGISTERED_PLACEHOLDERS
                .getOrDefault(plugin, new HashMap<>());
        pluginPlaceholders.put(placeholder.getIdentifier(), placeholder);
        REGISTERED_PLACEHOLDERS.put(plugin, pluginPlaceholders);
    }

    /**
     * Register a placeholder.
     *
     * @param placeholder The placeholder to register.
     * @deprecated Uses old placeholder system.
     */
    @Deprecated(since = "6.28.0", forRemoval = true)
    public static void registerPlaceholder(@NotNull final PlaceholderEntry placeholder) {
        registerPlaceholder(placeholder.toModernPlaceholder());
    }

    /**
     * Get the result of a placeholder with respect to a player.
     *
     * @param player     The player to get the result from.
     * @param identifier The placeholder identifier.
     * @return The value of the placeholder.
     * @deprecated Specify a plugin to get the result from.
     */
    @Deprecated
    public static String getResult(@Nullable final Player player,
                                   @NotNull final String identifier) {
        return getResult(player, identifier, null);
    }

    /**
     * Get the result of a placeholder with respect to a player.
     *
     * @param player     The player to get the result from.
     * @param identifier The placeholder identifier.
     * @param plugin     The plugin for the placeholder.
     * @return The value of the placeholder.
     */
    @NotNull
    public static String getResult(@Nullable final Player player,
                                   @NotNull final String identifier,
                                   @Nullable final EcoPlugin plugin) {
        EcoPlugin owner = plugin == null ? Eco.getHandler().getEcoPlugin() : plugin;
        Placeholder placeholder = REGISTERED_PLACEHOLDERS.getOrDefault(owner, new HashMap<>()).get(identifier.toLowerCase());

        if (placeholder == null && plugin != null) {
            Placeholder alternate = REGISTERED_PLACEHOLDERS.getOrDefault(Eco.getHandler().getEcoPlugin(), new HashMap<>())
                    .get(identifier.toLowerCase());
            if (alternate != null) {
                placeholder = alternate;
            }
        }

        if (placeholder == null) {
            return "";
        }

        if (placeholder instanceof PlayerPlaceholder playerPlaceholder) {
            if (player == null) {
                return "";
            } else {
                return PLACEHOLDER_CACHE.get(new EntryWithPlayer(playerPlaceholder, player));
            }
        } else if (placeholder instanceof PlayerlessPlaceholder playerlessPlaceholder) {
            return playerlessPlaceholder.getValue();
        } else {
            return "";
        }
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
        return translatePlaceholders(text, player, EMPTY_INJECTABLE);
    }

    /**
     * Translate all placeholders with respect to a player.
     *
     * @param text    The text that may contain placeholders to translate.
     * @param player  The player to translate the placeholders with respect to.
     * @param statics Extra static placeholders.
     * @return The text, translated.
     * @deprecated Use new static system.
     */
    @Deprecated(since = "6.35.0", forRemoval = true)
    public static String translatePlaceholders(@NotNull final String text,
                                               @Nullable final Player player,
                                               @NotNull final List<StaticPlaceholder> statics) {
        return translatePlaceholders(text, player, EMPTY_INJECTABLE);
    }

    /**
     * Translate all placeholders with respect to a player.
     *
     * @param text    The text that may contain placeholders to translate.
     * @param player  The player to translate the placeholders with respect to.
     * @param context The injectable context.
     * @return The text, translated.
     */
    public static String translatePlaceholders(@NotNull final String text,
                                               @Nullable final Player player,
                                               @NotNull final PlaceholderInjectable context) {
        return translatePlaceholders(text, player, context, new ArrayList<>());
    }

    /**
     * Translate all placeholders with respect to a player.
     *
     * @param text              The text that may contain placeholders to translate.
     * @param player            The player to translate the placeholders with respect to.
     * @param context           The injectable context.
     * @param additionalPlayers Additional players to translate placeholders for.
     * @return The text, translated.
     */
    public static String translatePlaceholders(@NotNull final String text,
                                               @Nullable final Player player,
                                               @NotNull final PlaceholderInjectable context,
                                               @NotNull final Collection<AdditionalPlayer> additionalPlayers) {
        String processed = text;

        // Prevent running 2 scans if there are no additional players.
        if (!additionalPlayers.isEmpty()) {
            List<String> found = findPlaceholdersIn(text);

            for (AdditionalPlayer additionalPlayer : additionalPlayers) {
                for (String placeholder : found) {
                    String prefix = "%" + additionalPlayer.getIdentifier() + "_";

                    if (placeholder.startsWith(prefix)) {
                        processed = processed.replace(
                                placeholder,
                                translatePlaceholders(
                                        "%" + StringUtils.removePrefix(prefix, placeholder),
                                        additionalPlayer.getPlayer()
                                )
                        );
                    }
                }
            }
        }

        for (PlaceholderIntegration integration : REGISTERED_INTEGRATIONS) {
            processed = integration.translate(processed, player);
        }
        for (InjectablePlaceholder injection : context.getPlaceholderInjections()) {
            // Do I know this is a bad way of doing this? Yes.
            if (injection instanceof StaticPlaceholder placeholder) {
                processed = processed.replace("%" + placeholder.getIdentifier() + "%", placeholder.getValue());
            } else if (injection instanceof PlayerStaticPlaceholder placeholder && player != null) {
                processed = processed.replace("%" + placeholder.getIdentifier() + "%", placeholder.getValue(player));
            }
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
        Set<String> found = new HashSet<>();

        // Mock PAPI for those without it installed
        if (REGISTERED_INTEGRATIONS.isEmpty()) {
            Matcher matcher = PATTERN.matcher(text);
            while (matcher.find()) {
                found.add(matcher.group());
            }
        }

        for (PlaceholderIntegration integration : REGISTERED_INTEGRATIONS) {
            found.addAll(integration.findPlaceholdersIn(text));
        }

        return new ArrayList<>(found);
    }

    private record EntryWithPlayer(@NotNull PlayerPlaceholder entry,
                                   @NotNull Player player) {

    }

    private PlaceholderManager() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
