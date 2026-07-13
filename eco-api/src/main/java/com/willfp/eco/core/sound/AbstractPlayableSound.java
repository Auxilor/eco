package com.willfp.eco.core.sound;

import com.willfp.eco.core.config.interfaces.Config;
import com.willfp.eco.core.serialization.ConfigDeserializer;
import com.willfp.eco.util.NumberUtils;
import com.willfp.eco.util.SoundUtils;

import java.util.Objects;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A sound that can be played at a location or to a player.
 *
 * @param <T> The type of sound source (e.g. {@link Sound} or {@link String} for custom sounds).
 */
public abstract class AbstractPlayableSound<T> {

    private static final ConfigDeserializer<AbstractPlayableSound<?>> DESERIALIZER = new Deserializer();

    private final T source;
    private final double minPitch;
    private final double maxPitch;
    private final double volume;
    private final boolean enabled;
    private final SoundCategory category;

    /**
     * @param source   The sound source.
     * @param minPitch The minimum pitch.
     * @param maxPitch The maximum pitch.
     * @param volume   The volume.
     * @param enabled  Whether the sound is enabled.
     * @param category The sound category.
     */
    protected AbstractPlayableSound(@NotNull final T source,
                                    final double minPitch,
                                    final double maxPitch,
                                    final double volume,
                                    final boolean enabled,
                                    @NotNull final SoundCategory category) {
        this.source = source;
        this.minPitch = minPitch;
        this.maxPitch = maxPitch;
        this.volume = volume;
        this.enabled = enabled;
        this.category = category;
    }

    /**
     * Get the sound source.
     *
     * @return The source.
     */
    @NotNull
    public T source() {
        return source;
    }

    /**
     * Get the minimum pitch.
     *
     * @return The minimum pitch.
     */
    public double minPitch() {
        return minPitch;
    }

    /**
     * Get the maximum pitch.
     *
     * @return The maximum pitch.
     */
    public double maxPitch() {
        return maxPitch;
    }

    /**
     * Get the volume.
     *
     * @return The volume.
     */
    public double volume() {
        return volume;
    }

    /**
     * Whether the sound is enabled.
     *
     * @return If enabled.
     */
    public boolean enabled() {
        return enabled;
    }

    /**
     * Get the sound category.
     *
     * @return The category.
     */
    @NotNull
    public SoundCategory category() {
        return category;
    }

    /**
     * Perform the actual play to a player. Called only when {@link #enabled()} is true.
     *
     * @param player   The player.
     * @param category The sound category.
     * @param volume   The volume.
     * @param pitch    The pitch (already randomised).
     */
    protected abstract void doPlayTo(@NotNull Player player,
                                     @NotNull SoundCategory category,
                                     float volume,
                                     float pitch);

    /**
     * Perform the actual play at a world location. Called only when {@link #enabled()} is true.
     *
     * @param world    The world.
     * @param location The location.
     * @param category The sound category.
     * @param volume   The volume.
     * @param pitch    The pitch (already randomised).
     */
    protected abstract void doPlayAt(@NotNull World world,
                                     @NotNull Location location,
                                     @NotNull SoundCategory category,
                                     float volume,
                                     float pitch);

    /**
     * Play the sound to a player if enabled.
     *
     * @param player The player.
     */
    public void playTo(@NotNull final Player player) {
        if (!enabled) return;
        float pitch = (float) NumberUtils.randFloat(minPitch, maxPitch);
        doPlayTo(player, category, (float) volume, pitch);
    }

    /**
     * Play the sound at a location if enabled.
     *
     * @param location The location.
     */
    public void playAt(@NotNull final Location location) {
        if (!enabled) return;
        World world = location.getWorld();
        if (world == null) return;
        float pitch = (float) NumberUtils.randFloat(minPitch, maxPitch);
        doPlayAt(world, location, category, (float) volume, pitch);
    }

    /**
     * Parse a playable sound from config.
     *
     * @param config The config.
     * @return The sound, or null if invalid.
     */
    @Nullable
    public static AbstractPlayableSound<?> create(@NotNull final Config config) {
        return DESERIALIZER.deserialize(config);
    }

    private static final class Deserializer implements ConfigDeserializer<AbstractPlayableSound<?>> {
        @Override
        public @Nullable AbstractPlayableSound<?> deserialize(@NotNull final Config config) {
            if (!config.has("sound")) return null;

            String soundKey = config.getString("sound");
            Sound sound = SoundUtils.getSound(soundKey);

            double minPitch = 1.0;
            double maxPitch = 1.0;

            String pitchString = config.getStringOrNull("pitch");
            if (pitchString != null && pitchString.contains("..")) {
                String[] parts = pitchString.split("\\.\\.", 2);
                try {
                    minPitch = Double.parseDouble(parts[0]);
                    maxPitch = Double.parseDouble(parts[1]);
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    minPitch = 1.0;
                    maxPitch = 1.0;
                }
            } else {
                double pitch = Objects.requireNonNullElse(config.getDoubleOrNull("pitch"), 1.0);
                minPitch = pitch;
                maxPitch = pitch;
            }

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

            if (sound != null) {
                return new PlayableSound(sound, minPitch, maxPitch, volume, enabled, category);
            } else {
                return new CustomPlayableSound(soundKey, minPitch, maxPitch, volume, enabled, category);
            }
        }
    }
}
