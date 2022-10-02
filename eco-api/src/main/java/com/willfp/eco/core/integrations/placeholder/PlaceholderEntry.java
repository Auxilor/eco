package com.willfp.eco.core.integrations.placeholder;

import com.willfp.eco.core.Eco;
import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.placeholder.Placeholder;
import com.willfp.eco.core.placeholder.PlayerPlaceholder;
import com.willfp.eco.core.placeholder.PlayerlessPlaceholder;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Function;

/**
 * A placeholder entry is a placeholder in and of itself.
 * <p>
 * It should be fairly straightforward.
 *
 * @deprecated Confusing functionality with inconsistent nullability and poor naming.
 */
@Deprecated(since = "6.28.0", forRemoval = true)
public class PlaceholderEntry {
    /**
     * The name of the placeholder, used in lookups.
     */
    private final String identifier;

    /**
     * The lambda to retrieve the output of the placeholder given a player.
     */
    private final Function<Player, String> function;

    /**
     * If the placeholder requires a player to lookup.
     */
    private final boolean requiresPlayer;

    /**
     * The plugin for the placeholder.
     */
    @Nullable
    private final EcoPlugin plugin;

    /**
     * Create a placeholder entry that doesn't require a player.
     *
     * @param identifier The identifier of the placeholder.
     * @param function   A lambda to get the result of the placeholder given a player.
     * @deprecated Specify a plugin.
     */
    @Deprecated
    public PlaceholderEntry(@NotNull final String identifier,
                            @NotNull final Function<Player, String> function) {
        this(identifier, function, false);
    }

    /**
     * Create a placeholder entry that may require a player.
     *
     * @param identifier     The identifier of the placeholder.
     * @param function       A lambda to get the result of the placeholder.
     * @param requiresPlayer If the placeholder requires a player.
     * @deprecated Specify a plugin.
     */
    @Deprecated
    public PlaceholderEntry(@NotNull final String identifier,
                            @NotNull final Function<Player, String> function,
                            final boolean requiresPlayer) {
        this(null, identifier, function, requiresPlayer);
    }

    /**
     * Create a placeholder entry that doesn't require a player.
     *
     * @param plugin     The plugin for the placeholder.
     * @param identifier The identifier of the placeholder.
     * @param function   A lambda to get the result of the placeholder given a player.
     */
    public PlaceholderEntry(@Nullable final EcoPlugin plugin,
                            @NotNull final String identifier,
                            @NotNull final Function<Player, String> function) {
        this(plugin, identifier, function, false);
    }

    /**
     * Create a placeholder entry that may require a player.
     *
     * @param plugin         The plugin for the placeholder.
     * @param identifier     The identifier of the placeholder.
     * @param function       A lambda to get the result of the placeholder.
     * @param requiresPlayer If the placeholder requires a player.
     */
    public PlaceholderEntry(@Nullable final EcoPlugin plugin,
                            @NotNull final String identifier,
                            @NotNull final Function<Player, String> function,
                            final boolean requiresPlayer) {
        this.plugin = plugin;
        this.identifier = identifier;
        this.function = function;
        this.requiresPlayer = requiresPlayer;
    }

    /**
     * Get the result of the placeholder with respect to a player.
     *
     * @param player The player to translate with respect to.
     * @return The result of the placeholder.
     */
    public String getResult(@Nullable final Player player) {
        if (player == null) {
            Validate.isTrue(!requiresPlayer, "null player passed to requiresPlayer placeholder.");
        }
        return this.function.apply(player);
    }

    /**
     * Get if the placeholder requires a player to get a result.
     *
     * @return If the placeholder requires a player.
     */
    public boolean requiresPlayer() {
        return requiresPlayer;
    }

    /**
     * Get the identifier.
     *
     * @return The identifier.
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Get the plugin.
     *
     * @return The plugin.
     */
    @Nullable
    public EcoPlugin getPlugin() {
        return plugin;
    }

    /**
     * Register the placeholder.
     */
    public void register() {
        PlaceholderManager.registerPlaceholder(this.toModernPlaceholder());
    }

    /**
     * Convert the placeholder to a modern placeholder.
     *
     * @return The placeholder.
     */
    Placeholder toModernPlaceholder() {
        if (this.requiresPlayer) {
            return new PlayerPlaceholder(
                    Objects.requireNonNullElse(plugin, Eco.get().getEcoPlugin()),
                    identifier,
                    function
            );
        } else {
            return new PlayerlessPlaceholder(
                    Objects.requireNonNullElse(plugin, Eco.get().getEcoPlugin()),
                    identifier,
                    () -> function.apply(null)
            );
        }
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PlaceholderEntry entry)) {
            return false;
        }
        return Objects.equals(this.getIdentifier(), entry.getIdentifier())
                && Objects.equals(this.getPlugin(), entry.getPlugin());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getIdentifier(), this.getPlugin());
    }
}
