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
     * Display lines are tagged by prepending {@link Display#PREFIX} to their text content. The
     * line is flattened before checking, as lore lines are wrapped in an empty parent component
     * to force italics off, which leaves the tagged text in a child rather than on the line
     * itself.
     *
     * @param line The lore line.
     * @return If the line is a display line.
     */
    static boolean isDisplayLine(@NotNull final Component line) {
        return flatten(line, new StringBuilder()).toString().startsWith(Display.PREFIX);
    }

    /**
     * Append the text content of a component and its children, stopping once enough characters
     * have been collected to test against {@link Display#PREFIX}.
     *
     * @param component The component.
     * @param builder   The builder to append to.
     * @return The same builder.
     */
    private static StringBuilder flatten(@NotNull final Component component,
                                         @NotNull final StringBuilder builder) {
        if (builder.length() >= Display.PREFIX.length()) {
            return builder;
        }

        if (component instanceof TextComponent textComponent) {
            builder.append(textComponent.content());
        }

        for (Component child : component.children()) {
            if (builder.length() >= Display.PREFIX.length()) {
                break;
            }

            flatten(child, builder);
        }

        return builder;
    }

    /**
     * Utility class, cannot be instantiated.
     */
    private DisplayLines() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
