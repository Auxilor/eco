package com.willfp.eco.core.display;

import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.jetbrains.annotations.NotNull;

/**
 * The lore of an item being displayed.
 * <p>
 * Lines added here are owned by eco's display: they are marked, removed again on revert, and
 * never mistaken for another plugin's lore. Lines from other plugins are kept exactly as they
 * were.
 */
public interface DisplayLore {
    /**
     * Add lines above the current lore.
     *
     * @param lines The lines.
     */
    void prepend(@NotNull List<? extends ComponentLike> lines);

    /**
     * Add lines below the current lore.
     *
     * @param lines The lines.
     */
    void append(@NotNull List<? extends ComponentLike> lines);

    /**
     * The lines that were not added by display modules.
     *
     * @return The lines.
     */
    @NotNull
    List<Component> getForeignLines();
}
