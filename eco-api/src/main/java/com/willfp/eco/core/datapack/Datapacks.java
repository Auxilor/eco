package com.willfp.eco.core.datapack;

import com.willfp.eco.core.Eco;
import com.willfp.eco.core.EcoPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * Write, validate and install datapacks.
 * <p>
 * Minecraft's plugin API cannot register a biome, a dimension, a structure, a
 * custom enchantment or a damage type: those live in data-driven registries
 * reachable only from a datapack. This is the shared mechanism for writing one.
 * <p>
 * There are two entry points, because the two lifecycle classes are genuinely
 * different:
 * <ul>
 *     <li><strong>Bootstrap-only content</strong> (worldgen, dimensions,
 *     enchantments, damage types, and everything else) resolves once at server
 *     start. Register a {@link DatapackContributor} and eco owns the timing.</li>
 *     <li><strong>Reloadable content</strong> (recipes, advancements,
 *     functions, tags, loot tables, predicates, item modifiers) applies on
 *     {@code /reload}. Use {@link DatapackHandle#apply(java.util.function.Consumer)}
 *     directly.</li>
 * </ul>
 * A recipe plugin should never have to think about world load order. A biome
 * plugin cannot avoid it.
 */
public final class Datapacks {
    /**
     * Get the pack belonging to a plugin. Created on first use.
     *
     * @param plugin The plugin.
     * @return The handle.
     */
    @NotNull
    public static DatapackHandle forPlugin(@NotNull final EcoPlugin plugin) {
        return Eco.get().getDatapackHandle(plugin);
    }

    /**
     * Register a contributor for bootstrap-only content.
     * <p>
     * The contributor is invoked immediately, and again on every reload of the
     * owning plugin, so that config changes are re-emitted for the next
     * restart. It must be idempotent, side-effect-free and cheap.
     *
     * Registering rebuilds the pack immediately, so the returned result says
     * whether the content this contributor emits is valid and installed. A
     * consumer migrating off its own pack should wait for
     * {@link InstallResult#succeeded()} before deleting the old one.
     *
     * @param plugin      The plugin.
     * @param contributor The contributor.
     * @return The outcome of the rebuild this registration triggered.
     */
    @NotNull
    public static InstallResult register(@NotNull final EcoPlugin plugin,
                                         @NotNull final DatapackContributor contributor) {
        return Eco.get().registerDatapackContributor(plugin, contributor);
    }

    /**
     * If any plugin has written bootstrap-only content that is not yet live.
     * <p>
     * Eco never restarts the server: it signals, and the admin or the consuming
     * plugin decides. Creating a world before the restart happens will silently
     * use the old, frozen registries.
     *
     * @return If a restart is pending.
     */
    public static boolean restartPending() {
        return Eco.get().isDatapackRestartPending();
    }

    private Datapacks() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
