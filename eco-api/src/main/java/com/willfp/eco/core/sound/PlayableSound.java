package com.willfp.eco.core.sound;

import com.willfp.eco.core.config.interfaces.Config;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A playable Bukkit {@link Sound}.
 */
public final class PlayableSound extends AbstractPlayableSound<Sound> {

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
     * Create a sound with a fixed pitch.
     *
     * @param sound    The sound.
     * @param pitch    The pitch.
     * @param volume   The volume.
     * @param enabled  If the sound is enabled.
     * @param category The sound category.
     */
    public PlayableSound(@NotNull final Sound sound,
                         final double pitch,
                         final double volume,
                         final boolean enabled,
                         @NotNull final SoundCategory category) {
        this(sound, pitch, pitch, volume, enabled, category);
    }

    /**
     * Create a sound with a variable pitch.
     *
     * @param sound    The sound.
     * @param minPitch The minimum pitch.
     * @param maxPitch The maximum pitch.
     * @param volume   The volume.
     * @param enabled  If the sound is enabled.
     * @param category The sound category.
     */
    public PlayableSound(@NotNull final Sound sound,
                         final double minPitch,
                         final double maxPitch,
                         final double volume,
                         final boolean enabled,
                         @NotNull final SoundCategory category) {
        super(sound, minPitch, maxPitch, volume, enabled, category);
    }

    @Override
    protected void doPlayTo(@NotNull final Player player,
                            @NotNull final SoundCategory category,
                            final float volume,
                            final float pitch) {
        player.playSound(player.getLocation(), source(), category, volume, pitch);
    }

    @Override
    protected void doPlayAt(@NotNull final World world,
                            @NotNull final Location location,
                            @NotNull final SoundCategory category,
                            final float volume,
                            final float pitch) {
        world.playSound(location, source(), category, volume, pitch);
    }

    /**
     * Parse a playable sound from config.
     *
     * @param config The config.
     * @return The sound, or null if invalid.
     */
    @Nullable
    public static AbstractPlayableSound<?> create(@NotNull final Config config) {
        return AbstractPlayableSound.create(config);
    }
}
