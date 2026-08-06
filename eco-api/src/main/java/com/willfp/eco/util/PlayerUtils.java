package com.willfp.eco.util;

import com.willfp.eco.core.Eco;
import com.willfp.eco.core.Prerequisite;
import com.willfp.eco.core.data.PlayerProfile;
import com.willfp.eco.core.data.keys.PersistentDataKey;
import com.willfp.eco.core.data.keys.PersistentDataKeyType;
import com.willfp.eco.core.integrations.anticheat.AnticheatManager;
import java.util.function.Consumer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.projectiles.ProjectileSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Utilities / API methods for players.
 */
public final class PlayerUtils {
    /**
     * The data key for saved player names.
     */
    private static final PersistentDataKey<String> PLAYER_NAME_KEY = new PersistentDataKey<>(
            NamespacedKeyUtils.createEcoKey("player_name"),
            PersistentDataKeyType.STRING,
            "Unknown Player"
    );

    /**
     * The data key for saved player display names.
     */
    private static final PersistentDataKey<String> PLAYER_DISPLAY_NAME_KEY = new PersistentDataKey<>(
            NamespacedKeyUtils.createEcoKey("player_display_name"),
            PersistentDataKeyType.STRING,
            "Unknown Player"
    );

    /**
     * The data key for saved player health.
     */
    private static final PersistentDataKey<Double> PLAYER_HEALTH_KEY = new PersistentDataKey<>(
            NamespacedKeyUtils.createEcoKey("player_health"),
            PersistentDataKeyType.DOUBLE,
            20.0
    );

    /**
     * Get the audience from a player.
     *
     * @param player The player.
     * @return The audience, or an empty audience if one could not be created.
     */
    @NotNull
    public static Audience getAudience(@NotNull final Player player) {
        BukkitAudiences adventure = Eco.get().getAdventure();

        if (Prerequisite.HAS_PAPER.isMet()) {
            if (player instanceof Audience) {
                return (Audience) player;
            } else {
                return Audience.empty();
            }
        } else {
            if (adventure == null) {
                return Audience.empty();
            } else {
                return adventure.player(player);
            }
        }
    }

    /**
     * Get the audience from a command sender.
     *
     * @param sender The command sender.
     * @return The audience, or an empty audience if one could not be created.
     */
    @NotNull
    public static Audience getAudience(@NotNull final CommandSender sender) {
        BukkitAudiences adventure = Eco.get().getAdventure();

        if (Prerequisite.HAS_PAPER.isMet()) {
            if (sender instanceof Audience) {
                return (Audience) sender;
            } else {
                return Audience.empty();
            }
        } else {
            if (adventure == null) {
                return Audience.empty();
            } else {
                return adventure.sender(sender);
            }
        }
    }

    /**
     * Get saved display name for an offline player.
     * <p>
     * If the player is online then the saved value is refreshed first.
     *
     * @param player The player.
     * @return The saved display name, falling back to the player's name if none has been saved.
     */
    public static String getSavedDisplayName(@NotNull final OfflinePlayer player) {
        if (player instanceof Player onlinePlayer) {
            updateSavedDisplayName(onlinePlayer);
        }

        PlayerProfile profile = PlayerProfile.load(player);

        String saved = profile.read(PLAYER_DISPLAY_NAME_KEY);

        if (saved.equals(PLAYER_DISPLAY_NAME_KEY.getDefaultValue())) {
            return player.getName();
        }

        return saved;
    }

    /**
     * Update the saved display name for a player.
     *
     * @param player The player.
     */
    public static void updateSavedDisplayName(@NotNull final Player player) {
        PlayerProfile profile = PlayerProfile.load(player);
        profile.write(PLAYER_DISPLAY_NAME_KEY, player.getDisplayName());
    }

