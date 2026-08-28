package com.willfp.eco.core.display;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;

/**
 * Identifies the lore lines added by display modules.
 */
final class DisplayLines {
    /**
     * If a lore line was added by a display module.
     * <p>
     * Display lines are written by eco through Adventure, so {@link Display#PREFIX} always ends
     * up at the start of a component that owns the rest of the line: either the whole line as
     * plain text, or a parent whose children hold the coloured text after the prefix. Lore
     * lines are also wrapped in an empty parent to force italics off, so that wrapping is
     * unwrapped before checking.
     * <p>
     * Lore written through Bukkit's {@code ItemMeta#setLore(List)} is built by
     * {@code CraftChatMessage} instead, which splits a legacy string into flat siblings, so a
     * prefix written that way is a leaf sitting beside the rest of the line rather than owning
     * it. Those lines belong to whichever plugin wrote them - AdvancedEnchantments writes its
     * enchantment descriptions as {@code §z}-prefixed legacy strings - and are left alone.
     *
     * @param line The lore line.
     * @return If the line is a display line.
     */
    static boolean isDisplayLine(@NotNull final Component line) {
        Component component = line;

        // Unwrap the empty parents lore lines are wrapped in to force italics off.
        while (component instanceof TextComponent textComponent
                && textComponent.content().isEmpty()
                && component.children().size() == 1) {
            component = component.children().get(0);
        }

        return component instanceof TextComponent textComponent
                && textComponent.content().startsWith(Display.PREFIX);
    }

    /**
     * Utility class, cannot be instantiated.
     */
    private DisplayLines() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
