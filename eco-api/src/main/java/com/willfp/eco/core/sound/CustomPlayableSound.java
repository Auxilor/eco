package com.willfp.eco.core.sound;

import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * A playable sound identified by a namespaced key string (custom / resource-pack sound).
 */
public final class CustomPlayableSound extends AbstractPlayableSound<String> {

    /**
     * Create a custom sound.
     *
     * @param soundKey The namespaced sound key.
     * @param minPitch The minimum pitch.
     * @param maxPitch The maximum pitch.
     * @param volume   The volume.
     * @param enabled  If the sound is enabled.
     * @param category The sound category.
     */
    public CustomPlayableSound(@NotNull final String soundKey,
                               final double minPitch,
                               final double maxPitch,
                               final double volume,
                               final boolean enabled,
                               @NotNull final SoundCategory category) {
        super(soundKey, minPitch, maxPitch, volume, enabled, category);
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
}
