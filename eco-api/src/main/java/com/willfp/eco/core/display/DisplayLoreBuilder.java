package com.willfp.eco.core.display;

import com.willfp.eco.util.StringUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.jetbrains.annotations.NotNull;

/**
 * The lore of an item as display modules build it.
 */
final class DisplayLoreBuilder implements DisplayLore {
    /**
     * The lines, in order.
     */
    private final List<LoreNode> nodes;

    /**
     * Create a builder from nodes.
     *
     * @param nodes The nodes.
     */
    DisplayLoreBuilder(@NotNull final List<LoreNode> nodes) {
        this.nodes = new ArrayList<>(nodes);
    }

    /**
     * Create a builder from lore that no module has touched yet.
     *
     * @param lore The lore.
     * @return The builder.
     */
    static DisplayLoreBuilder ofForeign(@NotNull final List<Component> lore) {
        return new DisplayLoreBuilder(lore.stream().map(LoreNode::foreign).toList());
    }

    @Override
    public void prepend(@NotNull final List<? extends ComponentLike> lines) {
        this.nodes.addAll(0, toNodes(lines));
    }

    @Override
    public void append(@NotNull final List<? extends ComponentLike> lines) {
        this.nodes.addAll(toNodes(lines));
    }

    @Override
    @NotNull
    public List<Component> getForeignLines() {
        return this.nodes.stream()
                .filter(node -> !node.display())
                .map(LoreNode::component)
                .toList();
    }

    /**
     * The nodes, in order.
     *
     * @return The nodes.
     */
    @NotNull
    List<LoreNode> getNodes() {
        return List.copyOf(this.nodes);
    }

    /**
     * The lore to put on the item.
     *
     * @param legacyPrefix If display lines should start with the legacy prefix.
     * @return The lore.
     */
    @NotNull
    List<Component> render(final boolean legacyPrefix) {
        return this.nodes.stream()
                .map(node -> node.display() ? DisplayLines.mark(node.component(), legacyPrefix) : node.component())
                .toList();
    }

    /**
     * The positions of display lines in the rendered lore.
     *
     * @return The indices.
     */
    int[] getDisplayIndices() {
        List<Integer> indices = new ArrayList<>();

        for (int index = 0; index < this.nodes.size(); index++) {
            if (this.nodes.get(index).display()) {
                indices.add(index);
            }
        }

        return indices.stream().mapToInt(Integer::intValue).toArray();
    }

    /**
     * Remove display lines that a failed revert left behind as foreign lines.
     * <p>
     * A foreign line that starts with the legacy prefix and reads the same as a display
     * line is a leftover copy, and one is removed for each display line that matches it. Lines
     * without the prefix belong to other plugins and are never removed.
     */
    void removeStaleLines() {
        Map<String, Integer> displayCounts = new HashMap<>();

        for (LoreNode node : this.nodes) {
            if (node.display()) {
                displayCounts.merge(StringUtils.toLegacy(node.component()), 1, Integer::sum);
            }
        }

        Iterator<LoreNode> iterator = this.nodes.iterator();

        while (iterator.hasNext()) {
            LoreNode node = iterator.next();

            if (node.display()) {
                continue;
            }

            String legacy = StringUtils.toLegacy(node.component());

            if (!legacy.startsWith(DisplayLines.LEGACY_PREFIX)) {
                continue;
            }

            String content = legacy.substring(DisplayLines.LEGACY_PREFIX.length());
            int remaining = displayCounts.getOrDefault(content, 0);

            if (remaining > 0) {
                iterator.remove();
                displayCounts.put(content, remaining - 1);
            }
        }
    }

    private static List<LoreNode> toNodes(@NotNull final List<? extends ComponentLike> lines) {
        return lines.stream()
                .map(line -> LoreNode.display(line.asComponent()))
                .toList();
    }
}
