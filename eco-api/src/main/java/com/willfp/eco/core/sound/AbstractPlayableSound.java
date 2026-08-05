package com.willfp.eco.core.sound;

import com.willfp.eco.core.config.interfaces.Config;
import com.willfp.eco.core.placeholder.context.PlaceholderContext;
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
     * <p>
     * Pitch/volume expressions are evaluated against an empty placeholder context, so
     * placeholders (e.g. {@code %libreforge_points_example%}) won't resolve; use
     * {@link #create(Config, PlaceholderContext)} when a context is available.
     *
     * @param config The config.
     * @return The sound, or null if invalid.
     */
    @Nullable
    public static AbstractPlayableSound<?> create(@NotNull final Config config) {
        return create(config, PlaceholderContext.EMPTY);
    }

    /**
     * Parse a playable sound from config, evaluating pitch/volume expressions against
     * the given placeholder context.
     *
     * @param config  The config.
     * @param context The placeholder context to evaluate pitch/volume expressions against.
     * @return The sound, or null if invalid.
     */
    @Nullable
    public static AbstractPlayableSound<?> create(@NotNull final Config config,
                                                  @NotNull final PlaceholderContext context) {
        return deserialize(config, context);
    }

    static double readNumber(@NotNull final Config config,
                             @NotNull final String path,
                             final double fallback,
                             @NotNull final PlaceholderContext context) {
        Double direct = config.getDoubleOrNull(path);
        if (direct != null) {
            return direct;
        }

        String raw = config.getStringOrNull(path);
        if (raw == null || raw.isBlank()) {
            return fallback;
        }

        return readRangePart(raw, fallback, context);
    }

    static double readRangePart(@NotNull final String raw,
                                final double fallback,
                                @NotNull final PlaceholderContext context) {
        String trimmed = raw.trim();

        try {
            return Double.parseDouble(trimmed);
        } catch (NumberFormatException ignored) {
            Double evaluated = NumberUtils.evaluateExpressionOrNull(trimmed, context);
            return evaluated != null ? evaluated : fallback;
        }
    }

    @Nullable
    private static AbstractPlayableSound<?> deserialize(@NotNull final Config config,
                                                         @NotNull final PlaceholderContext context) {
        if (!config.has("sound")) return null;

        String soundKey = config.getString("sound");
        Sound sound = SoundUtils.getSound(soundKey);

        double minPitch;
        double maxPitch;

        String pitchString = config.getStringOrNull("pitch");
        if (pitchString != null && pitchString.contains("..")) {
            String[] parts = pitchString.split("\\.\\.", 2);
            if (parts.length == 2) {
                minPitch = readRangePart(parts[0], 1.0, context);
                maxPitch = readRangePart(parts[1], 1.0, context);
            } else {
                minPitch = 1.0;
                maxPitch = 1.0;
            }
        } else {
            double pitch = readNumber(config, "pitch", 1.0, context);
            minPitch = pitch;
            maxPitch = pitch;
        }

        double volume = readNumber(config, "volume", 1.0, context);
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
