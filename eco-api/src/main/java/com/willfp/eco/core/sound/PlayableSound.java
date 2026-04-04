package com.willfp.eco.core.sound;

import com.willfp.eco.core.config.interfaces.Config;
import com.willfp.eco.core.serialization.ConfigDeserializer;
import com.willfp.eco.util.SoundUtils;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * A sound that can be played at a location.
 *
 * @param sound    The sound.
 * @param pitch    The pitch.
 * @param volume   The volume.
 * @param enabled  Whether the sound is enabled.
 * @param category The sound category.
 */
public record PlayableSound(@NotNull Sound sound,
                            double pitch,
                            double volume,
                            boolean enabled,
                            @NotNull SoundCategory category) {

    private static final ConfigDeserializer<PlayableSound> DESERIALIZER = new Deserializer();

    /**
     * Create a sound with default volume, enabled true, and category MASTER.
     *
     * @param sound The sound.
     * @param pitch The pitch.
     */
    public PlayableSound(@NotNull final Sound sound,
                         final double pitch) {
        this(sound, pitch, 1.0, true, SoundCategory.MASTER);
    }

    /**
     * Play the sound to a player if enabled.
     *
     * @param player The player.
     */
    public void playTo(@NotNull final Player player) {
        if (!enabled) return;
        player.playSound(player.getLocation(), sound, category, (float) volume, (float) pitch);
    }

    /**
     * Play the sound at a location if enabled.
     *
     * @param location The location.
     */
    public void playAt(@NotNull final Location location) {
        if (!enabled) return;
        World world = location.getWorld();
        if (world != null) {
            world.playSound(location, sound, category, (float) volume, (float) pitch);
        }
    }

    /**
     * Parse a playable sound from config.
     *
     * @param config The config.
     * @return The sound, or null if invalid.
     */
    @Nullable
    public static PlayableSound create(@NotNull final Config config) {
        return DESERIALIZER.deserialize(config);
    }

    /**
     * The deserializer for {@link PlayableSound}.
     */
    private static final class Deserializer implements ConfigDeserializer<PlayableSound> {
        @Override
        public @Nullable PlayableSound deserialize(@NotNull final Config config) {
            if (!config.has("sound")) return null;

            Sound sound = SoundUtils.getSound(config.getString("sound"));
            if (sound == null) return null;

            double pitch = Objects.requireNonNullElse(config.getDoubleOrNull("pitch"), 1.0);
            double volume = Objects.requireNonNullElse(config.getDoubleOrNull("volume"), 1.0);
            boolean enabled = Objects.requireNonNullElse(config.getBoolOrNull("enabled"), true);

            SoundCategory category;
            String catString = config.getStringOrNull("category");
            if (catString == null) {
                category = SoundCategory.MASTER;
            } else {
                try {
                    category = SoundCategory.valueOf(catString.toUpperCase());
                } catch (IllegalArgumentException e) {
                    category = SoundCategory.MASTER;
                }
            }

            return new PlayableSound(sound, pitch, volume, enabled, category);
        }
    }
}