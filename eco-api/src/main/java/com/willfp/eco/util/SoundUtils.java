package com.willfp.eco.util;

import java.lang.reflect.Field;

import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Utilities / API methods for sounds.
 */
public final class SoundUtils {
    /**
     * Get a sound in a version-compatible way.
     * <p>
     * The name is first looked up in {@link Registry#SOUNDS} as a {@link NamespacedKey}, using the
     * {@code minecraft} namespace if the name does not contain a colon. If that fails, the name is
     * looked up as a legacy {@link Sound} enum constant by reflection, which lets old constant
     * names such as {@code ENTITY_ITEM_BREAK} keep working.
     *
     * @param name The name of the sound, case-insensitive, optionally namespaced.
     * @return The sound, or null if not found.
     */
    @Nullable
    public static Sound getSound(@NotNull final String name) {
        NamespacedKey key = name.contains(":") ? NamespacedKeyUtils.fromString(name.toLowerCase()) : NamespacedKey.minecraft(name.toLowerCase());

        // First try from registry (preferred)
        Sound fromRegistry = Registry.SOUNDS.get(key);

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
