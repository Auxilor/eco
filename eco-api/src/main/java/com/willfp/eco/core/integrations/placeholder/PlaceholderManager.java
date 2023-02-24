package com.willfp.eco.core.integrations.placeholder;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.willfp.eco.core.Eco;
import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.placeholder.AdditionalPlayer;
import com.willfp.eco.core.placeholder.DynamicPlaceholder;
import com.willfp.eco.core.placeholder.InjectablePlaceholder;
import com.willfp.eco.core.placeholder.Placeholder;
import com.willfp.eco.core.placeholder.PlaceholderInjectable;
import com.willfp.eco.core.placeholder.PlayerDynamicPlaceholder;
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
import java.util.Optional;
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
    private static final Map<EcoPlugin, Map<Pattern, Placeholder>> REGISTERED_PLACEHOLDERS = new HashMap<>();

    /**
     * All registered placeholder integrations.
     */
    private static final Set<PlaceholderIntegration> REGISTERED_INTEGRATIONS = new HashSet<>();

    /**
     * Placeholder Lookup Cache.
     */
    private static final Cache<PlaceholderLookup, Optional<Placeholder>> PLACEHOLDER_LOOKUP_CACHE = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.SECONDS)
            .build();

    /**
     * Placeholder Cache.
     */
    private static final LoadingCache<EntryWithPlayer, String> PLACEHOLDER_CACHE = Caffeine.newBuilder()
            .expireAfterWrite(50, TimeUnit.MILLISECONDS)
            .build(key -> key.entry.getValue(key.player));

    /**
     * Dynamic Placeholder Cache.
     */
    private static final LoadingCache<DynamicEntryWithPlayer, String> DYNAMIC_PLACEHOLDER_CACHE = Caffeine.newBuilder()
            .expireAfterWrite(50, TimeUnit.MILLISECONDS)
            .build(key -> key.entry.getValue(key.args, key.player));

    /**
     * The default PlaceholderAPI pattern; brought in for compatibility.
     */
    private static final Pattern PATTERN = Pattern.compile("%([^% ]+)%");

    /**
     * Empty injectable object.
     */
    public static final PlaceholderInjectable EMPTY_INJECTABLE = new PlaceholderInjectable() {
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
        if (placeholder instanceof StaticPlaceholder || placeholder instanceof PlayerStaticPlaceholder) {
            throw new IllegalArgumentException("Static placeholders cannot be registered!");
        }

        EcoPlugin plugin = placeholder.getPlugin() == null ? Eco.get().getEcoPlugin() : placeholder.getPlugin();

        Map<Pattern, Placeholder> pluginPlaceholders = REGISTERED_PLACEHOLDERS
                .getOrDefault(plugin, new HashMap<>());

        pluginPlaceholders.put(placeholder.getPattern(), placeholder);

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
        // This is really janky, and it sucks, but it works so?
        // Compensating for regex being slow so that's why we get it.
        Placeholder placeholder = PLACEHOLDER_LOOKUP_CACHE.get(
                new PlaceholderLookup(identifier, plugin),
                (it) -> {
                    EcoPlugin owner = plugin == null ? Eco.get().getEcoPlugin() : plugin;

                    // I hate the streams API.
                    Optional<Placeholder> found = REGISTERED_PLACEHOLDERS
                            .getOrDefault(owner, new HashMap<>())
                            .entrySet()
                            .stream().filter(entry -> entry.getKey().matcher(identifier).matches())
                            .map(Map.Entry::getValue)
                            .findFirst();

                    if (found.isEmpty() && plugin != null) {
                        // Here we go again! Something about legacy support? I don't remember.
                        // I won't touch it though, I'm scared of the placeholder system.
                        found = REGISTERED_PLACEHOLDERS
                                .getOrDefault(Eco.get().getEcoPlugin(), new HashMap<>())
                                .entrySet()
                                .stream().filter(entry -> entry.getKey().matcher(identifier).matches())
                                .map(Map.Entry::getValue)
                                .findFirst();
                    }

                    return found;
                }
        ).orElse(null);

        if (placeholder == null) {
            return "";
        }

        /*
        This code here is *really* not very good. It's mega externalized logic hacked
        together and made worse by the addition of dynamic placeholders. But it works,
        and it means I don't have to rewrite the whole placeholder system. So it's
        good enough for me.
         */

        if (placeholder instanceof PlayerPlaceholder playerPlaceholder) {
            if (player == null) {
                return "";
            } else {
                return PLACEHOLDER_CACHE.get(new EntryWithPlayer(playerPlaceholder, player));
            }
        } else if (placeholder instanceof PlayerlessPlaceholder playerlessPlaceholder) {
            return playerlessPlaceholder.getValue();
        } else if (placeholder instanceof PlayerDynamicPlaceholder playerDynamicPlaceholder) {
            if (player == null) {
                return "";
            } else {
                return DYNAMIC_PLACEHOLDER_CACHE.get(new DynamicEntryWithPlayer(playerDynamicPlaceholder, identifier, player));
            }
        } else if (placeholder instanceof DynamicPlaceholder dynamicPlaceholder) {
            return dynamicPlaceholder.getValue(identifier);
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

        /*

        Why am I doing statics at the start, but player statics at the end?

        Additional players let you use something like victim as a player to parse in relation to,
        for example doing %victim_player_health%, which would parse the health of the victim.

        However, something like libreforge will also inject %victim_max_health%, which is unrelated
        to additional players, and instead holds a constant value. So, eco saw this, smartly thought
        "ah, it's an additional player, let's parse it", and then tried to parse %max_health% with
        relation to the victim, which resolved to zero. So, we have to parse statics and player statics
        that might include a prefix first, then additional players, then player statics with the support
        of additional players.

        This was a massive headache and took so many reports before I clocked what was going on.

        Oh well, at least it's fixed now.

         */


        for (InjectablePlaceholder injection : context.getPlaceholderInjections()) {
            if (injection instanceof StaticPlaceholder placeholder) {
                processed = processed.replace("%" + placeholder.getIdentifier() + "%", placeholder.getValue());
            } else if (injection instanceof PlayerStaticPlaceholder placeholder && player != null) {
                processed = processed.replace("%" + placeholder.getIdentifier() + "%", placeholder.getValue(player));
            }
        }

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

        // DON'T REMOVE THIS, IT'S NOT DUPLICATE CODE.
        for (InjectablePlaceholder injection : context.getPlaceholderInjections()) {
            // Do I know this is a bad way of doing this? Yes.
            if (injection instanceof PlayerStaticPlaceholder placeholder && player != null) {
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

    private record PlaceholderLookup(@NotNull String identifier,
                                     @Nullable EcoPlugin plugin) {

    }

    private record EntryWithPlayer(@NotNull PlayerPlaceholder entry,
                                   @NotNull Player player) {

    }

    private record DynamicEntryWithPlayer(@NotNull PlayerDynamicPlaceholder entry,
                                          @NotNull String args,
                                          @NotNull Player player) {

    }

    private PlaceholderManager() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
