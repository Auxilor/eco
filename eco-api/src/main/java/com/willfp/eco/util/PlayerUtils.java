package com.willfp.eco.util;

import com.willfp.eco.core.Eco;
import com.willfp.eco.core.Prerequisite;
import com.willfp.eco.core.data.PlayerProfile;
import com.willfp.eco.core.data.keys.PersistentDataKey;
import com.willfp.eco.core.data.keys.PersistentDataKeyType;
import com.willfp.eco.core.integrations.anticheat.AnticheatManager;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Tameable;
import org.bukkit.projectiles.ProjectileSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

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
     * Get the audience from a player.
     *
     * @param player The player.
     * @return The audience.
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
     * @return The audience.
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
     *
     * @param player The player.
     * @return The player name.
     */
    public static String getSavedDisplayName(@NotNull final OfflinePlayer player) {
        if (player instanceof Player onlinePlayer) {
            updateSavedDisplayName(onlinePlayer);
        }

        PlayerProfile profile = PlayerProfile.load(player);

        String saved = profile.read(PLAYER_NAME_KEY);

        if (saved.equals(PLAYER_NAME_KEY.getDefaultValue())) {
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
        profile.write(PLAYER_NAME_KEY, player.getDisplayName());
    }

    /**
     * Run something with the player exempted.
     *
     * @param player The player.
     * @param action The action.
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
     * Run something with the player exempted.
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
     *
     * @param entity The entity.
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
}