    /**
     * Get the saved name for an offline player.
     * <p>
     * If the player is online then the saved value is refreshed first.
     *
     * @param player The player.
     * @return The saved name, falling back to the player's name if none has been saved.
     */
    public static String getSavedName(@NotNull final OfflinePlayer player) {
        if (player instanceof Player onlinePlayer) {
            updateSavedName(onlinePlayer);
        }

        PlayerProfile profile = PlayerProfile.load(player);

        String saved = profile.read(PLAYER_NAME_KEY);

        if (saved.equals(PLAYER_NAME_KEY.getDefaultValue())) {
            return player.getName();
        }

        return saved;
    }

    /**
     * Update the saved name for a player.
     *
     * @param player The player.
     */
    public static void updateSavedName(@NotNull final Player player) {
        PlayerProfile profile = PlayerProfile.load(player);
        profile.write(PLAYER_NAME_KEY, player.getName());
    }

    /**
     * Get the saved health for an offline player.
     * <p>
     * The saved value is only updated by {@link #saveHealth(Player)}, so it may be stale for an
     * online player.
     *
     * @param player The player.
     * @return The saved health in half-hearts, or 20.0 if none has been saved.
     */
    public static double getSavedHealth(@NotNull final OfflinePlayer player) {
        PlayerProfile profile = PlayerProfile.load(player);

        return profile.read(PLAYER_HEALTH_KEY);
    }

    /**
     * Update the saved health for a player.
     *
     * @param player The player.
     */
    public static void saveHealth(@NotNull final Player player) {
        PlayerProfile profile = PlayerProfile.load(player);
        profile.write(PLAYER_HEALTH_KEY, player.getHealth());
    }

    /**
     * Run something with the player exempted from anticheats.
     * <p>
     * The player is exempted through the {@link AnticheatManager} for the duration of the action,
     * and is unexempted afterwards even if the action throws.
     *
     * @param player The player.
     * @param action The action, which is passed the player.
     */
    public static void runExempted(@NotNull final Player player,
                                   @NotNull final Consumer<Player> action) {
        try {
            AnticheatManager.exemptPlayer(player);
            action.accept(player);
        } finally {
            AnticheatManager.unexemptPlayer(player);
        }
    }

    /**
     * Run something with the player exempted from anticheats.
     * <p>
     * The player is exempted through the {@link AnticheatManager} for the duration of the action,
     * and is unexempted afterwards even if the action throws.
     *
     * @param player The player.
     * @param action The action.
     */
    public static void runExempted(@NotNull final Player player,
                                   @NotNull final Runnable action) {
        try {
            AnticheatManager.exemptPlayer(player);
            action.run();
        } finally {
            AnticheatManager.unexemptPlayer(player);
        }
    }

    /**
     * Try an entity as a player.
     * <p>
     * Resolves the entity itself if it is a {@link Player}, the shooter of a {@link Projectile},
     * or the owner of a {@link Tameable}.
     *
     * @param entity The entity, may be null.
     * @return The player, or null if no player could be found.
     */
    @Nullable
    public static Player tryAsPlayer(@Nullable final Entity entity) {
        if (entity == null) {
            return null;
        }

        if (entity instanceof Player player) {
            return player;
        }

        if (entity instanceof Projectile projectile) {
            ProjectileSource shooter = projectile.getShooter();
            if (shooter instanceof Player player) {
                return player;
            }
        }

        if (entity instanceof Tameable tameable) {
            AnimalTamer tamer = tameable.getOwner();
            if (tamer instanceof Player player) {
                return player;
            }
        }

        return null;
    }

    private PlayerUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Gives the player the amount of experience specified.
     *
     * @param player       The player.
     * @param amount       The amount of experience points to give.
     * @param applyMending If items enchanted with Mending should be repaired first, with the same
     *                     behaviour as picking up experience orbs.
     */
    public static void giveExpAndApplyMending(@NotNull Player player, int amount, boolean applyMending) {
        Eco.get().giveExpAndApplyMending(player, amount, applyMending);
    }
}
