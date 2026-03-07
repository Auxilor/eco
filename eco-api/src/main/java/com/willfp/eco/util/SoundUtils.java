package com.willfp.eco.util;

import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

/**
 * Utilities / API methods for sounds.
 */
public final class SoundUtils {
    /**
     * Get a sound in a version-compatible way.
     *
     * @param name The name of the sound, case-insensitive.
     * @return The sound, or null if not found.
     */
    @Nullable
    public static Sound getSound(@NotNull final String name) {
        // First try from registry (preferred)
        Sound fromRegistry = Registry.SOUNDS.get(NamespacedKey.minecraft(name.toLowerCase()));
        if (fromRegistry != null) {
            return fromRegistry;
        }

        // Next try using reflection (for legacy enum names)
        try {
            Field field = Sound.class.getDeclaredField(name.toUpperCase());
            return (Sound) field.get(null);
        } catch (ReflectiveOperationException e) {
            return null;
        }
    }

    private SoundUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
