package com.willfp.eco.core.integrations.placeholder;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.placeholder.AdditionalPlayer;
import com.willfp.eco.core.placeholder.InjectablePlaceholder;
import com.willfp.eco.core.placeholder.Placeholder;
import com.willfp.eco.core.placeholder.PlaceholderInjectable;
import com.willfp.eco.core.placeholder.RegistrablePlaceholder;
import com.willfp.eco.core.placeholder.parsing.PlaceholderContext;
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
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class to handle arguments integrations.
 */
public final class PlaceholderManager {
    /**
     * All registered placeholders.
     */
    private static final Map<EcoPlugin, Map<Pattern, Placeholder>> REGISTERED_PLACEHOLDERS = new HashMap<>();

    /**
     * All registered arguments integrations.
     */
    private static final Set<PlaceholderIntegration> REGISTERED_INTEGRATIONS = new HashSet<>();

    /**
     * Placeholder Lookup Cache.
     */
    private static final Cache<PlaceholderLookup, Optional<Placeholder>> PLACEHOLDER_LOOKUP_CACHE = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.SECONDS)
            .build();

    /**
     * The default PlaceholderAPI pattern; brought in for compatibility.
     */
    private static final Pattern PATTERN = Pattern.compile("%([^% ]+)%");

    /**
     * Empty injectableContext object.
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
     *
     * @param integration The {@link com.willfp.eco.core.integrations.placeholder.PlaceholderIntegration} to register.
     */
    public static void addIntegration(@NotNull final PlaceholderIntegration integration) {
        integration.registerIntegration();
        REGISTERED_INTEGRATIONS.add(integration);
    }

    /**
     * Register a arguments.
     *
     * @param placeholder The arguments to register.
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
     * Register a arguments.
     *
     * @param placeholder The arguments to register.
     */
    public static void registerPlaceholder(@NotNull final RegistrablePlaceholder placeholder) {
        Map<Pattern, Placeholder> pluginPlaceholders = REGISTERED_PLACEHOLDERS
                .getOrDefault(placeholder.getPlugin(), new HashMap<>());

        pluginPlaceholders.put(placeholder.getPattern(), placeholder);

        REGISTERED_PLACEHOLDERS.put(placeholder.getPlugin(), pluginPlaceholders);
    }

    /**
     * Get the result of a placeholder with respect to a player.
     *
     * @param player     The player to get the result from.
     * @param identifier The placeholder identifier.
     * @param plugin     The plugin for the arguments.
     * @return The value of the arguments.
     */
    @NotNull
    public static String getResult(@Nullable final Player player,
                                   @NotNull final String identifier,
                                   @NotNull final EcoPlugin plugin) {
        return Objects.requireNonNullElse(
                getResult(
                        plugin,
                        identifier,
                        new PlaceholderContext(
                                player,
                                null,
                                EMPTY_INJECTABLE,
                                Collections.emptyList()
                        )
                ),
                ""
        );
    }

    /**
     * Get the result of a placeholder given a plugin and arguments.
     *
     * @param plugin The plugin for the placeholder.
     * @param args   The arguments.
     * @return The value of the arguments.
     */
    @Nullable
    public static String getResult(@NotNull final EcoPlugin plugin,
                                   @NotNull final String args,
                                   @NotNull final PlaceholderContext context) {
        // This is really janky, and it sucks, but it works so?
        // Compensating for regex being slow so that's why we get it.
        Placeholder placeholder = PLACEHOLDER_LOOKUP_CACHE.get(
                new PlaceholderLookup(args, plugin),
                (it) -> {
                    // I hate the streams API.
                    return REGISTERED_PLACEHOLDERS
                            .getOrDefault(plugin, new HashMap<>())
                            .entrySet()
                            .stream().filter(entry -> entry.getKey().matcher(args).matches())
                            .map(Map.Entry::getValue)
                            .findFirst();
                }
        ).orElse(null);

        if (placeholder == null) {
            return null;
        }

        return placeholder.getValue(args, context);
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
    public static String translatePlaceholders(@NotNull final String text,
                                               @Nullable final Player player) {
        return translatePlaceholders(text, player, EMPTY_INJECTABLE);
    }

    /**
     * Translate all placeholders with respect to a player.
     *
     * @param text    The text that may contain placeholders to translate.
     * @param player  The player to translate the placeholders with respect to.
     * @param context The injectableContext parseContext.
     * @return The text, translated.
     * @deprecated Use {@link #translatePlaceholders(String, PlaceholderContext)} instead.
     */
    @Deprecated(since = "6.56.0", forRemoval = true)
    @NotNull
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
     * @param context           The injectableContext parseContext.
     * @param additionalPlayers Additional players to translate placeholders for.
     * @return The text, translated.
     * @deprecated Use {@link #translatePlaceholders(String, PlaceholderContext)} instead.
     */
    @Deprecated(since = "6.56.0", forRemoval = true)
    @NotNull
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
     * Translate all placeholders in a translation context.
     *
     * @param text    The text that may contain placeholders to translate.
     * @param context The translation context.
     * @return The text, translated.
     */
    @NotNull
    public static String translatePlaceholders(@NotNull final String text,
                                               @NotNull final PlaceholderContext context) {
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


        for (InjectablePlaceholder injection : context.injectableContext().getPlaceholderInjections()) {
            processed = injection.tryTranslateQuickly(processed, context);
        }

        // Prevent running 2 scans if there are no additional players.
        if (!context.additionalPlayers().isEmpty()) {
            List<String> found = findPlaceholdersIn(text);

            for (AdditionalPlayer additionalPlayer : context.additionalPlayers()) {
                for (String placeholder : found) {
                    String prefix = "%" + additionalPlayer.getIdentifier() + "_";

                    if (placeholder.startsWith(prefix)) {
                        processed = processed.replace(
                                placeholder,
                                translatePlaceholders(
                                        "%" + StringUtils.removePrefix(prefix, placeholder),
                                        context.copyWithPlayer(additionalPlayer.getPlayer())
                                )
                        );
                    }
                }
            }
        }

        // Only run jank code if there are no integrations.
        if (REGISTERED_INTEGRATIONS.isEmpty()) {
            processed = setWithoutIntegration(processed, context.player());
        }

        for (PlaceholderIntegration integration : REGISTERED_INTEGRATIONS) {
            processed = integration.translate(processed, context.player());
        }

        // DON'T REMOVE THIS, IT'S NOT DUPLICATE CODE.
        for (InjectablePlaceholder injection : context.injectableContext().getPlaceholderInjections()) {
            processed = injection.tryTranslateQuickly(processed, context);
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
     * Set placeholders without any integrations.
     * <p>
     * This is fallback if for some reason you don't have PAPI installed.
     * It's a cut-down version of the actual PAPI code, and I don't
     * really know how it works.
     * <p>
     * Original source
     * <a href="https://github.com/PlaceholderAPI/PlaceholderAPI/blob/master/src/main/java/me/clip/placeholderapi/replacer/CharsReplacer.java">here</a>.
     *
     * @param text   The text.
     * @param player The player.
     * @return The text.
     */
    private static String setWithoutIntegration(@NotNull final String text,
                                                @Nullable final Player player) {
        char[] chars = text.toCharArray();
        StringBuilder builder = new StringBuilder(text.length());
        StringBuilder identifier = new StringBuilder();
        StringBuilder parameters = new StringBuilder();

        for (int i = 0; i < chars.length; i++) {
            char currentChar = chars[i];
            if (currentChar == '%' && i + 1 < chars.length) {
                boolean identified = false;
                boolean badPlaceholder = true;
                boolean hadSpace = false;

                while (true) {
                    i++;
                    if (i >= chars.length) {
                        break;
                    }

                    char p = chars[i];
                    if (p == ' ' && !identified) {
                        hadSpace = true;
                        break;
                    }

                    if (p == '%') {
                        badPlaceholder = false;
                        break;
                    }

                    if (p == '_' && !identified) {
                        identified = true;
                    } else if (identified) {
                        parameters.append(p);
                    } else {
                        identifier.append(p);
                    }
                }

                String pluginName = identifier.toString().toLowerCase();
                EcoPlugin plugin = EcoPlugin.getPlugin(pluginName);
                String placeholderIdentifier = parameters.toString();
                identifier.setLength(0);
                parameters.setLength(0);
                if (badPlaceholder) {
                    builder.append('%').append(pluginName);
                    if (identified) {
                        builder.append('_').append(placeholderIdentifier);
                    }

                    if (hadSpace) {
                        builder.append(' ');
                    }
                } else {
                    if (plugin == null) {
                        builder.append('%').append(pluginName);

                        if (identified) {
                            builder.append('_');
                        }

                        builder.append(placeholderIdentifier).append('%');
                    } else {
                        builder.append(getResult(player, placeholderIdentifier, plugin));
                    }
                }
            } else {
                builder.append(currentChar);
            }
        }

        return builder.toString();
    }

    private record PlaceholderLookup(@NotNull String identifier,
                                     @Nullable EcoPlugin plugin) {

    }

    private PlaceholderManager() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
