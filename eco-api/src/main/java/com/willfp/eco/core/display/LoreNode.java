package com.willfp.eco.core.display;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

/**
 * A lore line, and whether eco's display owns it.
 *
 * @param component The line. Display lines hold their content without marker or prefix.
 * @param display   If the line was added by a display module.
 */
record LoreNode(@NotNull Component component, boolean display) {
    /**
     * A line eco did not write.
     *
     * @param component The line.
     * @return The node.
     */
    static LoreNode foreign(@NotNull final Component component) {
        return new LoreNode(component, false);
    }

    /**
     * A line added by a display module.
     *
     * @param component The line content.
     * @return The node.
     */
    static LoreNode display(@NotNull final Component component) {
        return new LoreNode(component, true);
    }
}
