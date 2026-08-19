package com.willfp.eco.core.floodgate;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Shared access point for Floodgate, the plugin that allows Bedrock edition players to join
 * Java edition servers through Geyser.
 * <p>
 * Floodgate is an optional dependency; every method here degrades gracefully to vanilla
 * behaviour when it isn't installed, so callers never need to guard their calls.
 * <p>
 * Bedrock players are given a username prefix (a full stop by default) and a version 0
 * UUID derived from their Xbox Live ID, neither of which the server can resolve through
 * the usual Mojang lookups. This class exists so that name to player resolution can account
 * for that in one place rather than at every call site.
 */
public final class FloodgateService {
    /**
     * The bridge to the Floodgate API, or null if it hasn't been created yet.
     * <p>
     * This is only ever populated with a working bridge; a failed creation is not cached, as
     * eco loads at startup and may well be asked for a bridge before Floodgate has enabled.
     */
    private static volatile FloodgateBridge bridge = null;

    /**
     * If bridge creation has already failed, to avoid logging the same error repeatedly.
     */
    private static volatile boolean failed = false;

    private FloodgateService() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Get the bridge to the Floodgate API, creating it if necessary.
     *
     * @return The bridge, or null if Floodgate isn't installed or couldn't be hooked into.
     */
    @Nullable
    private static FloodgateBridge getBridge() {
        FloodgateBridge existing = bridge;

        if (existing != null) {
            return existing;
        }

        if (failed || !Bukkit.getPluginManager().isPluginEnabled("floodgate")) {
            return null;
        }

        synchronized (FloodgateService.class) {
            if (bridge != null) {
                return bridge;
            }

            try {
                bridge = new FloodgateBridgeImpl();
            } catch (Throwable e) {
                failed = true;

                Bukkit.getLogger().log(
                        Level.WARNING,
                        "[eco] Floodgate is installed but its API could not be hooked into. "
                                + "Bedrock players will be resolved as if Floodgate were absent.",
                        e
                );
            }

            return bridge;
        }
    }

    /**
     * If Floodgate is installed, enabled, and could be hooked into.
     *
     * @return If Floodgate is available.
     */
    public static boolean isEnabled() {
        return getBridge() != null;
    }

    /**
     * Get the prefix that Floodgate prepends to Bedrock player usernames in order to keep
     * them from colliding with Java usernames.
     * <p>
     * This is a full stop by default, but servers can configure it, including to an empty
     * string.
     *
     * @return The prefix, or an empty string if Floodgate isn't available.
     */
    @NotNull
    public static String getPrefix() {
        FloodgateBridge bridge = getBridge();

        return bridge == null ? "" : bridge.getPrefix();
    }

    /**
     * Remove the Floodgate username prefix from a name, if it has one.
     *
     * @param name The name, which may or may not be prefixed.
     * @return The name without a prefix, which is the player's Bedrock gamertag if they are
     * a Bedrock player.
     */
    @NotNull
    public static String stripPrefix(@NotNull final String name) {
        String prefix = getPrefix();

        if (prefix.isEmpty() || !name.startsWith(prefix)) {
            return name;
        }

        return name.substring(prefix.length());
    }

    /**
     * If the player with the given UUID is a Bedrock player.
     * <p>
     * This detects both unlinked Bedrock players, who are online under a Floodgate UUID, and
     * linked ones, who are online under the UUID of the Java account they linked to.
     * <p>
     * Only reliable for online players; use {@link #isBedrockUniqueId(UUID)} to test a UUID
     * on its own.
     *
     * @param uuid The UUID.
     * @return If the player is a Bedrock player, or false if Floodgate isn't available.
     */
    public static boolean isBedrockPlayer(@NotNull final UUID uuid) {
        FloodgateBridge bridge = getBridge();

        return bridge != null && bridge.isBedrockPlayer(uuid);
    }

    /**
     * If the given player is a Bedrock player.
     *
     * @param player The player.
     * @return If the player is a Bedrock player, or false if Floodgate isn't available.
     * @see #isBedrockPlayer(UUID)
     */
    public static boolean isBedrockPlayer(@NotNull final OfflinePlayer player) {
        return isBedrockPlayer(player.getUniqueId());
    }

    /**
     * If the given UUID has the format Floodgate generates for unlinked Bedrock players,
     * which is a version 0 UUID with the player's Xbox Live ID in its lower 64 bits.
     * <p>
     * Unlike {@link #isBedrockPlayer(UUID)} this works offline, but it cannot recognise a
     * Bedrock player who has linked a Java account, as they use their Java UUID.
     *
     * @param uuid The UUID.
     * @return If the UUID is a Floodgate UUID, or false if Floodgate isn't available.
     */
    public static boolean isBedrockUniqueId(@NotNull final UUID uuid) {
        FloodgateBridge bridge = getBridge();

        return bridge != null && bridge.isBedrockUniqueId(uuid);
    }

    /**
     * Look up the UUID the server uses for an online Bedrock player, by name.
     * <p>
     * The name may be given with or without the Floodgate prefix. This never makes a network
     * request, so it only finds players who are currently online; use
     * {@link #fetchUniqueId(String)} to look up an offline Bedrock player.
     *
     * @param name The player name or Bedrock gamertag.
     * @return The UUID, or null if no online Bedrock player matches, or if Floodgate isn't
     * available.
     */
    @Nullable
    public static UUID findUniqueId(@NotNull final String name) {
        FloodgateBridge bridge = getBridge();

        if (bridge == null) {
            return null;
        }

        return bridge.findUniqueId(name, stripPrefix(name));
    }

