package com.willfp.eco.core.sound;

import com.willfp.eco.core.config.interfaces.Config;
import com.willfp.eco.core.placeholder.context.PlaceholderContext;
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

    /**
     * The deserializer used to read sounds from configs.
     */
    private static final ConfigDeserializer<AbstractPlayableSound<?>> DESERIALIZER = new Deserializer();

    /**
     * The sound source.
     */
    private final T source;

    /**
     * The minimum pitch.
     */
    private final double minPitch;

    /**
     * The maximum pitch.
     */
    private final double maxPitch;

    /**
     * The volume.
     */
    private final double volume;

    /**
     * If the sound is enabled.
     */
    private final boolean enabled;

    /**
     * The sound category.
     */
    private final SoundCategory category;

    /**
     * Create a new playable sound.
     * <p>
     * The pitch is randomised between {@code minPitch} and {@code maxPitch} on each play;
     * pass the same value for both for a fixed pitch.
     *
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
     * <p>
     * Does nothing if {@link #enabled()} is false.
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
     * <p>
     * Does nothing if {@link #enabled()} is false, or if the location has no world.
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
     * <p>
     * Reads {@code sound}, {@code pitch} (a number, an expression, or a {@code min..max}
     * range), {@code volume}, {@code enabled}, and {@code category}. If the sound key
     * matches a Bukkit {@link Sound} a {@link PlayableSound} is returned, otherwise a
     * {@link CustomPlayableSound} is returned for the raw key.
     *
     * @param config The config.
     * @return The sound, or null if the config has no {@code sound} key.
     */
    @Nullable
    public static AbstractPlayableSound<?> create(@NotNull final Config config) {
        return DESERIALIZER.deserialize(config);
    }

    /**
     * Read a number from a config, falling back if it is absent or unparseable.
     * <p>
     * Accepts a plain number, or a string containing a number or a placeholder-free
     * expression.
     *
     * @param config   The config.
     * @param path     The path to read.
     * @param fallback The value to use if the path is absent or invalid.
     * @return The number.
     */
    static double readNumber(@NotNull final Config config,
                             @NotNull final String path,
                             final double fallback) {
        Double direct = config.getDoubleOrNull(path);
        if (direct != null) {
            return direct;
        }

        String raw = config.getStringOrNull(path);
        if (raw == null || raw.isBlank()) {
            return fallback;
        }

        return readRangePart(raw, fallback);
    }

    /**
     * Read one part of a range as a number.
     * <p>
     * Tries to parse the string directly, then falls back to evaluating it as an
     * expression, then to the fallback value.
     *
     * @param raw      The raw string.
     * @param fallback The value to use if the string is not a number or a valid expression.
     * @return The number.
     */
    static double readRangePart(@NotNull final String raw,
                                final double fallback) {
        String trimmed = raw.trim();

        try {
            return Double.parseDouble(trimmed);
        } catch (NumberFormatException ignored) {
            Double evaluated = NumberUtils.evaluateExpressionOrNull(trimmed, PlaceholderContext.EMPTY);
            return evaluated != null ? evaluated : fallback;
        }
    }

    /**
     * Deserializes playable sounds from configs.
     */
    private static final class Deserializer implements ConfigDeserializer<AbstractPlayableSound<?>> {
        @Override
        public @Nullable AbstractPlayableSound<?> deserialize(@NotNull final Config config) {
            if (!config.has("sound")) return null;

            String soundKey = config.getString("sound");
            Sound sound = SoundUtils.getSound(soundKey);

            double minPitch;
            double maxPitch;

            String pitchString = config.getStringOrNull("pitch");
            if (pitchString != null && pitchString.contains("..")) {
                String[] parts = pitchString.split("\\.\\.", 2);
                if (parts.length == 2) {
                    minPitch = readRangePart(parts[0], 1.0);
                    maxPitch = readRangePart(parts[1], 1.0);
                } else {
                    minPitch = 1.0;
                    maxPitch = 1.0;
                }
            } else {
                double pitch = readNumber(config, "pitch", 1.0);
                minPitch = pitch;
                maxPitch = pitch;
            }

            double volume = readNumber(config, "volume", 1.0);
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
