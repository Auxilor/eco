package com.willfp.eco.core.integrations.placeholder;

import com.google.common.collect.ImmutableSet;
import com.willfp.eco.core.Eco;
import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.map.DefaultMap;
import com.willfp.eco.core.placeholder.AdditionalPlayer;
import com.willfp.eco.core.placeholder.InjectablePlaceholder;
import com.willfp.eco.core.placeholder.Placeholder;
import com.willfp.eco.core.placeholder.PlaceholderInjectable;
import com.willfp.eco.core.placeholder.RegistrablePlaceholder;
import com.willfp.eco.core.placeholder.context.PlaceholderContext;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class to handle arguments integrations.
 */
public final class PlaceholderManager {
    /**
     * All registered placeholders.
     */
    private static final DefaultMap<EcoPlugin, Set<Placeholder>> REGISTERED_PLACEHOLDERS = new DefaultMap<>(HashSet::new);

    /**
     * All registered arguments integrations.
     */
    private static final Set<PlaceholderIntegration> REGISTERED_INTEGRATIONS = new HashSet<>();

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
        // Storing as immutable set leads to slower times to register placeholders, but much
        // faster times to access registrations.
        Set<Placeholder> pluginPlaceholders = new HashSet<>(REGISTERED_PLACEHOLDERS.get(placeholder.getPlugin()));
        pluginPlaceholders.removeIf(p -> p.getPattern().equals(placeholder.getPattern()));
        pluginPlaceholders.add(placeholder);
        REGISTERED_PLACEHOLDERS.put(placeholder.getPlugin(), ImmutableSet.copyOf(pluginPlaceholders));
    }

    /**
     * Get the result of a placeholder with respect to a player.
     *
     * @param player     The player to get the result from.
     * @param identifier The placeholder args.
     * @param plugin     The plugin for the arguments.
     * @return The value of the arguments.
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
     * @param plugin  The plugin for the placeholder.
     * @param args    The arguments.
     * @param context The context.
     * @return The value of the arguments.
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
     * Get all registered placeholder integrations.
     *
     * @return The integrations.
     */
    public static Set<PlaceholderIntegration> getRegisteredIntegrations() {
        return Set.copyOf(REGISTERED_INTEGRATIONS);
    }

    /**
     * Get all registered placeholders for a plugin.
     *
     * @param plugin The plugin.
     * @return The placeholders.
     */
    public static Set<Placeholder> getRegisteredPlaceholders(@NotNull final EcoPlugin plugin) {
        return REGISTERED_PLACEHOLDERS.get(plugin);
    }

    private PlaceholderManager() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
