package com.willfp.eco.core.display;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Finds display modules that still use the item-mutating display methods.
 */
final class LegacyDisplayModules {
    /**
     * If each module class overrides a legacy display method.
     */
    private static final ClassValue<Boolean> OVERRIDES_LEGACY_DISPLAY = new ClassValue<>() {
        @Override
        protected Boolean computeValue(@NotNull final Class<?> type) {
            return overrides(type, ItemStack.class, Object[].class)
                    || overrides(type, ItemStack.class, Player.class, Object[].class)
                    || overrides(type, ItemStack.class, Player.class, DisplayProperties.class, Object[].class);
        }
    };

    /**
     * If a module overrides any of the legacy display methods.
     *
     * @param module The module.
     * @return If the module is a legacy module.
     */
    static boolean overridesLegacyDisplay(@NotNull final DisplayModule module) {
        return OVERRIDES_LEGACY_DISPLAY.get(module.getClass());
    }

    private static boolean overrides(@NotNull final Class<?> type,
                                     @NotNull final Class<?>... parameters) {
        try {
            return type.getMethod("display", parameters).getDeclaringClass() != DisplayModule.class;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    /**
     * Utility class, cannot be instantiated.
     */
    private LegacyDisplayModules() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
