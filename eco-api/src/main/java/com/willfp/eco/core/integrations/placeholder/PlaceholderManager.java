package com.willfp.eco.core.integrations.placeholder;

import com.willfp.eco.core.Eco;
import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.placeholder.*;
import com.willfp.eco.core.placeholder.context.PlaceholderContext;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Class to handle placeholders and placeholder integrations.
 * <p>
 * Holds every {@link Placeholder} registered by an {@link EcoPlugin}, along with the
 * {@link PlaceholderIntegration}s used to bridge eco's placeholders to external providers
 * such as PlaceholderAPI.
 */
public final class PlaceholderManager {
    /**
     * All registered placeholders.
     */
    private static final Map<EcoPlugin, Map<String, Placeholder>> REGISTERED_PLACEHOLDERS = new ConcurrentHashMap<>();

    /**
     * Cached immutable snapshots of registered placeholders per plugin.
     */
    private static final ConcurrentHashMap<EcoPlugin, Collection<Placeholder>> PLACEHOLDER_SNAPSHOT_CACHE = new ConcurrentHashMap<>();

    /**
     * All registered placeholder integrations.
     */
    private static final Set<PlaceholderIntegration> REGISTERED_INTEGRATIONS = ConcurrentHashMap.newKeySet();

    /**
     * Cached immutable snapshot of registered integrations.
     */
    private static volatile Set<PlaceholderIntegration> cachedIntegrations = null;

    /**
     * The default PlaceholderAPI pattern; brought in for compatibility.
     */
    private static final Pattern PATTERN = Pattern.compile("%([^% ]+)%");

