package com.willfp.eco.core.display;

import com.willfp.eco.util.StringUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Identifies the lore lines added by display modules.
 */
final class DisplayLines {
    /**
     * The insertion eco puts on the component that owns a display line.
     */
    static final String MARKER = "eco:display";

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
     * <p>
     * Lines eco renders also carry {@link #MARKER} as their insertion, which survives any
     * component-level copy and is recognised at any level of wrapping.
     *
     * @param line The lore line.
     * @return If the line is a display line.
     */
    static boolean isDisplayLine(@NotNull final Component line) {
        Component component = line;

        // Unwrap the empty parents lore lines are wrapped in to force italics off.
        while (!MARKER.equals(component.insertion())
                && component instanceof TextComponent textComponent
                && textComponent.content().isEmpty()
                && component.children().size() == 1) {
            component = component.children().get(0);
        }

        if (MARKER.equals(component.insertion())) {
            return true;
        }

        return component instanceof TextComponent textComponent
                && textComponent.content().startsWith(Display.PREFIX);
    }

    /**
     * Mark content as a display line.
     * <p>
     * The marker goes on a new owner component rather than on the italic wrapper eco adds when
     * the lore is set, as that wrapper is recognised by its style.
     *
     * @param content      The line content.
     * @param legacyPrefix If {@link Display#PREFIX} should start the line.
     * @return The display line.
     */
    @NotNull
    static Component mark(@NotNull final Component content,
                          final boolean legacyPrefix) {
        return Component.text(legacyPrefix ? Display.PREFIX : "")
                .insertion(MARKER)
                .append(content);
    }

    /**
     * The content of a line without its marker or {@link Display#PREFIX}.
     *
     * @param line The line.
     * @return The content.
     */
    @NotNull
    static Component withoutPrefix(@NotNull final Component line) {
        Component component = line;

        while (!MARKER.equals(component.insertion())
                && component instanceof TextComponent textComponent
                && textComponent.content().isEmpty()
                && component.children().size() == 1) {
            component = component.children().get(0);
        }

        if (component instanceof TextComponent textComponent
                && (MARKER.equals(textComponent.insertion()) || textComponent.content().startsWith(Display.PREFIX))) {
            String content = textComponent.content().startsWith(Display.PREFIX)
                    ? textComponent.content().substring(Display.PREFIX.length())
                    : textComponent.content();

            return textComponent.content(content).insertion(null);
        }

        String legacy = StringUtils.toLegacy(line);

        return StringUtils.toComponent(
                legacy.startsWith(Display.PREFIX) ? legacy.substring(Display.PREFIX.length()) : legacy
        );
    }

    /**
     * The lore with any stale display lines removed, or null if there are none.
     * <p>
     * Reverting identifies display lines by the shape eco writes them in, so a line that has
     * since been rewritten in another shape - anything that round-trips lore through legacy
     * strings does this, flattening the component tree - is left behind, and the module's
     * display then appends a second copy of it. Every display cycle would add another, so the
     * item accumulates duplicated lore.
     * <p>
     * A line that was already on the item and was written again by a module is therefore a
     * leftover of a failed revert, and the earlier copies are dropped. Lines no module wrote
     * are left alone, so lore belonging to other plugins is untouched, and lines are compared
     * by their legacy form so that copies written in different shapes still match.
     *
     * @param before The lore as it was after reverting, before any module displayed.
     * @param after  The lore after every module displayed.
     * @return The lore to set, or null if nothing needs removing.
     */
    @Nullable
    static List<Component> withoutStaleLines(@NotNull final List<Component> before,
                                             @NotNull final List<Component> after) {
        if (before.isEmpty() || after.isEmpty()) {
            return null;
        }

        Map<String, Integer> beforeCounts = countByLine(before);
        Map<String, Integer> afterCounts = countByLine(after);

        // How many copies of each line to drop: a line the modules wrote again, capped at the
        // number of copies that were already there.
        Map<String, Integer> toRemove = new HashMap<>();

        for (Map.Entry<String, Integer> entry : beforeCounts.entrySet()) {
            int added = afterCounts.getOrDefault(entry.getKey(), 0) - entry.getValue();

            if (added > 0) {
                toRemove.put(entry.getKey(), Math.min(entry.getValue(), added));
            }
        }

        if (toRemove.isEmpty()) {
            return null;
        }

        List<Component> lore = new ArrayList<>(after.size());

        // Modules append, so the stale copies are the earliest ones.
        for (Component line : after) {
            String legacy = StringUtils.toLegacy(line);
            int remaining = toRemove.getOrDefault(legacy, 0);

            if (remaining > 0) {
                toRemove.put(legacy, remaining - 1);
                continue;
            }

            lore.add(line);
        }

        return lore;
    }

    /**
     * Count lore lines by their legacy form, so that lines written in different component
     * shapes still compare equal.
     *
     * @param lore The lore.
     * @return The number of times each line appears.
     */
    @NotNull
    private static Map<String, Integer> countByLine(@NotNull final List<Component> lore) {
        Map<String, Integer> counts = new HashMap<>();

        for (Component line : lore) {
            counts.merge(StringUtils.toLegacy(line), 1, Integer::sum);
        }

        return counts;
    }

    /**
     * Utility class, cannot be instantiated.
     */
    private DisplayLines() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
