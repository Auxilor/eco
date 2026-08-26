package com.willfp.eco.core.datapack;

import org.jetbrains.annotations.NotNull;

/**
 * Contributes bootstrap-only datapack content.
 * <p>
 * Bootstrap-only content (biomes, dimensions, enchantments, damage types, and
 * everything else outside the small reloadable set) resolves once at server
 * start, so eco owns the timing. Register a contributor with
 * {@link Datapacks#register(com.willfp.eco.core.EcoPlugin, DatapackContributor)}
 * and eco will invoke it whenever the pack needs rebuilding.
 * <p>
 * <strong>Contributors must be idempotent, side-effect-free and cheap.</strong>
 * They are invoked more than once per server lifetime: on registration, and
 * again on every reload of the owning plugin.
 */
@FunctionalInterface
public interface DatapackContributor {
    /**
     * Add this contributor's entries to the draft.
     *
     * @param draft The draft.
     */
    void contribute(@NotNull DatapackDraft draft);
}