    /**
     * An empty {@link PlaceholderInjectable} that discards all injections and never
     * returns any of its own.
     */
    public static final PlaceholderInjectable EMPTY_INJECTABLE = new PlaceholderInjectable() {
        @Override
        public void addInjectablePlaceholder(@NotNull Iterable<InjectablePlaceholder> placeholders) {
            // Do nothing.
        }

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
     * <p>
     * Calls {@link PlaceholderIntegration#registerIntegration()} before storing it.
     *
     * @param integration The {@link PlaceholderIntegration} to register.
     */
    public static void addIntegration(@NotNull final PlaceholderIntegration integration) {
        integration.registerIntegration();
        REGISTERED_INTEGRATIONS.add(integration);
        cachedIntegrations = null;
    }

    /**
     * Register a placeholder.
     *
     * @param placeholder The placeholder to register. Must be a {@link RegistrablePlaceholder}.
     * @throws IllegalArgumentException If the placeholder is not a {@link RegistrablePlaceholder}.
     * @deprecated Use {@link #registerPlaceholder(RegistrablePlaceholder)} instead.
     */
    @Deprecated(since = "6.56.0", forRemoval = true)
    public static void registerPlaceholder(@NotNull final Placeholder placeholder) {
        if (!(placeholder instanceof RegistrablePlaceholder)) {
            throw new IllegalArgumentException("Placeholder must be RegistrablePlaceholder!");
        }

        registerPlaceholder((RegistrablePlaceholder) placeholder);
    }

    /**
     * Register a placeholder.
     * <p>
     * The placeholder is stored against {@link RegistrablePlaceholder#getPlugin()}, keyed by its
     * pattern, so registering a second placeholder with the same pattern replaces the first.
     *
     * @param placeholder The placeholder to register.
     */
    public static void registerPlaceholder(@NotNull final RegistrablePlaceholder placeholder) {
        Map<String, Placeholder> pluginPlaceholders = REGISTERED_PLACEHOLDERS.computeIfAbsent(
                placeholder.getPlugin(),
                k -> Collections.synchronizedMap(new LinkedHashMap<>())
        );
        pluginPlaceholders.put(placeholder.getPattern().pattern(), placeholder);
        PLACEHOLDER_SNAPSHOT_CACHE.remove(placeholder.getPlugin());
    }

    /**
     * Get the result of a placeholder with respect to a player.
     *
     * @param player     The player to get the result from.
     * @param identifier The placeholder args.
     * @param plugin     The plugin that owns the placeholder.
     * @return The value of the placeholder, or an empty string if it could not be resolved.
     */
    @NotNull
    public static String getResult(@Nullable final Player player,
                                   @NotNull final String identifier,
                                   @Nullable final EcoPlugin plugin) {
        return Objects.requireNonNullElse(
                getResult(
                        plugin,
                        identifier,
                        new PlaceholderContext(player)
                ),
                ""
        );
    }

    /**
     * Get the result of a placeholder given a plugin and arguments.
     *
     * @param plugin  The plugin for the placeholder, or null if the placeholder is not owned
     *                by a specific plugin.
     * @param args    The arguments.
     * @param context The context.
     * @return The value of the placeholder, or null if it could not be resolved.
     */
    @Nullable
    public static String getResult(@Nullable final EcoPlugin plugin,
                                   @NotNull final String args,
                                   @NotNull final PlaceholderContext context) {
        return Eco.get().getPlaceholderValue(plugin, args, context);
    }

    /**
     * Translate all placeholders with respect to a player.
     *
     * @param text   The text that may contain placeholders to translate.
     * @param player The player to translate the placeholders with respect to.
     * @return The text, translated.
     * @deprecated Use {@link #translatePlaceholders(String, PlaceholderContext)} instead.
     */
    @Deprecated(since = "6.56.0", forRemoval = true)
    @NotNull
    @SuppressWarnings("DeprecatedIsStillUsed")
    public static String translatePlaceholders(@NotNull final String text,
                                               @Nullable final Player player) {
        return translatePlaceholders(text, player, EMPTY_INJECTABLE);
    }

    /**
     * Translate all placeholders with respect to a player.
     *
     * @param text    The text that may contain placeholders to translate.
     * @param player  The player to translate the placeholders with respect to.
     * @param context The injectable context to take additional placeholders from.
     * @return The text, translated.
     * @deprecated Use {@link #translatePlaceholders(String, PlaceholderContext)} instead.
     */
    @Deprecated(since = "6.56.0", forRemoval = true)
    @NotNull
    @SuppressWarnings("DeprecatedIsStillUsed")
    public static String translatePlaceholders(@NotNull final String text,
                                               @Nullable final Player player,
                                               @NotNull final PlaceholderInjectable context) {
        return translatePlaceholders(
                text,
                new PlaceholderContext(
                        player,
                        null,
                        context,
                        new ArrayList<>()
                )
        );
    }

    /**
     * Translate all placeholders with respect to a player.
     *
     * @param text              The text that may contain placeholders to translate.
     * @param player            The player to translate the placeholders with respect to.
     * @param context           The injectable context to take additional placeholders from.
     * @param additionalPlayers Additional players to translate placeholders for.
     * @return The text, translated.
     * @deprecated Use {@link #translatePlaceholders(String, PlaceholderContext)} instead.
     */
    @Deprecated(since = "6.56.0", forRemoval = true)
    @NotNull
    @SuppressWarnings("DeprecatedIsStillUsed")
    public static String translatePlaceholders(@NotNull final String text,
                                               @Nullable final Player player,
                                               @NotNull final PlaceholderInjectable context,
                                               @NotNull final Collection<AdditionalPlayer> additionalPlayers) {
        return translatePlaceholders(
                text,
                new PlaceholderContext(
                        player,
                        null,
                        context,
                        additionalPlayers
                )
        );
    }

    /**
     * Translate all placeholders without a placeholder context.
     *
     * @param text The text that may contain placeholders to translate.
     * @return The text, translated.
     */
    @NotNull
    public static String translatePlaceholders(@NotNull final String text) {
        return Eco.get().translatePlaceholders(text, PlaceholderContext.EMPTY);
    }

    /**
     * Translate all placeholders in a translation context.
     *
     * @param text    The text that may contain placeholders to translate.
     * @param context The translation context.
     * @return The text, translated.
     */
    @NotNull
    public static String translatePlaceholders(@NotNull final String text,
                                               @NotNull final PlaceholderContext context) {
        return Eco.get().translatePlaceholders(text, context);
    }

    /**
     * Find all placeholders in a given text.
     * <p>
     * Matches the standard PlaceholderAPI {@code %placeholder%} pattern, and additionally asks
     * every registered {@link PlaceholderIntegration} for the placeholders it recognises.
     *
     * @param text The text.
     * @return The distinct placeholders found, including their surrounding delimiters,
     *         in no particular order.
     */
    public static List<String> findPlaceholdersIn(@NotNull final String text) {
        Set<String> found = new HashSet<>();

        Matcher matcher = PATTERN.matcher(text);
        while (matcher.find()) {
            found.add(matcher.group());
        }

        for (PlaceholderIntegration integration : REGISTERED_INTEGRATIONS) {
            found.addAll(integration.findPlaceholdersIn(text));
        }

        return new ArrayList<>(found);
    }

    /**
     * Get all registered placeholder integrations.
     *
     * @return An immutable set of the integrations.
     */
    public static Set<PlaceholderIntegration> getRegisteredIntegrations() {
        Set<PlaceholderIntegration> cached = cachedIntegrations;
        if (cached == null) {
            cached = Set.copyOf(REGISTERED_INTEGRATIONS);
            cachedIntegrations = cached;
        }
        return cached;
    }

    /**
     * Get all registered placeholders for a plugin.
     *
     * @param plugin The plugin.
     * @return An immutable collection of the plugin's placeholders, empty if it has none.
     */
    public static Collection<Placeholder> getRegisteredPlaceholders(@NotNull final EcoPlugin plugin) {
        return PLACEHOLDER_SNAPSHOT_CACHE.computeIfAbsent(plugin, p -> {
            Map<String, Placeholder> pluginPlaceholders = REGISTERED_PLACEHOLDERS.get(p);
            if (pluginPlaceholders == null) {
                return Collections.emptyList();
            }
            synchronized (pluginPlaceholders) {
                return List.copyOf(pluginPlaceholders.values());
            }
        });
    }

    /**
     * Look up a registered placeholder by direct map key, then fall back to regex scan.
     * <p>
     * The fallback scan only considers placeholders whose pattern was not compiled with
     * {@link Pattern#LITERAL}, since a literal pattern can only ever be matched by the
     * direct key lookup.
     *
     * @param plugin The plugin.
     * @param args   The placeholder args to match.
     * @return The matching placeholder, or null if the plugin has no placeholder matching the args.
     */
    @Nullable
    public static Placeholder getRegisteredPlaceholder(@NotNull final EcoPlugin plugin,
                                                       @NotNull final String args) {
        Map<String, Placeholder> pluginPlaceholders = REGISTERED_PLACEHOLDERS.get(plugin);
        if (pluginPlaceholders == null) {
            return null;
        }

        Placeholder direct = pluginPlaceholders.get(args);
        if (direct != null) {
            return direct;
        }

        synchronized (pluginPlaceholders) {
            for (Placeholder placeholder : pluginPlaceholders.values()) {
                java.util.regex.Pattern pattern = placeholder.getPattern();
                if ((pattern.flags() & java.util.regex.Pattern.LITERAL) == 0) {
                    if (pattern.matcher(args).matches()) {
                        return placeholder;
                    }
                }
            }
        }

        return null;
    }

    private PlaceholderManager() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