    /**
     * Look up the Floodgate UUID for a Bedrock gamertag through the Geyser global API.
     * <p>
     * This makes a network request, so it must not be waited on from the main thread. Note
     * that the returned UUID is the player's <i>unlinked</i> Floodgate UUID; if they have
     * linked a Java account then the server will be using that account's UUID instead.
     *
     * @param gamertag The Bedrock gamertag, without a prefix.
     * @return A future completing with the UUID, with null if no such gamertag exists, or
     * with null immediately if Floodgate isn't available.
     */
    @NotNull
    public static CompletableFuture<UUID> fetchUniqueId(@NotNull final String gamertag) {
        FloodgateBridge bridge = getBridge();

        if (bridge == null) {
            return CompletableFuture.completedFuture(null);
        }

        return bridge.fetchUniqueId(stripPrefix(gamertag));
    }

    /**
     * Look up an offline player by name, accounting for Bedrock players.
     * <p>
     * {@link Bukkit#getOfflinePlayer(String)} cannot resolve a Bedrock player who isn't in the
     * server's user cache: their gamertag isn't a valid Java username, so the lookup misses and
     * an offline mode UUID is invented instead, which belongs to nobody. This method tries, in
     * order:
     * <ol>
     *     <li>Online Bedrock players, matched on their gamertag or their server username.</li>
     *     <li>The name as given, through the server's user cache.</li>
     *     <li>The name with the Floodgate prefix prepended, so that a Bedrock player can be
     *     referred to without it.</li>
     * </ol>
     * If none of those find a player who has played before, the result of the plain
     * {@link Bukkit#getOfflinePlayer(String)} lookup is returned, so behaviour is unchanged
     * from not having Floodgate installed at all.
     *
     * @param name The player name or Bedrock gamertag.
     * @return The offline player, never null.
     */
    @NotNull
    @SuppressWarnings("deprecation")
    public static OfflinePlayer getOfflinePlayer(@NotNull final String name) {
        UUID uuid = findUniqueId(name);

        if (uuid != null) {
            return Bukkit.getOfflinePlayer(uuid);
        }

        OfflinePlayer direct = Bukkit.getOfflinePlayer(name);

        if (hasPlayed(direct)) {
            return direct;
        }

        String prefix = getPrefix();

        if (!prefix.isEmpty() && !name.startsWith(prefix)) {
            OfflinePlayer prefixed = Bukkit.getOfflinePlayer(prefix + name);

            if (hasPlayed(prefixed)) {
                return prefixed;
            }
        }

        return direct;
    }

    /**
     * Look up an online player by name, accounting for Bedrock players.
     * <p>
     * {@link Bukkit#getPlayer(String)} matches online players by name prefix, so it cannot
     * find a Bedrock player from the name anyone would type: Floodgate has renamed them to
     * {@code .Notch}, which does not start with {@code Notch}. This falls back to matching
     * online Bedrock players on their gamertag, or on the prefixed name if that is what was
     * given.
     * <p>
     * Prefer this over {@link Bukkit#getPlayer(String)} anywhere a name comes from a command
     * argument or a config value.
     *
     * @param name The player name or Bedrock gamertag.
     * @return The player, or null if nobody online matches.
     */
    @Nullable
    public static Player findOnlinePlayer(@NotNull final String name) {
        Player direct = Bukkit.getPlayer(name);

        if (direct != null) {
            return direct;
        }

        UUID uuid = findUniqueId(name);

        return uuid == null ? null : Bukkit.getPlayer(uuid);
    }

    /**
     * Get the raw Bedrock gamertag of an online player.
     * <p>
     * Unlike the name the server uses, this has no prefix, keeps its original spacing, and is
     * not shortened, so it is what the player sees themselves called in game.
     *
     * @param player The player.
     * @return The gamertag, or null if the player isn't a Bedrock player, or if Floodgate
     * isn't available.
     */
    @Nullable
    public static String getGamertag(@NotNull final OfflinePlayer player) {
        FloodgateBridge bridge = getBridge();

        return bridge == null ? null : bridge.getGamertag(player.getUniqueId());
    }

    /**
     * If a player goes by a name, either as their server username or as their Bedrock gamertag.
     * <p>
     * Use this instead of comparing against {@link Player#getName()} directly when the name
     * being tested was written by a person, as they will not have included the Floodgate
     * prefix.
     *
     * @param player The player.
     * @param name The name to test.
     * @return If the name refers to the player.
     */
    public static boolean namesMatch(@NotNull final OfflinePlayer player,
                                     @NotNull final String name) {
        if (name.equalsIgnoreCase(player.getName())) {
            return true;
        }

        return name.equalsIgnoreCase(getGamertag(player));
    }

    /**
     * If an offline player is known to the server.
     *
     * @param player The player.
     * @return If the player is online or has played before.
     */
    private static boolean hasPlayed(@NotNull final OfflinePlayer player) {
        return player.hasPlayedBefore() || player.isOnline();
    }
}
